package com.koenigmed.luomanager.data.server.interceptor

import com.koenigmed.luomanager.data.repository.prefs.IPrefsRepository
import okhttp3.Interceptor
import okhttp3.Response

class AuthHeaderInterceptor(private val authData: IPrefsRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = authData.token
        var request = chain.request()
        if (token.isNotEmpty()) {
            request = request.newBuilder().addHeader("Authorization", "Bearer " + authData.token).build()
        }
        return chain.proceed(request)
    }
}