package com.example.pulsa

import java.util.*
import kotlin.collections.ArrayList

data class PostService(val posts: ArrayList<Post> = ArrayList<Post>(
    Arrays.asList(
        Post("Pólitík",
            Content("Hægri vinstri upp og niður",
        "suð",
        "blóm"),
    "Áttaviti"),
        Post("Listaverk",
            Content("Píkatsjú málverk",
                "suð",
                "blóm"),
            "Lista-Verktakar"),
        Post("Fyndið",
            Content("Brandari, haha",
                "suð",
                "blóm"),
            "Brandarar"))))