package com.example.pulsa

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class User(
    var user_id: Long? = null,
    var userName: String? = null,
    var password: String? = null,
    var realName: String? = null,
    var avatar: String? = null,
    var email: String? = null,
    var subs: MutableList<Sub>? = null,
    var posts: MutableList<Post>? = null,
    var created: LocalDateTime? = null,
    var updated: LocalDateTime? = null
) : Parcelable
