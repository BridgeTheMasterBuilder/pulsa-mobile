package com.example.pulsa

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import org.w3c.dom.Text

class FrontPageAdapter(private val context: Activity, private val arrayList : ArrayList<Post> ) :
                        ArrayAdapter<Post>(context, R.layout.list_item, arrayList)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate(R.layout.list_item, null)

        val imageView : ImageView = view.findViewById(R.id.post_image)
        val postTitle : TextView = view.findViewById(R.id.post_title)
        val postText : TextView = view.findViewById(R.id.post_text)
        val subId : TextView = view.findViewById(R.id.sub_id)

        imageView.setImageResource(R.drawable.pulsa)
        postTitle.text = arrayList[position].title
        postText.text = arrayList[position].content.text
        subId.text = arrayList[position].sub

        return view
    }
}