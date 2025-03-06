package com.swavlambancard.udid.utilities

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONArray
import java.io.IOException
import kotlin.coroutines.resume

class Translator {
    companion object {
        suspend fun getTranslation(
            selectedLangCode: String = "en",
            targetLangCode: String,
            inputString: String
        ): String {
            return suspendCancellableCoroutine { continuation ->
                val urlBuilder = "https://translate.googleapis.com/translate_a/single".toHttpUrlOrNull()
                    ?.newBuilder()
                urlBuilder?.addQueryParameter("client", "gtx")
                urlBuilder?.addQueryParameter("sl", selectedLangCode)
                urlBuilder?.addQueryParameter("tl", targetLangCode)
                urlBuilder?.addQueryParameter("dt", "t")
                urlBuilder?.addQueryParameter("q", inputString)

                val url = urlBuilder?.build() ?: return@suspendCancellableCoroutine // it will suspend the coroutine if result is not generated or response is not recieved where as previous version was using a callback to show the result which is in apt as we are hiting an api

                val request = Request.Builder().url(url).get().build()
                val client = OkHttpClient()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        continuation.resume(inputString) // Return input text if translation fails
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseBody = response.body?.string()
                        if (!responseBody.isNullOrEmpty()) {
                            try {
                                val responseJson = JSONArray(responseBody)
                                val firstObject = responseJson.getJSONArray(0)
                                val secondObject = firstObject.getJSONArray(0)
                                val resultString = secondObject.getString(0) ?: inputString
                                continuation.resume(resultString)
                            } catch (e: Exception) {
                                continuation.resume(inputString)
                            }
                        } else {
                            continuation.resume(inputString)
                        }
                    }
                })
            }
        }
    }
}