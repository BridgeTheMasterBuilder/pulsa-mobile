package com.example.pulsa

import android.os.Parcelable
import java.time.LocalDateTime

@kotlinx.parcelize.Parcelize
data class Content(
    val content_id: Long? = null,
    val text: String? = null,
    val image: String? = null,
    val audio: String? = null,
    val recording: String? = null,
    val created: LocalDateTime? = null,
    val updated: LocalDateTime? = null
    ): Parcelable
