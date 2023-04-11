package com.example.pulsa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.chibde.visualizer.LineVisualizer
import com.example.pulsa.BR
import com.example.pulsa.R
import com.example.pulsa.databinding.PostItemBindingImpl
import com.example.pulsa.objects.Post
import com.example.pulsa.objects.Sub
import com.example.pulsa.utils.MediaUtils
import com.example.pulsa.utils.UserUtils
import com.example.pulsa.utils.glideRequestListener
import com.google.android.material.button.MaterialButton


open class GenericRecyclerAdapter<T : Any>(
    private var items: MutableList<T>,
    private val onClick: ((T, Int) -> Unit)?,
    @LayoutRes val layoutId: Int
) : RecyclerView.Adapter<GenericRecyclerAdapter.GenericViewHolder<T>>() {

    private lateinit var context: Context
    private lateinit var upvote: ((id: Long, pos: Int) -> Unit)
    private lateinit var downvote: ((id: Long, pos: Int) -> Unit)
    private lateinit var sub: ((sub: Sub, position: Int) -> Unit)
    private lateinit var playAudio: ((button: MaterialButton?, mediaUtils: MediaUtils) -> Unit)
    private lateinit var playRecording: ((button: MaterialButton?, mediaUtils: MediaUtils) -> Unit)


    fun upvoteOnClick(upvoteOnClickListener: (id: Long, pos: Int) -> Unit) {
        upvote = upvoteOnClickListener
    }

    fun downvoteOnClick(downvoteOnClickListener: ((id: Long, pos: Int) -> Unit)) {
        downvote = downvoteOnClickListener
    }

    fun subOnClick(subOnClickListener: ((sub: Sub, position: Int) -> Unit)) {
        sub = subOnClickListener
    }

    fun playAudioOnClick(playAudioOnClickListener: ((button: MaterialButton?, mediaUtils: MediaUtils) -> Unit)) {
        playAudio = playAudioOnClickListener
    }

    fun playRecordingOnClick(playRecordingOnClickListener: ((button: MaterialButton?, mediaUtils: MediaUtils) -> Unit)) {
        playRecording = playRecordingOnClickListener
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


        return GenericViewHolder(binding, onClick, context)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: GenericViewHolder<T>, position: Int) {
        if (this::upvote.isInitialized) holder.upvoteOnClickListener = upvote
        if (this::downvote.isInitialized) holder.downvoteOnClickListener = downvote
        if (this::sub.isInitialized) holder.subOnClickListener = sub

        when (val item = items[position]) {
            is Post -> {
                if (URLUtil.isValidUrl(item.content.audio)) {
                    holder.audio?.visibility = View.VISIBLE
                    holder.playAudioOnClickListener = playAudio
                }
                if (URLUtil.isValidUrl(item.content.recording)) {
                    holder.recording?.visibility = View.VISIBLE
                    holder.playRecordingOnClickListener = playRecording
                }
            }
        }

        holder.bind(items[position], position)
        val image = when (val item = items[position]) {
            is Post -> item.content.image
            is Sub -> item.image
            else -> ""
        }

        /* val avatar = when (val item = items[position]) {
            is Post -> item.creator.avatar
            else -> ""
        } */

        val avatar = ""

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
        private val onClick: ((T, position: Int) -> Unit)?,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        val imageView: ImageView = binding.root.findViewWithTag("image")
        var avatar: ImageView? = null
        var sub: TextView? = null

        var audio: MaterialButton? = binding.root.findViewWithTag("audio")
        var recording: MaterialButton? = binding.root.findViewWithTag("recording")

        lateinit var upvoteOnClickListener: (id: Long, pos: Int) -> Unit
        lateinit var downvoteOnClickListener: (id: Long, pos: Int) -> Unit
        lateinit var subOnClickListener: (sub: Sub, position: Int) -> Unit
        lateinit var playAudioOnClickListener: (button: MaterialButton?, mediaUtils: MediaUtils) -> Unit
        lateinit var playRecordingOnClickListener: (button: MaterialButton?, mediaUtils: MediaUtils) -> Unit

        fun bind(item: T, position: Int) {
            when (item) {
                is Post -> binding.setVariable(BR.postItem, item)
                is Sub -> binding.setVariable(BR.subItem, item)
            }

            if (binding is PostItemBindingImpl) {
                val post = binding.postItem!!
                avatar = binding.root.findViewWithTag("avatar")
                binding.root.findViewWithTag<TextView>("sub").let { it ->
                    it.setOnClickListener {
                        subOnClickListener(post.sub, position)
                    }
                }

                audio.let { it ->
                    if (URLUtil.isValidUrl((item as Post).content.audio)) {
                        val mediaUtils = MediaUtils()
                        val mediaPlayer = mediaUtils.initMediaPlayerWithUrl(
                            context,
                            binding.root.findViewWithTag("audioVisualizer"),
                            audio,
                            post.content.audio
                        )
                        it?.setOnClickListener {
                            playAudioOnClickListener(audio, mediaUtils)
                        }
                    }
                }

                recording.let { it ->
                    if (URLUtil.isValidUrl((item as Post).content.recording)) {
                        val mediaUtils = MediaUtils()
                        mediaUtils.initMediaPlayerWithUrl(
                            context,
                            binding.root.findViewWithTag("recordingVisualizer"),
                            recording,
                            post.content.recording
                        )
                        it?.setOnClickListener {
                            playRecordingOnClickListener(recording, mediaUtils)
                        }
                    }
                }

                if (UserUtils.loggedIn()) {
                    binding.root.findViewWithTag<ImageView>("vote_up").let { it ->
                        it.setOnClickListener {
                            upvoteOnClickListener(post.postId, position)
                        }
                        it.visibility = View.VISIBLE
                    }
                    binding.root.findViewWithTag<ImageView>("vote_down").let { it ->
                        it.setOnClickListener {
                            downvoteOnClickListener(
                                post.postId,
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