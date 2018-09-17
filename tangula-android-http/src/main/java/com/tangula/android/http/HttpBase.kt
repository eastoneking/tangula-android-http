package com.tangula.android.http

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.tangula.android.utils.UiThreadUtils
import com.tangula.utils.JsonUtils
import com.tangula.utils.function.BiConsumer
import com.tangula.utils.function.Consumer
import com.tangula.utils.function.Supplier
import okhttp3.*
import org.apache.commons.lang3.StringUtils
import java.io.IOException
import java.lang.Exception
import java.lang.reflect.Modifier


/**
 * Http工具类.
 * <p>使用这个类时，请继承这个类.类中的方法都是<code>protected</code>.这意味着必须对本类进行业务封装，否则不能使用其提供的Http访问接口</p>
 */
abstract class HttpBase {


    @Suppress("UNCHECKED_CAST")
    companion object {

        @JvmStatic
        var USER_ID_SUPPLIER: Supplier<String> = Supplier { "" }

        var FUNC_LOG_VERB:Consumer<String> = Consumer { it->
            println("[tag:http] $it")
        }

        var FUNC_LOG_WARN: BiConsumer<String, Throwable?> = BiConsumer { msg, e->
            println("[tag:http] $msg")
            e?.printStackTrace()
        }

        var FUNC_LOG_ERROR: BiConsumer<String, Throwable?> = BiConsumer { msg, e->
            println("[tag:http] $msg")
            e?.printStackTrace()
        }

        var FUNC_SHOW_MESSAGE:Consumer<String> = Consumer { it->
            println("[tag:http] $it")
        }

        @JvmStatic
        val MEDIA_JSON = MediaType.parse("application/json; charset=utf-8")

        @JvmStatic
        var  URL_PREFIX:String = ""

        @JvmStatic
        val METHOD_GET = { url: String, param: Any? ->
            val b = HttpUrl.get(url).newBuilder()

            for (prop in (param ?: object {}).javaClass.declaredMethods) {
                var name = prop.name
                if (StringUtils.startsWith(prop.name, "get") && prop.modifiers and Modifier.PUBLIC > 0) {
                    val value: Any? = prop.invoke(param)
                    if (value != null) {
                        name = name.drop(3)
                        name = name.replace(name[0], name[0].toLowerCase(), false)
                        b.addQueryParameter(name, value.toString())
                    }
                }
            }

            val req = Request.Builder().url(b.build())
            req.addHeader("auth", USER_ID_SUPPLIER.get())
            req.get().build()
        }

        @JvmStatic
        val METHOD_POST_JSON = { url: String, param: Any? ->
            val req = Request.Builder().url(url)
            req.addHeader("auth", USER_ID_SUPPLIER.get())
            val body = RequestBody.create(MEDIA_JSON, JsonUtils.toJson(param ?: object {}))
            req.post(body).build()
        }

        @JvmStatic
        val METHOD_PUT_JSON = { url: String, param: Any? ->
            val req = Request.Builder().url(url)
            req.addHeader("auth", USER_ID_SUPPLIER.get())
            val body = RequestBody.create(MEDIA_JSON, JsonUtils.toJson(param ?: object {}))
            req.put(body).build()
        }

        @JvmStatic
        val METHOD_DELETE_JSON = { url: String, param: Any? ->
            val req = Request.Builder().url(url)
            req.addHeader("auth", USER_ID_SUPPLIER.get())
            val body = RequestBody.create(MEDIA_JSON, JsonUtils.toJson(param ?: object {}))
            req.delete(body).build()
        }

        val METHOD_COMMON_HTTP_FAIL_RESP: (Response, Call) -> Unit = { res: Response, call: Call ->
            call.request().apply {
                FUNC_LOG_VERB.accept("wrong response, req:" + method() +" " + url() +", resp:"+ res.toString()+".")
            }
            FUNC_LOG_VERB.accept(res.toString())
        }

        val METHOD_COMMON_HTTP_FAIL: (IOException, Call) -> Unit = { ex: IOException, call: Call ->
            call.request().apply {
                FUNC_LOG_ERROR.accept("request fail, req:" + method() +" "+ url() +"", ex)
            }
        }

        val METHOD_COMMON_BIZ_FAIL: (BizResponse<Any>?, Response, Call) -> Unit = { biz: BizResponse<Any>?, resp: Response, call: Call ->
            call.request().apply {
                FUNC_LOG_VERB.accept("business fail, req:" + method() +" " + url() +", resp:"+ resp.toString()+".")
            }
            UiThreadUtils.runInUiThread(Runnable{
                FUNC_SHOW_MESSAGE.accept(biz?.message)
            })
        }

        @JvmStatic
        fun loadImage(imageId: String, cb: Consumer<Bitmap?>) {
            val url = "$URL_PREFIX/viewImageController/view/$imageId"

            val url_fac = HttpUrl.get(url).newBuilder()
            val builder = Request.Builder().url(url_fac.build())
            builder.addHeader("auth", USER_ID_SUPPLIER.get())
            val request = builder.get().build()

            val client = OkHttpClient()
            client.newCall(
                    request
            ).enqueue(
                    object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            FUNC_LOG_ERROR.accept(e.localizedMessage, e)
                        }

                        @Throws(IOException::class)
                        override fun onResponse(call: Call, response: Response) {
                            if (response.isSuccessful) {
                                val body = response.body()
                                if (body != null) {
                                    val bytes = body.bytes()
                                    try {
                                        cb.accept(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
                                    } catch (e: Exception) {
                                        FUNC_LOG_ERROR.accept("image $url response not a picture", e)
                                        cb.accept(null)
                                    }
                                } else {
                                    FUNC_LOG_WARN.accept("image $url response not have a body",null)
                                    cb.accept(null)
                                }
                            } else {
                                FUNC_LOG_WARN.accept("image $url response not success",null)
                                cb.accept(null)
                            }
                        }
                    }
            )

        }

