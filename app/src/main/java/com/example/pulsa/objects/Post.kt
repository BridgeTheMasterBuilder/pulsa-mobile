package com.example.pulsa.objects

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
class Post(
    val postId: Long,
    var title: String,
    var content: Content,
    var creator: User,
    var sub: Sub,
    var vote: Int,
    var voted: MutableList<Voter>,
    var replies: MutableList<Reply>,
    var created: LocalDateTime,
    var updated: LocalDateTime
) : Parcelable {
    fun replyCount(): Int {
        fun inner(reply: Reply): Int {
            if (reply.replies.isEmpty()) return 1
            var innerCount = 1
            for (item: Reply in reply.replies) {
                innerCount += inner(item)
            }
            return innerCount
        }

        var count = 0
        for (item: Reply in replies) {
            count += inner(item)
        }

        return count
    }
}
