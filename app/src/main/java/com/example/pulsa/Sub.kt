package com.example.pulsa

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sub(
    val sub_id: Long,
    var name: String,
    var slug: String,
    var followers: MutableList<User>,
    var image: String,
    var followerCount: Int
) : Parcelable
