package com.example.pulsa.objects

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sub(
    var sub_id: Long,
    var name: String,
    var slug: String,
    var followers: MutableList<User>,
    var image: String,
    var followerCount: Int
) : Parcelable
