package com.tangula.android.http

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.tangula.android.http.HttpBase.Companion.postBizSucessA
import com.tangula.utils.BitmapUtils
import com.tangula.utils.function.Consumer
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException


class ImageHttpUtils {

    companion object {

        /**
         * 上传图片
         */
        @JvmStatic
        fun uploadImage(url: String, img: Bitmap, callback: Consumer<String>) {
            postBizSucessA(url, UploadImageForm(BitmapUtils.bitmapToBase64(img)), UploadImageResp::class.java) { o ->
                callback.accept(o?.imageId)
            }
        }


        @JvmStatic
        fun loadImage(view: ImageView, url: String, placeHolder: Int?, errorHolder: Int?) {
            loadImage(view.context, view, url, placeHolder, errorHolder, Runnable {})
        }

        @JvmStatic
        fun loadImage(view: ImageView, url: String, placeHolder: Int?, errorHolder: Int?, onSuccess: Runnable) {
            loadImage(view.context, view, url, placeHolder, errorHolder, onSuccess)
        }

        @JvmStatic
        fun loadImage(view: ImageView, url: String, placeHolder: Int?, errorHolder: Int?, onSuccess: Runnable, onFail: Runnable) {
            loadImage(view.context, view, url, placeHolder, errorHolder, onSuccess, onFail)
        }

        @JvmStatic
        fun loadImage(context: Context?, view: ImageView, url: String, placeHolder: Int?, errorHolder: Int?, onSuccess: Runnable) {
            loadImage(context, view, url, placeHolder, errorHolder, onSuccess, Runnable {})
        }

        @JvmStatic
        fun loadImage(view: ImageView, url: String, placeHolder: Drawable?, errorHolder: Drawable?) {
            loadImage(view.context, view, url, placeHolder, errorHolder, Runnable {})
        }

        @JvmStatic
        fun loadImage(view: ImageView, url: String, placeHolder: Drawable?, errorHolder: Drawable?, onSuccess: Runnable) {
            loadImage(view.context, view, url, placeHolder, errorHolder, onSuccess)
        }

        @JvmStatic
        fun loadImage(view: ImageView, url: String, placeHolder: Drawable?, errorHolder: Drawable?, onSuccess: Runnable, onFail: Runnable) {
            loadImage(view.context, view, url, placeHolder, errorHolder, onSuccess, onFail)
        }

        @JvmStatic
        fun loadImage(context: Context?, view: ImageView, url: String, placeHolder: Drawable?, errorHolder: Drawable?, onSuccess: Runnable) {
            loadImage(context, view, url, placeHolder, errorHolder, onSuccess, Runnable {})
        }

        /**
         * 显示图片.
         */
        @JvmStatic
        fun loadImage(context: Context?, view: ImageView, url: String, placeHolder: Int?, errorHolder: Int?, onSuccess: Runnable, onFail: Runnable) {
            val client = OkHttpClient.Builder()
                    .addInterceptor(object : Interceptor {
                        @Throws(IOException::class)
                        override fun intercept(chain: Interceptor.Chain): Response {
                            val newRequest = chain.request().newBuilder()
                                    .addHeader("auth", HttpBase.USER_ID_SUPPLIER.get())
                                    .build()
                            return chain.proceed(newRequest)
                        }
                    })
                    .build()

            val picasso = Picasso.Builder(context ?: view.context)
                    .downloader(OkHttp3Downloader(client))
                    .build()

            val req = picasso.load(url)
            when (placeHolder != null) {
                true -> {
                    req.placeholder(placeHolder!!)
                }
            }
            when (errorHolder != null) {
                true -> {
                    req.error(errorHolder!!)
                }
            }

            view.post{
                req.into(view, object : Callback {
                    override fun onSuccess() {
                        onSuccess.run()
                    }

                    override fun onError() {
                        onFail.run()
                    }
                })
            }
        }

        /**
         * 显示图片.
         */
        @JvmStatic
        fun loadImage(context: Context?, view: ImageView, url: String, placeHolder: Drawable?, errorHolder: Drawable?, onSuccess: Runnable, onFail: Runnable) {
            val client = OkHttpClient.Builder()
                    .addInterceptor(object : Interceptor {
                        @Throws(IOException::class)
                        override fun intercept(chain: Interceptor.Chain): Response {
                            val newRequest = chain.request().newBuilder()
                                    .addHeader("auth", HttpBase.USER_ID_SUPPLIER.get())
                                    .build()
                            return chain.proceed(newRequest)
                        }
                    })
                    .build()

            val picasso = Picasso.Builder(context ?: view.context)
                    .downloader(OkHttp3Downloader(client))
                    .build()

            val req = picasso.load(url)
            when (placeHolder != null) {
                true -> {
                    req.placeholder(placeHolder)
                }
            }
            when (errorHolder != null) {
                true -> {
                    req.error(errorHolder)
                }
            }

            view.post{
                req.into(view, object : Callback {
                    override fun onSuccess() {
                        onSuccess.run()
                    }

                    override fun onError() {
                        onFail.run()
                    }
                })
            }
        }
    }

}