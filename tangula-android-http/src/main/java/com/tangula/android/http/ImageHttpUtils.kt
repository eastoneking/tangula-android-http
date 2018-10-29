package com.tangula.android.http

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.tangula.android.utils.BitmapUtils
import okhttp3.OkHttpClient

@Suppress("UNUSED", "MemberVisibilityCanBePrivate")
class ImageHttpUtils {

    companion object {

        /**
         * 上传图片
         */
        @JvmStatic
        fun uploadImage(url: String, img: Bitmap, callback: (String)->Unit) {
            HttpBaseKotlin.postBizSucessA(url, UploadImageForm(BitmapUtils.bitmapToBase64(img)), UploadImageResp::class.java) { o ->
                o?.imageId?.also(callback)
            }
        }

        /**
         * 显示图片.
         */
        @JvmStatic
        fun loadImage(context: Context?, view: ImageView, url: String, placeHolder: Int?, errorHolder: Int?, onSuccess: Runnable?, onFail: Runnable?) {
            val client = OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val newRequest = chain.request().newBuilder()
                                .addHeader("auth", HttpBaseKotlin.USER_ID_SUPPLIER())
                                .build()
                        chain.proceed(newRequest)
                    }
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

            view.post {
                req.into(view, object : Callback {
                    override fun onSuccess() {
                        onSuccess?.run()
                    }

                    override fun onError() {
                        onFail?.run()
                    }
                })
            }
        }

        /**
         * 显示图片.
         */
        @JvmStatic
        fun loadImage(context: Context?, view: ImageView, url: String, placeHolder: Drawable?, errorHolder: Drawable?, onBeforeRequest: Runnable?, onSuccess: Runnable?, onFail: Runnable?) {
            val client = OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val newRequest = chain.request().newBuilder()
                                .addHeader("auth", HttpBaseKotlin.USER_ID_SUPPLIER())
                                .build()
                        chain.proceed(newRequest)
                    }
                    .addInterceptor { chain ->
                        onBeforeRequest?.run()
                        chain.proceed(chain.request())
                    }
                    .build()

            val picasso = Picasso.Builder(context ?: view.context)
                    .downloader(OkHttp3Downloader(client))
                    .build()

            val req = picasso.load(url)
            when (placeHolder != null) {
                true -> {
                    req.placeholder(placeHolder)
                }
                else -> {
                    req.noPlaceholder()
                }
            }
            when (errorHolder != null) {
                true -> {
                    req.error(errorHolder)
                }
            }


            view.post {
                req.into(view, object : Callback {
                    override fun onSuccess() {
                        onSuccess?.run()
                    }

                    override fun onError() {
                        onFail?.run()
                    }
                })
            }
        }
    }

}