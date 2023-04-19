package com.example.pulsa

import java.time.LocalDateTime

data class UserService(
    var users: MutableList<User> = mutableListOf(
        User(
            69,
            "admin",
            "123",
            "adminus",
            "https://res.cloudinary.com/dc6h0nrwk/image/upload/v1668893599/a6zqfrxfflxw5gtspwjr.png",
            "god@mode.com",
            HashSet(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            LocalDateTime.now(),
            LocalDateTime.now()
        )
    )
) {
    companion object {
        val user = User(
            69,
            "admin",
            "123",
            "adminus",
            "https://res.cloudinary.com/dc6h0nrwk/image/upload/v1668893599/a6zqfrxfflxw5gtspwjr.png",
            "god@mode.com",
            HashSet(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            LocalDateTime.now(),
            LocalDateTime.now()
        )
    }
}