package com.example.pulsa.networking

import android.util.Log
import com.example.pulsa.activities.BaseLayoutActivity
import com.example.pulsa.utils.UserUtils
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
    // private val URL = "http://10.0.2.2:8080/api/v1/"

    private val URL = "https://pulsa-rest-production.up.railway.app/api/v1/"
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
                    JsonDeserializer<Any?> { json, typeOfT, context ->
                        LocalDateTime.parse(json.asString)
                    })
                    .create()
                val type = (map["type"] as TypeToken<*>)
                val content = gson.fromJson(response.body?.string(), type)
                if (response.isSuccessful) {
                    activity.runOnUiThread { activity.resolveGet(content) }
                } else {
                    println(message)
                }
            }
        })
    }

    fun post(activity: BaseLayoutActivity, map: HashMap<String, Any>) {
        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        val requestHeaderBuilder = Headers.Builder()

        // Post and replies
        map["title"]?.let { requestBodyBuilder.addFormDataPart("title", it as String) }
        map["text"]?.let { requestBodyBuilder.addFormDataPart("text", it as String) }
        (map["image"] as? File)?.let {
            map["imageType"]?.let { imageType ->
                requestBodyBuilder.addFormDataPart(
                    "image",
                    "image1.jpg",
                    it.asRequestBody((imageType as String).toMediaTypeOrNull())
                )
            }
        }
        (map["recording"] as? File)?.let {
            map["recordingType"]?.let { recordingType ->
                requestBodyBuilder.addFormDataPart(
                    "recording",
                    "recording1.mp3",
                    it.asRequestBody((recordingType as String).toMediaTypeOrNull())
                )
            }
        }
        (map["audio"] as? File)?.let {
            map["audioType"]?.let { audioType ->
                requestBodyBuilder.addFormDataPart(
                    "audio",
                    "audio1.mp3",
                    it.asRequestBody((audioType as String).toMediaTypeOrNull())
                )
            }
        }

        // Users
        map["realName"]?.let { requestBodyBuilder.addFormDataPart("realName", it as String) }
        map["username"]?.let { requestBodyBuilder.addFormDataPart("username", it as String) }
        map["email"]?.let { requestBodyBuilder.addFormDataPart("email", it as String) }
        map["password"]?.let { requestBodyBuilder.addFormDataPart("password", it as String) }
        (map["avatar"] as? File)?.let {
            map["avatarType"]?.let { imageType ->
                requestBodyBuilder.addFormDataPart(
                    "avatar",
                    "image1.jpg",
                    it.asRequestBody((imageType as String).toMediaTypeOrNull())
                )
            }
        }

        map["vote"]?.let { requestBodyBuilder.addFormDataPart("", "") }

        // Header
        if (UserUtils.loggedIn()) requestHeaderBuilder.add("Authorization", UserUtils.getJwtToken())

        val requestBody = requestBodyBuilder.build()
        val requestHeader = requestHeaderBuilder.build()

        println("request url: ${URL + map["url"]}")
        println("Request body: ${requestBody}")
        val request: Request = Request.Builder()
            .url(URL + map["url"])
            .post(requestBody)
            .headers(requestHeader)
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
                    JsonDeserializer<Any?> { json, typeOfT, context ->
                        LocalDateTime.parse(json.asString)
                    })
                    .create()
                val type = (map["type"] as TypeToken<*>)
                val content = gson.fromJson(response.body?.string(), type)

                if (response.isSuccessful) {
                    // Jwt token
                    response.headers["Authorization"]?.let {
                        activity.intent.putExtra("token", it)
                    }

                    if (map.containsKey("nestedReply")) {
                        activity.runOnUiThread { activity.resolvePost(mapOf("reply" to content)) }
                    } else {
                        activity.runOnUiThread { activity.resolvePost(content) }
                    }
                } else {
                    activity.runOnUiThread { activity.resolveFailure(response) }
                }
            }
        })
    }
}