        @JvmStatic
        fun <T, R> getBizSucessA(url: String, param: T, respType: Class<R>, onSuccess: (R?) -> Unit) {
            getBizHttpSucessA(url, param, respType, onSuccess, METHOD_COMMON_BIZ_FAIL as (BizResponse<R>?, Response, Call) -> Unit)
        }

        @JvmStatic
        fun <T, R> postBizSucessA(url: String, param: T, respType: Class<R>, onSuccess: (R?) -> Unit) {
            postBizHttpSucessA(url, param, respType, onSuccess, METHOD_COMMON_BIZ_FAIL as (BizResponse<R>?, Response, Call) -> Unit)
        }

        /**
         * 列表数据
         */
        @JvmStatic
        fun <T, R> getBizListSucessA(url: String, param: T, itemType: Class<R>, onSuccess: Consumer<PagingBody<R>>) {
            getBizHttpSucessA(url, param, PagingBody::class.java, { body ->

                val res = PagingBody<R>().apply {
                    pageIndex = body!!.pageIndex
                    pageSize = body.pageSize
                    total = body.total
                }

                (body?.items as ArrayList<Object>).forEach {
                    val cur = JsonUtils.fromJson(JsonUtils.toJson(it), itemType)
                    (res.items as MutableList).add(cur)
                }

                onSuccess.accept(res)

            }, METHOD_COMMON_BIZ_FAIL as (BizResponse<PagingBody<*>>?, Response, Call) -> Unit)
        }


        /**
         * 列表数据
         */
        @JvmStatic
        fun <T, R> postBizListSucessA(url: String, param: T, itemType: Class<R>, onSuccess: Consumer<PagingBody<R>>) {
            postBizHttpSucessA(url, param, PagingBody::class.java, { body ->

                val res = PagingBody<R>().apply {
                    pageIndex = body!!.pageIndex
                    pageSize = body.pageSize
                    total = body.total
                }

                (body?.items as ArrayList<Object>).forEach {
                    val cur = JsonUtils.fromJson(JsonUtils.toJson(it), itemType)
                    (res.items as MutableList).add(cur)
                }

                onSuccess.accept(res)

            }, METHOD_COMMON_BIZ_FAIL as (BizResponse<PagingBody<*>>?, Response, Call) -> Unit)
        }


        @JvmStatic
        fun <T, R> putBizSucessA(url: String, param: T, respType: Class<R>, onSuccess: (R?) -> Unit) {
            putBizHttpSucessA(url, param, respType, onSuccess, METHOD_COMMON_BIZ_FAIL as (BizResponse<R>?, Response, Call) -> Unit)
        }

