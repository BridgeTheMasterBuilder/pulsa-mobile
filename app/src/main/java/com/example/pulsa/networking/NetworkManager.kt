package com.example.pulsa.networking

import android.content.Context
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request

class NetworkManager private constructor(context: Context) {
    private val URL = "localhost:8080"
    private val mContext: Context = context.applicationContext
    private val client: OkHttpClient = OkHttpClient()

    private fun getPosts() {
        val request: Request = Request.Builder()
            .url(URL)
            .build()

        val call: Call = client.newCall(request)
        call.enqueue(Callback)
    }


}