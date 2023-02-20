package com.example.pulsa

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post(
    var title: String,
    var content: Content,
    var sub: String,
    var replies: ArrayList<Reply>
) : Parcelable
