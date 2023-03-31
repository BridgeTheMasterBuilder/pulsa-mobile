package com.example.pulsa.activities

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

    fun dispatch(content: Content, pos: Int, launcher: (Content, Int) -> Unit)
}