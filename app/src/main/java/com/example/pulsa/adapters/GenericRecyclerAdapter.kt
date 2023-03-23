package com.example.pulsa.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.pulsa.BR
import com.example.pulsa.objects.Post
import com.example.pulsa.objects.Sub
import com.example.pulsa.utils.glideRequestListener
import okhttp3.HttpUrl.Companion.toHttpUrl


open class GenericRecyclerAdapter<T : Any>(
    private var items: MutableList<T>,
    private val onClick: ((T) -> Unit)?,
    @LayoutRes val layoutId: Int
) : RecyclerView.Adapter<GenericRecyclerAdapter.GenericViewHolder<T>>() {

    private lateinit var context: Context

    fun swapList(list: MutableList<T>) {
        this.items.clear()
        this.items.addAll(list)
        notifyDataSetChanged()
    }

    fun addItem(item: T, position: Int? = null) {
        if (position != null) {
            this.items.add(position, item)
            notifyItemInserted(position)
        } else {
            this.items.add(item)
            notifyItemInserted(this.items.size - 1)
        }
    }

    fun updateItem(item: T, position: Int) {
        this.items[position] = item
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<T> {
        context = parent.context
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(context),
            layoutId,
            parent,
            false
        )

        return GenericViewHolder(binding, onClick)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: GenericViewHolder<T>, position: Int) {
        holder.bind(items[position])

        val image = when(val item = items[position]) {
            is Post -> item.content.image
            is Sub -> item.image
            else -> ""
        }

        if (URLUtil.isValidUrl(image))
            Glide.with(context)
                .load(image)
                .addListener(glideRequestListener)
                .into(holder.imageView)
                .view.visibility = View.VISIBLE
    }

    class GenericViewHolder<T>(
        private val binding: ViewDataBinding,
        private val onClick: ((T) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        val imageView: ImageView = binding.root.findViewWithTag("image")

        fun bind(item: T) {
            when (item) {
                is Post -> binding.setVariable(BR.postItem, item)
                is Sub -> binding.setVariable(BR.subItem, item)
            }
            onClick?.let { listener ->
                itemView.setOnClickListener { listener(item) }
            }
        }
    }
}