package com.example.pulsa

import java.time.LocalDateTime

object UserList {
    var users: MutableList<User> = mutableListOf<User>(
        User(
            69,
            "admin",
            "123",
            "adminus",
            "https://res.cloudinary.com/dc6h0nrwk/image/upload/v1668893599/a6zqfrxfflxw5gtspwjr.png",
            "god@mode.com",
            mutableListOf(),
            mutableListOf(),
            LocalDateTime.now(),
            LocalDateTime.now()
        )
    )
        private set

    fun addUser(user: User) {
        users.add(user)
    }

}