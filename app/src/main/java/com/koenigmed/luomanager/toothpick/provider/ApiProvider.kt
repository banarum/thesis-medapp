package com.koenigmed.luomanager.toothpick.provider

import com.google.gson.Gson
import com.koenigmed.luomanager.BuildConfig.BASE_SERVER_URL
import com.koenigmed.luomanager.data.server.MyoApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Provider

class ApiProvider @Inject constructor(
        private val okHttpClient: OkHttpClient,
        private val gson: Gson
) : Provider<MyoApi> {

    override fun get() =
            Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .baseUrl(BASE_SERVER_URL)
                    .build()
                    .create(MyoApi::class.java)
}