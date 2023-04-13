package com.example.pulsa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pulsa.BR
import com.example.pulsa.R
import com.example.pulsa.objects.Post
import com.example.pulsa.objects.Reply
import com.example.pulsa.objects.Sub
import com.example.pulsa.objects.User
import com.example.pulsa.utils.MediaUtils
import com.example.pulsa.utils.UserUtils
import com.example.pulsa.utils.glideRequestListener
import com.google.android.material.button.MaterialButton
import io.noties.markwon.Markwon
import io.noties.markwon.movement.MovementMethodPlugin


open class GenericRecyclerAdapter<T : Any>(
    private var items: MutableList<T>,
    private val onClick: ((T, Int) -> Unit)?,
    @LayoutRes val layoutId: Int
) : RecyclerView.Adapter<GenericRecyclerAdapter.GenericViewHolder<T>>() {

    private lateinit var context: Context
    private lateinit var upvote: ((id: Long, pos: Int) -> Unit)
    private lateinit var downvote: ((id: Long, pos: Int) -> Unit)
    private lateinit var subItem: ((sub: Sub, position: Int) -> Unit)
    private lateinit var userPage: ((user: User, position: Int) -> Unit)
    private lateinit var playAudio: ((button: MaterialButton?, mediaUtils: MediaUtils) -> Unit)
    private lateinit var playRecording: ((button: MaterialButton?, mediaUtils: MediaUtils) -> Unit)
    private lateinit var markwon: Markwon

    fun upvoteOnClick(upvoteOnClickListener: (id: Long, pos: Int) -> Unit) {
        upvote = upvoteOnClickListener
    }

    fun downvoteOnClick(downvoteOnClickListener: ((id: Long, pos: Int) -> Unit)) {
        downvote = downvoteOnClickListener
    }

    fun subOnClick(subOnClickListener: ((sub: Sub, position: Int) -> Unit)) {
        subItem = subOnClickListener
    }

    fun userOnClick(userOnClickListener: ((user: User, position: Int) -> Unit)) {
        userPage = userOnClickListener
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
        markwon = Markwon.builder(context)
            // explicit `none` movement method
            .usePlugin(MovementMethodPlugin.none())
            .build()
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
        if (this::subItem.isInitialized) holder.subOnClickListener = subItem
        if (this::userPage.isInitialized) holder.userOnClickListener = userPage

        with(holder) {
            val item = items[position]
            if (item is Post || item is Reply) {
                val content = when (item) {
                    is Post -> item.content
                    is Reply -> item.content
                    else -> null
                }

                if (URLUtil.isValidUrl(content?.audio)) {
                    audio?.visibility = View.VISIBLE
                    playAudioOnClickListener = playAudio
                }
                if (URLUtil.isValidUrl(content?.recording)) {
                    recording?.visibility = View.VISIBLE
                    playRecordingOnClickListener = playRecording
                }
            }

            bind(item, position)

            when (item) {
                is Post -> {
                    markwon.setMarkdown(title!!, item.title)
                    markwon.setMarkdown(text!!, item.content.text)
                }
                is Reply -> {
                    markwon.setMarkdown(text!!, item.content.text)
                }
            }

            when (item) {
                is Post -> item.content.image
                is Reply -> item.content.image
                is Sub -> item.image
                else -> ""
            }.takeIf { URLUtil.isValidUrl(it) }?.let { image ->
                Glide.with(context)
                    .load(image)
                    .listener(glideRequestListener)
                    .into(imageView)
                    .view.visibility = View.VISIBLE
            }


            when (item) {
                is Post -> item.creator.avatar
                is Reply -> item.creator.avatar
                else -> ""
            }.takeIf { URLUtil.isValidUrl(it) }?.let { avatar ->
                this.avatar?.let {
                    Glide.with(context)
                        .load(avatar)
                        .listener(glideRequestListener)
                        .into(it)
                }
            }
        }
    }


    class GenericViewHolder<T>(
        private val binding: ViewDataBinding,
        private val onClick: ((T, position: Int) -> Unit)?,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        val imageView: ImageView = binding.root.findViewWithTag("image")
        var avatar: ImageView? = null
        var title: TextView? = null
        var text: TextView? = null
        var sub: TextView? = null

        var audio: MaterialButton? = binding.root.findViewWithTag("audio")
        var recording: MaterialButton? = binding.root.findViewWithTag("recording")

        lateinit var upvoteOnClickListener: (id: Long, pos: Int) -> Unit
        lateinit var downvoteOnClickListener: (id: Long, pos: Int) -> Unit
        lateinit var subOnClickListener: (sub: Sub, position: Int) -> Unit
        lateinit var userOnClickListener: (user: User, position: Int) -> Unit
        lateinit var playAudioOnClickListener: (button: MaterialButton?, mediaUtils: MediaUtils) -> Unit
        lateinit var playRecordingOnClickListener: (button: MaterialButton?, mediaUtils: MediaUtils) -> Unit

        fun bind(item: T, position: Int) {

            avatar = binding.root.findViewWithTag("avatar")
            title = binding.root.findViewWithTag("title")
            text = binding.root.findViewWithTag("text")

            onClick?.let { listener -> itemView.setOnClickListener { listener(item, position) } }

            when (item) {
                is Post -> {
                    binding.setVariable(BR.postItem, item)
                    title = binding.root.findViewWithTag("post_title")
                    text = binding.root.findViewWithTag("post_text")
                    binding.root.findViewWithTag<TextView>("sub").setOnClickListener {
                        subOnClickListener(item.sub, position)
                    }
                }
                is Reply -> {
                    binding.setVariable(BR.listItem, item)
                    text = binding.root.findViewWithTag("reply_text")
                }
                is Sub -> {
                    binding.setVariable(BR.subItem, item)
                    return
                }
            }

            binding.root.apply {
                findViewWithTag<TextView>("user")
                    .setOnClickListener {
                        userOnClickListener(
                            (item as? Post)?.creator ?: (item as? Reply)?.creator!!,
                            position
                        )
                    }

                setUpMediaUtils(
                    context,
                    "audioVisualizer",
                    audio,
                    (item as? Post)?.content?.audio ?: (item as? Reply)?.content?.audio
                )
                setUpMediaUtils(
                    context,
                    "recordingVisualizer",
                    recording,
                    (item as? Post)?.content?.recording ?: (item as? Reply)?.content?.recording
                )

                findViewWithTag<ImageView>("vote_up").visibility =
                    if (UserUtils.loggedIn()) View.VISIBLE else View.GONE
                findViewWithTag<ImageView>("vote_down").visibility =
                    if (UserUtils.loggedIn()) View.VISIBLE else View.GONE

                if (item is Post) {
                    setupVoteOnClickListener(this, item.postId, position)
                } else if (item is Reply) {
                    setupVoteOnClickListener(this, item.replyId, position)
                }

                val voter = when (item) {
                    is Post -> item.voted.find { it.id == UserUtils.getUserID() }
                    is Reply -> item.voted.find { it.id == UserUtils.getUserID() }
                    else -> null
                }

                val colorResId = when (voter?.vote) {
                    true -> R.color.purple_500
                    false -> R.color.title
                    else -> R.color.white
                }

                findViewWithTag<TextView>("voteCount").setTextColor(context.getColor(colorResId))
            }
        }

        private fun setUpMediaUtils(
            context: Context,
            tag: String,
            button: MaterialButton?,
            url: String?
        ) {
            if (URLUtil.isValidUrl(url)) {
                val mediaUtils = MediaUtils().apply {
                    initMediaPlayerWithUrl(
                        context,
                        binding.root.findViewWithTag(tag),
                        button,
                        url!!
                    )
                }
                button?.setOnClickListener { playAudioOnClickListener(button, mediaUtils) }
            }
        }

        private fun setupVoteOnClickListener(view: View, id: Long, position: Int) {
            view.findViewWithTag<ImageView>("vote_up")
                .setOnClickListener { upvoteOnClickListener(id, position) }
            view.findViewWithTag<ImageView>("vote_down")
                .setOnClickListener { downvoteOnClickListener(id, position) }
        }
    }
}