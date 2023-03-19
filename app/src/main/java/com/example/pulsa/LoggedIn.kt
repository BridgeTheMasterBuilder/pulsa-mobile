package com.example.pulsa

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
        this.user = user
    }

    fun getUser(): User {
        return this.user
    }
}