package com.koenigmed.luomanager.toothpick.provider

import android.content.Context
import com.koenigmed.luomanager.BuildConfig
import com.koenigmed.luomanager.data.repository.prefs.IPrefsRepository
import com.koenigmed.luomanager.data.server.interceptor.AuthHeaderInterceptor
import com.koenigmed.luomanager.data.server.interceptor.ErrorResponseInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider

class OkHttpClientProvider @Inject constructor(
        private val authData: IPrefsRepository,
        private val context: Context
) : Provider<OkHttpClient> {

    override fun get() = with(OkHttpClient.Builder()) {
        connectTimeout(60, TimeUnit.SECONDS)
        readTimeout(60, TimeUnit.SECONDS)

        addNetworkInterceptor(AuthHeaderInterceptor(authData))
        addInterceptor(ErrorResponseInterceptor())
        //addInterceptor(FakeInterceptor(context))
        if (BuildConfig.DEBUG) {
            addNetworkInterceptor(
                    HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            )
            //addNetworkInterceptor(CurlLoggingInterceptor())
        }
        build()
    }
}