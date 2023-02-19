package com.example.pulsa

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Content(var text: String, var audio:String, var image: String): Parcelable
