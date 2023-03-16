package com.example.pulsa.networking

import android.content.Context

class NetworkManager private constructor(context: Context) {
    private val URL = "localhost:8080"
    private val mContext: Context = context.applicationContext
}