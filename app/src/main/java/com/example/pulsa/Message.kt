package com.example.pulsa

import android.os.Parcelable
import java.time.LocalDateTime

abstract class Message(
    open val content: Content,
    open val creator: User,
    open val sub: Sub,
    open val Vote: Int,
    open val voted: MutableList<Voter>,
    open val replies: MutableList<Reply>,
    open val created: LocalDateTime,
    open val updated: LocalDateTime
) : Parcelable
