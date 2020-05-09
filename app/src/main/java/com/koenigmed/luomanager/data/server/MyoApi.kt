package com.koenigmed.luomanager.data.server

import com.koenigmed.luomanager.data.model.JsonMyoProgram
import com.koenigmed.luomanager.data.model.JsonPulseForm
import com.koenigmed.luomanager.data.model.JsonTokenData
import com.koenigmed.luomanager.data.model.JsonUserData
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MyoApi {
    companion object {
        const val API_PATH = "api"
    }

    @POST("$API_PATH/accounts/register")
    fun register(@Body body: Map<String, @JvmSuppressWildcards Any?>): Single<ResponseBody>

    @POST("$API_PATH/accounts/login")
    fun auth(@Body body: Map<String, String>): Single<JsonTokenData>

    @POST("$API_PATH/accounts/logout")
    fun logout(): Single<ResponseBody>

    @POST(API_PATH + "/accounts/resetPassword")
    fun resetPassword(@Body body: Map<String, @JvmSuppressWildcards Any?>): Single<ResponseBody>

    @GET(API_PATH + "/accounts/userdata")
    fun getUserData(): Single<JsonUserData>

    @POST(API_PATH + "/accounts/userdata")
    fun setUserData(@Body body: Map<String, @JvmSuppressWildcards Any?>): Single<ResponseBody>

    @GET("$API_PATH/myoprograms")
    fun getPrograms(): Single<List<JsonMyoProgram>>

    @GET("$API_PATH/myoprograms/{id}")
    fun getProgram(@Path("id") id: Int): Single<JsonMyoProgram>

    @GET("$API_PATH/myoprograms/{id}")
    fun getProgram(@Path("id") id: Long): Single<JsonMyoProgram>

    @GET("$API_PATH/myoprograms/impulseforms")
    fun getPulseForms() : Single<List<JsonPulseForm>>

}