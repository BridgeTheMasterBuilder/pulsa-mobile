package com.example.pulsa

data class PostService(
    val sub: Sub = Sub(
        1,
        "Subbið Sub",
        "subbisub",
        null,
        "submynd",
        1,
    ),
    val user: User = User(
        0,
        "Anonymous",
        "anon",
        "Anonymous",
        "https://res.cloudinary.com/dc6h0nrwk/image/upload/v1668893599/a6zqfrxfflxw5gtspwjr.png",
        "anon@anon.anon",
        null,
        null,
        null,
        null
        ),
    val reply1: Reply = Reply(
        1,
        Content(
            1,
            "reply 1",
            "mynd",
            "audio",
            "record"
        )
    ),
    val reply2: Reply = Reply(
        2,
        Content(
            2,
            "reply 2",
            "mynd",
            "audio",
            "record"
        )
    ),
    val reply3: Reply = Reply(
        3,
        Content(
            3,
            "reply 3",
            "mynd",
            "audio",
            "record"
        )
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
                "record"
            ),
            user,
            sub,
            1,
            null,
            replies,
            null,
            null
        ),
        Post(
            4,
            "Herp",
            Content(
                0,
                "Derp vinstri upp og niður",
                "suð",
                "blóm",
                "record"
            ),
            user,
            sub,
            1,
            null,
            replies,
            null,
            null
        ),
        Post(
            5,
            "Derp",
            Content(
                0,
                "Hehehehe vinstri upp og niður",
                "suð",
                "blóm",
                "record"
            ),
            user,
            sub,
            1,
            null,
            replies,
            null,
            null
        )
    )
)