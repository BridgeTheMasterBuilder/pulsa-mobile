package com.example.pulsa.activities

import android.os.Bundle

interface ActivityRing<Content> {
    fun next(list: MutableList<Content>, pos: Int): Pair<Content, Int> {

        val position = (pos + 1) % list.size
        val post = list[position]

        return Pair(post, position)
    }

    fun prev(list: MutableList<Content>, pos: Int): Pair<Content, Int> {
        var position = pos - 1

        if (position < 0) {
            position = list.size - 1
        }

        val post = list[position]

        return Pair(post, position)
    }

    fun dispatch(content: Content, pos: Int)

    fun handle(data: Bundle, contentList: MutableList<Content>, pos: Int): Boolean {
        if (data.getBoolean("nextContent", false)) {
            val (content, position) = next(contentList, pos)

            dispatch(content, position)
        } else if (data.getBoolean("prevContent", false)) {
            val (content, position) = prev(contentList, pos)

            dispatch(content, position)
        } else {
            return false
        }

        return true
    }
}