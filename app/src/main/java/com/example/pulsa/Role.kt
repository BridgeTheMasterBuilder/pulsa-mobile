package com.example.pulsa.objects

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Role(
    val id: Long,
    val name: ERole
) : Parcelable