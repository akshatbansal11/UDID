package com.swavlambancard.udid.services

import android.util.Log
import com.swavlambancard.udid.utilities.CONSTANTS.BASE_URL
import com.swavlambancard.udid.utilities.UDID
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object ServiceGeneratorLogin {
    // This is the base Url of the application.
    private val httpClient = OkHttpClient.Builder()
    private val builder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(buildClient())
        .addConverterFactory(GsonConverterFactory.create())

    private var retrofit: Retrofit? = null

    fun <S> createServiceLogin(serviceClass: Class<S>): S {
        val authToken = UDID.getToken()
        Log.e("token", "c $authToken")

        if (!checkEmptyString(authToken)) {
            val interceptor = AuthenticationInterceptor(authToken)
            val logInterceptor = HttpLoggingInterceptor()
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(MyOkHttpInterceptor())
                httpClient.addInterceptor(logInterceptor)
                builder.client(httpClient.build())
                retrofit = builder.build() }
        }
        return retrofit!!.create(serviceClass) }


    private fun buildClient(): OkHttpClient {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(logInterceptor)
            .readTimeout(300, TimeUnit.SECONDS)
            .connectTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .build()
    }

    class MyOkHttpInterceptor internal constructor() : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
                val newRequest = originalRequest.newBuilder()
                    .build()
               return chain.proceed(newRequest)
        }
    }

    private fun checkEmptyString(string: String?): Boolean {
        return string == null || string.trim { it <= ' ' }.isEmpty()
    }
}