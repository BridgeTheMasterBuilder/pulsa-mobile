package com.example.pulsa

import android.os.Build
import androidx.annotation.RequiresApi

class Reply : Message {
    var replyId: Long? = null

    /**
     * Reply entity
     *
     * @param content Content contains content of reply
     * @param user    User owner of reply
     * @param voted   List<Voter> List of users that have voted on reply
     * @param replies List<Reply> List of replies to Reply
     * @return Reply
    </Reply></Voter> */
    @RequiresApi(Build.VERSION_CODES.O)
    constructor(
        content: Content?,
        user: User?,
        voted: List<Voter?>?,
        replies: List<Reply?>?,
        sub: Sub?
    ) {
        setContent(content)
        setCreator(user)
        setVoted(voted as MutableList<Voter>)
        setReplies(replies as MutableList<Reply>)
        setCreated()
        setUpdated()
        setSub(sub)
    }

    constructor() {}
}
