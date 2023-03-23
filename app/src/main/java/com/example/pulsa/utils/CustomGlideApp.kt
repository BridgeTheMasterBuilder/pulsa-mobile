package com.example.pulsa.utils

import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestListener

@GlideModule
class CustomGlideApp : AppGlideModule()

public val glideRequestListener = object : RequestListener<Drawable> {
    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: com.bumptech.glide.request.target.Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        val message = e?.message.toString()
        Log.e("Glide: ", message, Throwable(e))
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: com.bumptech.glide.request.target.Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        // everything worked out, so probably nothing to do
        return false
    }
}