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
        "Stefán",
        "anon",
        "Anonymous",
        "https://res.cloudinary.com/dc6h0nrwk/image/upload/v1668893599/a6zqfrxfflxw5gtspwjr.png",
        "anon@anon.anon",
        HashSet(),
        mutableListOf(),
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
            "http://res.cloudinary.com/dc6h0nrwk/image/upload/v1666562503/t94r47hzji1i2aoyxhx7.jpg",
            "http://res.cloudinary.com/dc6h0nrwk/video/upload/v1664772263/ltaf63f4ococlisbqtfo.mp3",
            "http://res.cloudinary.com/dc6h0nrwk/video/upload/v1664772263/ltaf63f4ococlisbqtfo.mp3",
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
                    "http://res.cloudinary.com/dc6h0nrwk/image/upload/v1664770603/q1h9kuthpcgqpkgz8c8r.png",
                    "http://res.cloudinary.com/dc6h0nrwk/video/upload/v1664772263/ltaf63f4ococlisbqtfo.mp3",
                    "http://res.cloudinary.com/dc6h0nrwk/video/upload/v1664772263/ltaf63f4ococlisbqtfo.mp3",
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
                        Content(
                            5,
                            "Bar",
                            "http://res.cloudinary.com/dc6h0nrwk/image/upload/v1666562503/t94r47hzji1i2aoyxhx7.jpg",
                            "http://res.cloudinary.com/dc6h0nrwk/video/upload/v1664772263/ltaf63f4ococlisbqtfo.mp3",
                            "http://res.cloudinary.com/dc6h0nrwk/video/upload/v1664772263/ltaf63f4ococlisbqtfo.mp3",
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
            "http://res.cloudinary.com/dc6h0nrwk/image/upload/v1664770603/q1h9kuthpcgqpkgz8c8r.png",
            "http://res.cloudinary.com/dc6h0nrwk/video/upload/v1664772263/ltaf63f4ococlisbqtfo.mp3",
            "http://res.cloudinary.com/dc6h0nrwk/video/upload/v1664772263/ltaf63f4ococlisbqtfo.mp3",
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
            "http://res.cloudinary.com/dc6h0nrwk/image/upload/v1664770603/q1h9kuthpcgqpkgz8c8r.png",
            "http://res.cloudinary.com/dc6h0nrwk/video/upload/v1664772263/ltaf63f4ococlisbqtfo.mp3",
            "http://res.cloudinary.com/dc6h0nrwk/video/upload/v1664772263/ltaf63f4ococlisbqtfo.mp3",
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
                "http://res.cloudinary.com/dc6h0nrwk/image/upload/v1664770603/q1h9kuthpcgqpkgz8c8r.png",
                "http://res.cloudinary.com/dc6h0nrwk/video/upload/v1664772263/ltaf63f4ococlisbqtfo.mp3",
                "http://res.cloudinary.com/dc6h0nrwk/video/upload/v1664772263/ltaf63f4ococlisbqtfo.mp3",
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
                "http://res.cloudinary.com/dc6h0nrwk/image/upload/v1664770603/q1h9kuthpcgqpkgz8c8r.png",
                "http://res.cloudinary.com/dc6h0nrwk/video/upload/v1664772263/ltaf63f4ococlisbqtfo.mp3",
                "http://res.cloudinary.com/dc6h0nrwk/video/upload/v1664772263/ltaf63f4ococlisbqtfo.mp3",
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
                "http://res.cloudinary.com/dc6h0nrwk/image/upload/v1664770603/q1h9kuthpcgqpkgz8c8r.png",
                "http://res.cloudinary.com/dc6h0nrwk/video/upload/v1664772263/ltaf63f4ococlisbqtfo.mp3",
                "http://res.cloudinary.com/dc6h0nrwk/video/upload/v1664772263/ltaf63f4ococlisbqtfo.mp3",
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