package com.example.pulsa

class Voter {
    private val id: Long? = null

    var user: User? = null
        private set

    var isVote = false

    /**
     * Voter entity
     *
     * @param user User casting the vote
     * @param vote Boolean upvote when true
     * downvote when false
     * @return Voter Object
     */
    constructor(user: User?, vote: Boolean) {
        this.user = user
        isVote = vote
    }

    constructor() {}

    fun setUser(username: String?) {
        user = user
    }

    var userID: Long?
        get() = user!!.user_id
        set(userID) {
            user!!.user_id = userID!!
        }

    override fun toString(): String {
        return "Voter{" +
                "id='" + id + '\'' +
                "user='" + user + '\'' +
                ", userID=" + user!!.user_id +
                ", vote=" + isVote +
                '}'
    }
}