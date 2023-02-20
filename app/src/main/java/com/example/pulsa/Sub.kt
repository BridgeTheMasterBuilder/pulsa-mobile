package com.example.pulsa

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sub(
    var sub_id: Long? = null,
    var name: String? = null,
    var slug: String? = null,
    var followers: MutableList<User>? = null,
    var image: String? = null,
    var followerCount: Int? = null
) : Parcelable
