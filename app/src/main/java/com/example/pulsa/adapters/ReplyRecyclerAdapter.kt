package com.example.pulsa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.pulsa.BR
import com.example.pulsa.R
import com.example.pulsa.databinding.ReplyBindingImpl
import com.example.pulsa.objects.Reply
import com.example.pulsa.objects.Sub
import com.example.pulsa.utils.UserUtils
import com.example.pulsa.utils.glideRequestListener


open class ReplyRecyclerAdapter<T : Any>(
    private var items: MutableList<T>,
    private val onClick: ((T, Int) -> Unit)?,
    @LayoutRes val layoutId: Int
) : RecyclerView.Adapter<ReplyRecyclerAdapter.ReplyViewHolder<T>>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder<T> {
        context = parent.context
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(context),
            layoutId,
            parent,
            false
        )
        binding.root.findViewById<Button>(R.id.postpage_replybutton).visibility = View.GONE

        return ReplyViewHolder(binding, onClick)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ReplyViewHolder<T>, position: Int) {
        if (this::upvote.isInitialized) holder.upvoteOnClickListener = upvote
        if (this::downvote.isInitialized) holder.downvoteOnClickListener = downvote
        holder.bind(items[position], position)
        val image = when (val item = items[position]) {
            is Reply -> item.content.image
            is Sub -> item.image
            else -> ""
        }
        val avatar = when (val item = items[position]) {
            is Reply -> item.creator.avatar
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
        val text = when (val item = items[position]) {
            is Reply -> item.content.text
            else -> ""
        }
        holder.text.text = text

    }

    class ReplyViewHolder<T>(
        private val binding: ViewDataBinding,
        private val onClick: ((T, position: Int) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        val imageView: ImageView = binding.root.findViewById(R.id.reply_image)
        var avatar: ImageView? = null
        val text: TextView = binding.root.findViewById(R.id.reply_text)
        val username: TextView = binding.root.findViewById(R.id.reply_username)
        lateinit var upvoteOnClickListener: (id: Long, pos: Int) -> Unit
        lateinit var downvoteOnClickListener: (id: Long, pos: Int) -> Unit


        fun bind(item: T, position: Int) {
            when (item) {
                is Reply -> binding.setVariable(BR.listItem, item)
                is Sub -> binding.setVariable(BR.subItem, item)
            }

            if (binding is ReplyBindingImpl) {
                avatar = binding.root.findViewById(R.id.reply_avatar)

                if (UserUtils.loggedIn()) {
                    binding.root.findViewWithTag<ImageView>("vote_up").let { it ->
                        it.setOnClickListener {
                            upvoteOnClickListener(binding.listItem!!.replyId, position)
                        }
                        it.visibility = View.VISIBLE
                    }
                    binding.root.findViewWithTag<ImageView>("vote_down").let { it ->
                        it.setOnClickListener {
                            downvoteOnClickListener(
                                binding.listItem!!.replyId,
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