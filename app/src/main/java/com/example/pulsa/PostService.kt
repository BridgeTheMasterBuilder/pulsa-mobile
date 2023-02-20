package com.example.pulsa

import java.util.*

data class PostService(
    val posts: ArrayList<Post> = ArrayList<Post>(
        Arrays.asList(
            Post(
                "Pólitík",
                Content(
                    "Hægri vinstri upp og niður",
                    "suð",
                    "blóm"
                ),
                "Áttaviti",
                ArrayList(
                    listOf(
                        Reply(Content("Foo", "", "")),
                        Reply(Content("Bar", "", "")),
                        Reply(Content("Baz", "", "")),
                    )
                )
            ),
            Post(
                "Listaverk",
                Content(
                    "Píkatsjú málverk",
                    "suð",
                    "blóm"
                ),
                "Lista-Verktakar",
                ArrayList(listOf())
            ),
            Post(
                "Fyndið",
                Content(
                    "Brandari, haha",
                    "suð",
                    "blóm"
                ),
                "Brandarar",
                ArrayList(listOf())
            )
        )
    )
)