        @JvmStatic
        fun <T, R> deleteBizSucessA(url: String, param: T, respType: Class<R>, onSuccess: (R?) -> Unit) {
            deleteBizHttpSucessA(url, param, respType, onSuccess, METHOD_COMMON_BIZ_FAIL as (BizResponse<R>?, Response, Call) -> Unit)
        }


        @JvmStatic
        fun <T, R> getBizHttpSucessA(url: String, param: T, respType: Class<R>, onSuccess: (R?) -> Unit, onBizFail: (BizResponse<R>?, Response, Call) -> Unit) {
            getBizA(url, param, respType, onSuccess, onBizFail, METHOD_COMMON_HTTP_FAIL_RESP, METHOD_COMMON_HTTP_FAIL)
        }

        @JvmStatic
        fun <T, R> postBizHttpSucessA(uri: String, param: T, respType: Class<R>, onSuccess: (R?) -> Unit, onBizFail: (BizResponse<R>?, Response, Call) -> Unit) {
            postBizA(uri, param, respType, onSuccess, onBizFail, METHOD_COMMON_HTTP_FAIL_RESP, METHOD_COMMON_HTTP_FAIL)
        }

        @JvmStatic
        fun <T, R> putBizHttpSucessA(url: String, param: T, respType: Class<R>, onSuccess: (R?) -> Unit, onBizFail: (BizResponse<R>?, Response, Call) -> Unit) {
            putBizA(url, param, respType, onSuccess, onBizFail, METHOD_COMMON_HTTP_FAIL_RESP, METHOD_COMMON_HTTP_FAIL)
        }


        @JvmStatic
        fun <T, R> deleteBizHttpSucessA(url: String, param: T, respType: Class<R>, onSuccess: (R?) -> Unit, onBizFail: (BizResponse<R>?, Response, Call) -> Unit) {
            deleteBizA(url, param, respType, onSuccess, onBizFail, METHOD_COMMON_HTTP_FAIL_RESP, METHOD_COMMON_HTTP_FAIL)
        }


        @JvmStatic
        fun <T, R> getBizA(url: String, param: T, respType: Class<R>, onSuccess: (R?) -> Unit, onBizFail: (BizResponse<R>?, Response, Call) -> Unit,
                           onFailResponse: (Response, Call) -> Unit, onHttpFail: (ex: IOException, call: Call) -> Unit) {
            httpBizA(HttpBase.Companion::getA, url, param, respType, onSuccess, onBizFail, onFailResponse, onHttpFail)
        }

        @JvmStatic
        fun <T, R> postBizA(url: String, param: T, respType: Class<R>, onSuccess: (R?) -> Unit, onBizFail: (BizResponse<R>?, Response, Call) -> Unit,
                            onFailResponse: (Response, Call) -> Unit, onHttpFail: (ex: IOException, call: Call) -> Unit) {
            httpBizA(HttpBase.Companion::postJsonA, url, param, respType, onSuccess, onBizFail, onFailResponse, onHttpFail)
        }

        @JvmStatic
        fun <T, R> putBizA(url: String, param: T, respType: Class<R>, onSuccess: (R?) -> Unit, onBizFail: (BizResponse<R>?, Response, Call) -> Unit,
                           onFailResponse: (Response, Call) -> Unit, onHttpFail: (ex: IOException, call: Call) -> Unit) {
            httpBizA(HttpBase.Companion::putJsonA, url, param, respType, onSuccess, onBizFail, onFailResponse, onHttpFail)
        }


        @JvmStatic
        fun <T, R> deleteBizA(url: String, param: T, respType: Class<R>, onSuccess: (R?) -> Unit, onBizFail: (BizResponse<R>?, Response, Call) -> Unit,
                              onFailResponse: (Response, Call) -> Unit, onHttpFail: (ex: IOException, call: Call) -> Unit) {
            httpBizA(HttpBase.Companion::deleteJsonA, url, param, respType, onSuccess, onBizFail, onFailResponse, onHttpFail)
        }


