package com.swavlambancard.udid.services

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/*This class used to Authentication in service*/
internal class AuthenticationInterceptor(private val authToken: String) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val builder = original.newBuilder()
                .header("Authorization", "Bearer $authToken")
        val request = builder.build()
        return chain.proceed(request)
    }
}
