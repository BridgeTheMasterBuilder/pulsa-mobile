package com.example.pulsa.networking

import android.content.Context
import okhttp3.*
import okhttp3.internal.threadFactory
import java.io.IOException


class NetworkManager {
    private val URL = "http://10.0.2.2:8080/"
    private val client: OkHttpClient = OkHttpClient()

    public fun getPosts(): String? {
        var result: String? = null

        val request: Request = Request.Builder()
            .url(URL)
            .build()

        val response = client.newCall(request).execute()
        return response.body?.string()
    }

}