        @JvmStatic
        private fun <T, R> httpBizA(func: (url: String, param: T, respType: Class<BizResponse<R>>, onSuccess: (BizResponse<R>?, Response, Call) -> Unit,
                                           onFailResponse: (Response, Call) -> Unit, onHttpFail: (IOException, Call) -> Unit) -> Unit,
                                    url: String, param: T, respType: Class<R>, onSuccess: (R?) -> Unit, onBizFail: (BizResponse<R>?, Response, Call) -> Unit,
                                    onFailResponse: (Response, Call) -> Unit, onHttpFail: (IOException, Call) -> Unit
        ) {
            return func(URL_PREFIX + url, param, BizResponse::class.java as Class<BizResponse<R>>, { biz: BizResponse<R>?, resp: Response, call: Call ->
                if (biz != null) {
                    if (biz.status == 0) {
                        FUNC_LOG_VERB.accept("biz.body" + JsonUtils.toJson(biz.body))  //TODO would remove
                        FUNC_LOG_VERB.accept( "biz.body type:" + respType.name) //TODO would remove
                        if (StringUtils.equalsAny(respType.name, "boolean", "java.lang.Boolean")) {
                            onSuccess(biz.body as R)
                        } else {
                            onSuccess(biz.body)
                        }
                    } else {
                        onBizFail(BizResponse<R>(biz.status, biz.statusText, biz.message, null), resp, call)
                    }
                } else {
                    onFailResponse(resp, call)
                }
            }, onFailResponse, onHttpFail)
        }

        @JvmStatic
        fun <T, R> getA(url: String, param: T, respType: Class<R>, onSuccess: (R?, Response, Call) -> Unit,
                        onFailResponse: (Response, Call) -> Unit, onHttpFail: (IOException, Call) -> Unit) {
            sendHttpJsonARespJson(METHOD_GET, url, param, respType, onSuccess, onFailResponse, onHttpFail)
        }

        @JvmStatic
        fun <T, R> postJsonA(url: String, param: T, respType: Class<R>, onSuccess: (R?, Response, Call) -> Unit,
                             onFailResponse: (Response, Call) -> Unit, onHttpFail: (ex: IOException, call: Call) -> Unit) {
            sendHttpJsonARespJson(METHOD_POST_JSON, url, param, respType, onSuccess, onFailResponse, onHttpFail)
        }

        @JvmStatic
        fun <T, R> putJsonA(url: String, param: T, respType: Class<R>, onSuccess: (R?, Response, Call) -> Unit,
                            onFailResponse: (Response, Call) -> Unit, onHttpFail: (ex: IOException, call: Call) -> Unit) {
            sendHttpJsonARespJson(METHOD_PUT_JSON, url, param, respType, onSuccess, onFailResponse, onHttpFail)
        }

        @JvmStatic
        fun <T, R> deleteJsonA(url: String, param: T, respType: Class<R>, onSuccess: (R?, Response, Call) -> Unit,
                               onFailResponse: (Response, Call) -> Unit, onHttpFail: (ex: IOException, call: Call) -> Unit) {
            sendHttpJsonARespJson(METHOD_DELETE_JSON, url, param, respType, onSuccess, onFailResponse, onHttpFail)
        }


        @JvmStatic
        fun <T, R> sendHttpJsonARespJson(method: (url: String, Any?) -> Request, url: String, param: T, respType: Class<R>, onSuccess: (R?, Response, Call) -> Unit,
                                         onFailResponse: (Response, Call) -> Unit, onHttpFail: (ex: IOException, call: Call) -> Unit) {
            val client = OkHttpClient()
            client.newCall(
                    method(url, param)
            ).enqueue(
                    object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            onHttpFail(e, call)
                        }

                        @Throws(IOException::class)
                        override fun onResponse(call: Call, response: Response) {
                            if (response.isSuccessful||response.code() in 200 .. 299) {
                                val body = response.body()
                                if (body != null) {
                                    val str = body.string()
                                    FUNC_LOG_VERB.accept("http resp body:$str")
                                    FUNC_LOG_VERB.accept("http resp body type:" + respType.name)
                                    val obj = JsonUtils.fromJson(str, respType)
                                    onSuccess(obj, response, call)
                                } else {
                                    onSuccess(null, response, call)
                                }
                            } else {
                                onFailResponse(response, call)
                            }
                        }
                    }
            )
        }
    }


}
