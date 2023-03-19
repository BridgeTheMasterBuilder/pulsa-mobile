package com.example.pulsa.objects

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
class Reply(
    var reply_id: Long,
    var content: Content,
    var creator: User,
    var sub: Sub,
    var vote: Int,
    var voted: MutableList<Voter>,
    var replies: MutableList<Reply>,
    var created: LocalDateTime,
    var updated: LocalDateTime,
) : Parcelable
