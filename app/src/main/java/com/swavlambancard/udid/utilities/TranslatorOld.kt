package com.swavlambancard.udid.utilities

import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONArray
import java.io.IOException

class Translator {

    companion object {
        fun getTranslation(
            selectedLangCode: String = "en",
            targetLangCode: String,
            inputString: String,
            completionHandler: (String) -> Unit
        ) {
            val urlBuilder = "https://translate.googleapis.com/translate_a/single".toHttpUrlOrNull()
                ?.newBuilder()
            urlBuilder?.addQueryParameter("client", "gtx")
            urlBuilder?.addQueryParameter("sl", selectedLangCode)
            urlBuilder?.addQueryParameter("tl", targetLangCode)
            urlBuilder?.addQueryParameter("dt", "t")
            urlBuilder?.addQueryParameter("q", inputString)

            val url = urlBuilder?.build() ?: return

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    completionHandler(inputString)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    val queryParams = url.queryParameterNames.associateWith { url.queryParameter(it) ?: "" }

                    if (!responseBody.isNullOrEmpty()) {
                        try {
                            val responseJson = JSONArray(responseBody)
                            val firstObject = responseJson.getJSONArray(0)
                            val secondObject = firstObject.getJSONArray(0)
                            val resultString = secondObject.getString(0) ?: queryParams["q"] ?: ""
                            completionHandler(resultString)
                        } catch (e: Exception) {
                            completionHandler(queryParams["q"] ?: "")
                        }
                    } else {
                        completionHandler(queryParams["q"] ?: "")
                    }
                }
            })
        }
    }
}