package com.example.pulsa.objects

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Voter(
    var id: Long,
    var user: User,
    var vote: Boolean
) : Parcelable
