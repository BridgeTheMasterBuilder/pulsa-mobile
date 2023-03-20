package com.example.pulsa.networking

import android.util.Log
import com.example.pulsa.activities.BaseLayoutActivity
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException
import java.time.LocalDateTime


class NetworkManager {
    private val LOG = "OkHttp"
    private val URL = "https://pulsa-rest-production.up.railway.app/api/v1"
    private val client: OkHttpClient = OkHttpClient()
    private lateinit var message: String

    public fun get(activity: BaseLayoutActivity, map: HashMap<String, Any>) {
        val request: Request = Request.Builder()
            .url(URL + map["url"])
            .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                fun run() {
                    message = e.message.toString()
                    Log.e(LOG, message)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                message = response.message
                Log.i(LOG, message)

                val gson = GsonBuilder().registerTypeAdapter(
                    LocalDateTime::class.java,
                    JsonDeserializer<Any?> {
                            json, typeOfT, context -> LocalDateTime.parse(json.asString)
                    })
                    .create()
                val type = (map["type"] as TypeToken<*>)
                val content = gson.fromJson(response.body?.string(), type)
                if (response.isSuccessful) {
                    activity.runOnUiThread{activity.resolveGet(content)}
                } else {
                    println(message)
                }
            }
        })
    }
}
