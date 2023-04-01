package com.example.pulsa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.pulsa.BR
import com.example.pulsa.databinding.PostItemBindingImpl
import com.example.pulsa.objects.Post
import com.example.pulsa.objects.Sub
import com.example.pulsa.utils.UserUtils
import com.example.pulsa.utils.glideRequestListener


open class GenericRecyclerAdapter<T : Any>(
    private var items: MutableList<T>,
    private val onClick: ((T, Int) -> Unit)?,
    @LayoutRes val layoutId: Int
) : RecyclerView.Adapter<GenericRecyclerAdapter.GenericViewHolder<T>>() {

    private lateinit var context: Context
    private lateinit var upvote: ((id: Long, pos: Int) -> Unit)
    private lateinit var downvote: ((id: Long, pos: Int) -> Unit)

    fun upvoteOnClick(upvoteOnClickListener: (id: Long, pos: Int) -> Unit) {
        upvote = upvoteOnClickListener
    }

    fun downvoteOnClick(downvoteOnClickListener: ((id: Long, pos: Int) -> Unit)) {
        downvote = downvoteOnClickListener
    }

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
        if (this::upvote.isInitialized) holder.upvoteOnClickListener = upvote
        if (this::downvote.isInitialized) holder.downvoteOnClickListener = downvote
        holder.bind(items[position], position)
        val image = when (val item = items[position]) {
            is Post -> item.content.image
            is Sub -> item.image
            else -> ""
        }

        val avatar = when (val item = items[position]) {
            is Post -> item.creator.avatar
            else -> ""
        }

        if (URLUtil.isValidUrl(avatar)) {
            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.strokeWidth = 3f
            circularProgressDrawable.centerRadius = 18f
            circularProgressDrawable.start()

            holder.avatar?.let {
                Glide.with(context)
                    .load(avatar)
                    .placeholder(circularProgressDrawable)
                    .listener(glideRequestListener)
                    .into(it)
            }
        }

        if (URLUtil.isValidUrl(image)) {
            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.strokeWidth = 15f
            circularProgressDrawable.centerRadius = 90f
            circularProgressDrawable.start()

            Glide.with(context)
                .load(image)
                .listener(glideRequestListener)
                .placeholder(circularProgressDrawable)
                .into(holder.imageView)
                .view.visibility = View.VISIBLE
        }

    }

    class GenericViewHolder<T>(
        private val binding: ViewDataBinding,
        private val onClick: ((T, position: Int) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        val imageView: ImageView = binding.root.findViewWithTag("image")
        var avatar: ImageView? = null
        lateinit var upvoteOnClickListener: (id: Long, pos: Int) -> Unit
        lateinit var downvoteOnClickListener: (id: Long, pos: Int) -> Unit


        fun bind(item: T, position: Int) {
            when (item) {
                is Post -> binding.setVariable(BR.postItem, item)
                is Sub -> binding.setVariable(BR.subItem, item)
            }

            if (binding is PostItemBindingImpl) {
                avatar = binding.root.findViewWithTag("avatar")
                if (UserUtils.loggedIn()) {
                    binding.root.findViewWithTag<ImageView>("vote_up").let { it ->
                        it.setOnClickListener {
                            upvoteOnClickListener(binding.postItem!!.postId, position)
                        }
                        it.visibility = View.VISIBLE
                    }
                    binding.root.findViewWithTag<ImageView>("vote_down").let { it ->
                        it.setOnClickListener {
                            downvoteOnClickListener(
                                binding.postItem!!.postId,
                                position
                            )
                        }
                        it.visibility = View.VISIBLE
                    }
                } else {
                    binding.root.findViewWithTag<ImageView>("vote_up").visibility = View.GONE
                    binding.root.findViewWithTag<ImageView>("vote_down").visibility = View.GONE
                }
            }

            onClick?.let { listener ->
                itemView.setOnClickListener { listener(item, position) }
            }
        }
    }
}