package com.example.pulsa.objects

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class User(
    val user_id: Long,
    var username: String,
    var password: String,
    var realName: String,
    var avatar: String,
    var email: String,
    var roles: Set<Role>,
    var subs: MutableList<Sub>,
    var posts: MutableList<Post>,
    var replies: MutableList<Reply>,
    val created: LocalDateTime,
    var updated: LocalDateTime
) : Parcelable
