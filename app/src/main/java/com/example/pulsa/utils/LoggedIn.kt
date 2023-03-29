package com.example.pulsa.utils

import com.example.pulsa.objects.User

object LoggedIn {
    private var loggedIn = false
    private lateinit var user: User

    fun setLoggedIn(login: Boolean) {
        loggedIn = login
    }

    fun getLoggedIn(): Boolean {
        return loggedIn
    }


    fun setUser(user: User) {
        LoggedIn.user = user
    }

    fun getUser(): User {
        return user
    }
}