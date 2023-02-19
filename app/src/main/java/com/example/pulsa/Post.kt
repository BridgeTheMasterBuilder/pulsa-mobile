package com.example.pulsa

import android.os.Build
import androidx.annotation.RequiresApi

class Post : Message {
    var postId: Long? = null
    var title: String? = null

    constructor() {}

    /**
     * Post entity
     *
     * @param title   String title of post
     * @param sub     Sub SubPulsa of post
     * @param content Content contains content of post
     * @param creator User Post owner
     * @param voted   List<Voter> List of users that have voted on post
     * @param replies List<Reply> List of replies to post
     * @return Post object
    </Reply></Voter> */
    @RequiresApi(Build.VERSION_CODES.O)
    constructor(
        title: String?,
        sub: Sub?,
        content: Content?,
        creator: User?,
        voted: List<Voter?>?,
        replies: List<Reply?>?
    ) {
        this.title = title
        setSub(sub)
        setContent(content)
        setCreator(creator)
        setVoted(voted as MutableList<Voter>)
        setReplies(replies as MutableList<Reply>)
        setCreated()
        setUpdated()
    }

    override fun setSub(sub: Sub?) {
        TODO("Not yet implemented")
    }

    override fun setContent(content: Content?) {
        TODO("Not yet implemented")
    }

    override fun setCreator(creator: User?) {
        TODO("Not yet implemented")
    }
}
