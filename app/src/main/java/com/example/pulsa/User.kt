package com.example.pulsa

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class User(
    var user_id: Long,
    var userName: String,
    var password: String,
    var realName: String,
    var avatar: String,
    var email: String,
    var subs: MutableList<Sub>,
    var posts: MutableList<Post>,
    var created: LocalDateTime,
    var updated: LocalDateTime
) : Parcelable
