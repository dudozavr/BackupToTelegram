package com.mindorks.framework.backuptotelegram.data.network.telegram

import android.os.SystemClock
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class TelegramNetworkInterceptor : Interceptor {

    companion object {
        private const val TAG = "TelegramNetworkInterceptor"
        private const val DELAY = 3000L
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url.toString()
        Log.d(TAG, "url that was requested: $url")
        SystemClock.sleep(DELAY)
        return chain.proceed(chain.request())
    }
}