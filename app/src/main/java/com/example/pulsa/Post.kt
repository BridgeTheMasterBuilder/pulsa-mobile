package com.example.pulsa

import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
class Post(
    val post_id: Int,
    val title: String,
    override val content: Content,
    override val creator: User,
    override val sub: Sub,
    override val Vote: Int,
    override val voted: MutableList<Voter>,
    override val replies: MutableList<Reply>,
    override val created: LocalDateTime,
    override val updated: LocalDateTime
) : Message(content, creator, sub, Vote, voted, replies, created, updated)
