package com.example.pulsa.networking

import android.util.Log
import com.example.pulsa.activities.MainActivity
import com.example.pulsa.objects.Post
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException
import java.time.LocalDateTime


class NetworkManager {
    private val LOG = "OkHttp"
    private val URL = "http://10.0.2.2:8080/"
    private val client: OkHttpClient = OkHttpClient()
    private lateinit var message: String

    public fun getPosts(activity: MainActivity) {
        val request: Request = Request.Builder()
            .url(URL)
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
                val postsType = object : TypeToken<List<Post>>(){}.type
                val posts = gson.fromJson<MutableList<Post>?>(response.body?.string(), postsType)
                    .toMutableList()
                if (response.isSuccessful) {
                    activity.runOnUiThread{activity.displayPosts(posts)}
                } else {
                    println(message)
                }
            }
        })
        println("Called")
    }
}
