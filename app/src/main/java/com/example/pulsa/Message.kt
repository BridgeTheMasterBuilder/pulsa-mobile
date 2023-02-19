package com.example.pulsa

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.util.*

abstract class Message {
    var content: Content? = null
    var creator: User? = null
    var sub: Sub? = null

    /**
     * Returns sum of total upvotes and downvotes on Message
     * Voter object contains boolean value "vote"
     * Upvote if true, votes is incremented
     * Downvote if false, votes is decremented
     *
     * @return Integer Sum of total votes on Message
     */
    val Vote: Int
        get() {
            var votes = 0
            for (vote in getVoted()) {
                if (vote.isVote) {
                    votes++
                } else {
                    votes--
                }
            }
            return votes
        }

    private var voted: MutableList<Voter> = ArrayList()
    private var replies: MutableList<Reply> = ArrayList()
    private var created: LocalDateTime? = null
    private var updated: LocalDateTime? = null
    fun getReplies(): List<Reply> {
        return replies
    }

    fun setReplies(replies: MutableList<Reply>) {
        this.replies = replies
    }

    /**
     * Returns internal Reply object by id if it exists
     *
     * @param replyId Long id of Reply
     * @return Optional<Reply>
    </Reply> */
    @RequiresApi(Build.VERSION_CODES.N)
    fun getReplyById(replyId: Long): Optional<Reply> {
        println("Finding $replyId")
        for (reply in replies) {
            println("Is it " + reply.replyId + "?")
            if (reply.replyId === replyId) return Optional.of(reply)
        }
        return Optional.empty()
    }

    fun addReply(reply: Reply) {
        replies.add(reply)
    }

    fun getVoted(): List<Voter> {
        return voted
    }

    fun setVoted(voted: MutableList<Voter>) {
        this.voted = voted
    }

    fun addVote(voter: Voter) {
        voted.add(voter)
    }

    fun removeVote(vote: Voter) {
        voted.remove(vote)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCreated(): LocalDateTime {
        return LocalDateTime.now()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setCreated() {
        created = LocalDateTime.now()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getUpdated(): LocalDateTime {
        return LocalDateTime.now()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUpdated() {
        updated = LocalDateTime.now()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun findVoter(user: User): Optional<Voter> {
        val voted = getVoted()
        return voted.stream().filter { v: Voter -> v.user!!.user_id === user.user_id }.findAny()
    }

    abstract fun setSub(sub: Sub?)
    abstract fun setContent(content: Content?)
    abstract fun setCreator(creator: User?)
}