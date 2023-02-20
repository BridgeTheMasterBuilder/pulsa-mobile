package com.example.pulsa

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Reply(var content: Content, var sub: String) : Parcelable
