package com.example.pulsa.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.view.View
import androidx.databinding.ViewDataBinding
import com.example.pulsa.databinding.ListItemBinding
import com.example.pulsa.objects.Post
import java.io.InputStream
import java.net.URL


class DownloadImageTaskPost(binding: ViewDataBinding, item: Post) :
    AsyncTask<String?, Void?, Bitmap?>() {
    var binding: ViewDataBinding
    var item: Post

    init {
        this.binding = binding
        this.item = item
    }

    override fun doInBackground(vararg p0: String?): Bitmap? {
        val urldisplay = p0[0]
        var mIcon11: Bitmap? = null
        try {
            val `in`: InputStream = URL(urldisplay).openStream()
            mIcon11 = BitmapFactory.decodeStream(`in`)
        } catch (e: Exception) {
            e.message?.let { Log.e("Error", it) }
            e.printStackTrace()
        }
        return mIcon11
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(result: Bitmap?) {
        val postImage = (binding as ListItemBinding).postImage
        postImage.setImageBitmap(result)
        result ?.let {
            postImage.visibility = View.VISIBLE
        }
    }
}