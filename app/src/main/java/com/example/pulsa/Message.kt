package com.example.pulsa

import android.os.Parcelable
import java.time.LocalDateTime

abstract class Message(
    open val content: Content? = null,
    open val creator: User? = null,
    open val sub: Sub? = null,
    open val Vote: Int? = null,
    open val voted: MutableList<Voter>? = null,
    open val replies: MutableList<Reply>? = null,
    open val created: LocalDateTime? = null,
    open val updated: LocalDateTime? = null
) : Parcelable
