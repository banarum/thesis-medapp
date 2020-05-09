package com.koenigmed.luomanager.data.server.interceptor

import android.content.Context
import android.text.TextUtils
import com.koenigmed.luomanager.BuildConfig
import okhttp3.*
import timber.log.Timber
import java.io.IOException

/**
 * Fake Api Responses - uses mock json assets only for debug
 */
class FakeInterceptor(private val context: Context) : Interceptor {
    private var code = 200

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response
        if (BuildConfig.DEBUG) {
            var responseString = ""
            val url = chain.request().url()
            Timber.i("intercept $url")
            try {
                responseString = getFakeResponseString(url.toString())
                if (TextUtils.isEmpty(responseString)) {
                    return chain.proceed(chain.request())
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
            response = buildFakeHtmlResponse(chain, responseString, code)
        } else {
            response = chain.proceed(chain.request())
        }
        return response
    }

    @Throws(IOException::class)
    private fun getFakeResponseString(url: String): String {
        var responseString = ""
        when {
            url.endsWith("token") -> {
                //responseString = AssetsHelper.getStringFromFile(context, "api_mock/auth_response.json");
            }
        }
        return responseString
    }

    private fun buildFakeResponse(chain: Interceptor.Chain, responseString: String, code: Int): Response {
        /*if (chain.request().url().toString().endsWith("v1/order")) {
            return buildFakeHtmlResponse(chain, responseString, code);
        } */
        return Response.Builder()
                .code(code)
                .message(responseString)
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .body(ResponseBody.create(MediaType.parse("application/json"), responseString.toByteArray()))
                .addHeader("content-type", "application/json")
                .build()
    }

    private fun buildFakeHtmlResponse(chain: Interceptor.Chain, responseString: String, code: Int): Response {
        return Response.Builder()
                .code(code)
                .message(responseString)
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .body(ResponseBody.create(MediaType.parse("text/html"), responseString.toByteArray()))
                .addHeader("content-type", "text/html")
                .build()
    }
}