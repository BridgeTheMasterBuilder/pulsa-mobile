package com.example.pulsa

import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
class Reply(
    val reply_id: Long? = null,
    override val content: Content? = null,
    override val creator: User? = null,
    override val sub: Sub? = null,
    override val Vote: Int? = null,
    override val voted: MutableList<Voter>? = null,
    override val replies: MutableList<Reply>? = null,
    override val created: LocalDateTime? = null,
    override val updated: LocalDateTime? = null,
) : Message(content, creator, sub, Vote, voted, replies, created, updated)
