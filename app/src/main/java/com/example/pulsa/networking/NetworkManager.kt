package com.example.pulsa.networking

import android.content.ContentResolver
import android.util.Log
import com.example.pulsa.activities.BaseLayoutActivity
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.time.LocalDateTime


class NetworkManager {
    private val LOG = "OkHttp"
    private val URL = "https://pulsa-rest-production.up.railway.app/api/v1"
    private val client: OkHttpClient = OkHttpClient()
    private lateinit var message: String

    fun get(activity: BaseLayoutActivity, map: HashMap<String, Any>) {
        val request: Request = Request.Builder()
            .url(URL + map["url"])
            .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                message = e.message.toString()
                Log.e(LOG, message)
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

    fun post(activity: BaseLayoutActivity, map: HashMap<String, Any>) {
        val requestBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("title", map["title"] as String)
            .addFormDataPart("text", map["text"] as String)
            .addFormDataPart("slug", map["slug"] as String)
            .addFormDataPart("image", "image1.jpg",
                (map["image"] as File).asRequestBody((map["imageType"] as String).toMediaTypeOrNull()))
            .addFormDataPart("recording", "recording1.mp3",
                (map["recording"] as File).asRequestBody((map["recordingType"] as String).toMediaTypeOrNull()))
            .addFormDataPart("audio", "audio1.mp3",
                (map["audio"] as File).asRequestBody((map["audioType"] as String).toMediaTypeOrNull()))
            .build()
        val request: Request = Request.Builder()
            .url(URL + map["url"])
            .post(requestBody)
            .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                message = e.message.toString()
                Log.e(LOG, message)
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
                    activity.runOnUiThread{activity.resolvePost(content)}
                } else {
                    println(message)
                }
            }
        })
    }
}
