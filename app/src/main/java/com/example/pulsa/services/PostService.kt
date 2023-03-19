package com.example.pulsa.services

import com.example.pulsa.objects.*
import java.time.LocalDateTime

data class PostService(
    val sub: Sub = Sub(
        1,
        "Subbið Sub",
        "subbisub",
        mutableListOf(),
        "sub",
        1,
    ),
    val user: User = User(
        0,
        "Anonymous",
        "anon",
        "Anonymous",
        "https://res.cloudinary.com/dc6h0nrwk/image/upload/v1668893599/a6zqfrxfflxw5gtspwjr.png",
        "anon@anon.anon",
        mutableListOf(),
        mutableListOf(),
        LocalDateTime.now(),
        LocalDateTime.now()
    ),
    val reply1: Reply = Reply(
        1,
        Content(
            1,
            "reply 1",
            "",
            "audio",
            "record",
            LocalDateTime.now(),
            LocalDateTime.now()

        ),
        user,
        sub,
        0,
        mutableListOf(),
        mutableListOf(
            Reply(
                4,
                Content(
                    4,
                    "Foo",
                    "",
                    "",
                    "",
                    LocalDateTime.now(),
                    LocalDateTime.now()
                ),
                user,
                sub,
                0,
                mutableListOf(),
                mutableListOf(
                    Reply(
                        5,
                        Content(5, "Bar", "", "", "", LocalDateTime.now(), LocalDateTime.now()),
                        user,
                        sub,
                        0,
                        mutableListOf(),
                        mutableListOf(),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                    )
                ),
                LocalDateTime.now(),
                LocalDateTime.now()
            )
        ),
        LocalDateTime.now(),
        LocalDateTime.now()
    ),
    val reply2: Reply = Reply(
        2,
        Content(
            2,
            "reply 2",
            "",
            "audio",
            "record",
            LocalDateTime.now(),
            LocalDateTime.now()
        ),
        user,
        sub,
        0,
        mutableListOf(),
        mutableListOf(),
        LocalDateTime.now(),
        LocalDateTime.now()
    ),
    val reply3: Reply = Reply(
        3,
        Content(
            3,
            "reply 3",
            "",
            "audio",
            "record",
            LocalDateTime.now(),
            LocalDateTime.now()
        ),
        user,
        sub,
        0,
        mutableListOf(),
        mutableListOf(),
        LocalDateTime.now(),
        LocalDateTime.now()
    ),
    val replies: MutableList<Reply> = mutableListOf(reply1, reply2, reply3),
    val posts: MutableList<Post> = mutableListOf(
        Post(
            0,
            "Pólitík",
            Content(
                0,
                "Hægri vinstri upp og niður",
                "suð",
                "blóm",
                "record",
                LocalDateTime.now(),
                LocalDateTime.now()
            ),
            user,
            sub,
            1,
            mutableListOf(),
            replies,
            LocalDateTime.now(),
            LocalDateTime.now()
        ),
        Post(
            4,
            "Herp",
            Content(
                0,
                "Derp vinstri upp og niður",
                "suð",
                "blóm",
                "record",
                LocalDateTime.now(),
                LocalDateTime.now()
            ),
            user,
            sub,
            1,
            mutableListOf(),
            replies,
            LocalDateTime.now(),
            LocalDateTime.now()
        ),
        Post(
            5,
            "Derp",
            Content(
                0,
                "Hehehehe vinstri upp og niður",
                "suð",
                "blóm",
                "record",
                LocalDateTime.now(),
                LocalDateTime.now()
            ),
            user,
            sub,
            1,
            mutableListOf(),
            replies,
            LocalDateTime.now(),
            LocalDateTime.now()
        )
    )
)