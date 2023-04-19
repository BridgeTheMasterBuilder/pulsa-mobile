package com.example.pulsa.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.pulsa.R
import com.example.pulsa.activities.BaseLayoutActivity


object UserUtils {
    private var sharedPreferences: SharedPreferences? = null
    private var token: String? = ""
    private var userName: String? = ""
    private var avatar: String? = ""
    private var loggedIn: String? = ""
    private var userId: String? = ""

    fun setup(context: Context) {
        token = context.getString(R.string.token)
        avatar = context.getString(R.string.avatar)
        loggedIn = context.getString(R.string.loggedIn)
        userName = context.getString(R.string.userName)
        userId = context.getString(R.string.userId)
        sharedPreferences =
            context.getSharedPreferences(context.getString(R.string.user), MODE_PRIVATE)
    }

    fun loggedIn(): Boolean {
        return sharedPreferences!!.getBoolean(loggedIn, false)
    }

    fun getJwtToken(): String {
        return "Bearer " + sharedPreferences!!.getString(token, "")!!
    }

    fun getUserAvatar(activity: BaseLayoutActivity): String {
        return sharedPreferences!!.getString(avatar, "")!!
    }

    fun getUserName(activity: BaseLayoutActivity): String {
        return sharedPreferences!!.getString(userName, "")!!
    }

    fun getUserID(): Long {
        return sharedPreferences!!.getLong(userId, -1)
    }
}