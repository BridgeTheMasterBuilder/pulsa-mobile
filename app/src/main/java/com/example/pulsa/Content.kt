package com.example.pulsa

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

class Content {
    var content_id: Long? = null
    var text: String? = null
    var image: String? = null
    var audio: String? = null
    var recording: String? = null
    var created: LocalDateTime? = null
        private set
    var updated: LocalDateTime? = null
        private set

    constructor() {}

    /**
     * Container for message content
     *
     * @param text      String text input from user
     * @param image     String DataURL of uploaded image
     * @param audio     String DataURL of uploaded audio
     * @param recording String DataURL of recorded audio
     * @return Content
     */
    @RequiresApi(Build.VERSION_CODES.O)
    constructor(text: String?, image: String?, audio: String?, recording: String?) {
        this.text = text
        this.image = image
        this.audio = audio
        this.recording = recording
        setCreated()
        setUpdated()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setCreated() {
        created = LocalDateTime.now()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUpdated() {
        updated = LocalDateTime.now()
    }
}
