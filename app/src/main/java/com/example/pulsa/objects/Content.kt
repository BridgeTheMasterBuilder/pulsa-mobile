package com.example.pulsa.objects

import android.os.Parcelable
import java.time.LocalDateTime

@kotlinx.parcelize.Parcelize
data class Content(
    val content_id: Long,
    val text: String,
    val image: String,
    val audio: String,
    val recording: String,
    val created: LocalDateTime,
    val updated: LocalDateTime
) : Parcelable
