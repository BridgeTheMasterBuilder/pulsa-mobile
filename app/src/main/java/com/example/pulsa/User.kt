package com.example.pulsa

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class User {
    var user_id: Long? = null
    var userName: String? = null
    var password: String? = null
    var realName: String? = null
    var avatar: String? = null
    var email: String? = null
    private var subs: MutableList<Sub> = ArrayList()
    var posts: List<Post> = ArrayList()
    var replies: List<Reply> = ArrayList()
    var created: LocalDateTime? = null
    var updated: LocalDateTime? = null

    constructor() {}

    /**
     * User entity
     *
     * @param userName String Unique user identifier
     * @param password String Top secret password
     * @param realName String Users real name
     * @param avatar   String DataURL of uploaded image
     * representing the user
     * @param email    String email for user
     * @return User
     */
    @RequiresApi(Build.VERSION_CODES.O)
    constructor(
        userName: String?,
        password: String?,
        realName: String?,
        avatar: String?,
        email: String?
    ) {
        this.userName = userName
        this.password = password
        this.realName = realName
        this.avatar = avatar
        this.email = email
        setCreated()
        setUpdated()
    }

    val time: String
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            return created!!.format(formatter)
        }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setCreated() {
        created = LocalDateTime.now()
    }

    fun getSubs(): List<Sub> {
        return subs
    }

    fun setSubs(subs: MutableList<Sub>) {
        this.subs = subs
    }

    fun addSub(sub: Sub) {
        subs.add(sub)
    }

    fun isFollowing(sub: Sub): Boolean {
        return subs.contains(sub)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUpdated() {
        updated = LocalDateTime.now()
    }
}