package com.example.pulsa.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.webkit.URLUtil
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.amrdeveloper.treeview.*
import com.bumptech.glide.Glide
import com.example.pulsa.R
import com.example.pulsa.databinding.ActivityPostBinding
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.Post
import com.example.pulsa.objects.Reply
import com.example.pulsa.utils.MediaUtils
import com.example.pulsa.utils.UserUtils
import com.example.pulsa.utils.glideRequestListener
import com.google.android.material.button.MaterialButton
import com.google.gson.reflect.TypeToken
import io.noties.markwon.Markwon

const val NO_REPLY = -1L
private const val MEDIA_PLAY = R.drawable.icons8_play_96
private const val MEDIA_STOPPED = "stopped"
private const val TOLERANCE = 30.0

class PostActivity : BaseLayoutActivity(), GestureDetector.OnGestureListener {
    private lateinit var binding: ActivityPostBinding
    private lateinit var adapter: TreeViewAdapter
    private lateinit var post: Post
    private var postPosition = 0;
    private lateinit var replies: MutableList<Reply>
    private lateinit var factory: TreeViewHolderFactory
    private lateinit var treeNodeManager: TreeNodeManager
    private lateinit var roots: MutableList<TreeNode>
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var mDetector: GestureDetectorCompat
    private var mediaUtilsArray = arrayOf<Pair<MediaUtils, MaterialButton?>>()
    private lateinit var markwon: Markwon
    private var ypos = 0
    private var delay = 0
    private var postHidden = false

    private fun createReplyTree(replies: MutableList<Reply>): MutableList<TreeNode> {
        fun aux(child: TreeNode, replies: MutableList<Reply>) {
            for (reply in replies) {
                val grandChild = TreeNode(reply, R.layout.reply)
                child.addChild(grandChild)

                aux(grandChild, reply.replies)
            }
        }

        val roots = mutableListOf<TreeNode>()

        for (reply in replies) {
            val child = TreeNode(reply, R.layout.reply)
            roots.add(child)

            aux(child, reply.replies)
        }

        return roots
    }

    private fun MutableList<Reply>.findReply(parentReply: Reply): Reply? {
        for (reply in this) {
            if (reply.replyId == parentReply.replyId) {
                return reply
            } else {
                val foundReply = reply.replies.findReply(parentReply)
                if (foundReply != null) {
                    return foundReply
                }
            }
        }
        return null
    }

    private fun MutableList<Reply>.updateReply(updatedReply: Reply): Reply? {
        var index = 0
        for (reply in this) {
            if (reply.replyId == updatedReply.replyId) {
                this[index] = updatedReply
                return reply
            } else {
                val foundReply = reply.replies.updateReply(updatedReply)
                if (foundReply != null) {
                    return foundReply
                }
            }
            index++
        }
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        treeNodeManager = TreeNodeManager()
        val post: Post = intent.getParcelableExtra("post")!!
        runOnUiThread {
            NetworkManager().get(
                this,
                hashMapOf(
                    "type" to object : TypeToken<Post>() {},
                    "url" to "p/${post.sub.slug}/${post.postId}"
                )
            )
        }

        markwon = Markwon.create(this)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.postMainLayout.setOnTouchListener(View.OnTouchListener { v, event ->
            v.performClick()
            mDetector.onTouchEvent(event)
        })

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (delay > 0) {
                    delay -= 1;
                    return
                }

                ypos = if (recyclerView.canScrollVertically(-1)) {
                    ypos + dy
                } else {
                    0
                }

                Log.println(Log.ERROR, "", "${ypos}")

                if (ypos > 100 && !postHidden) {
                    hidePost()
                    postHidden = true
                    delay = 10
                } else if (ypos == 0) {
                    unhidePost()
                    postHidden = false
                    delay = 10
                }
            }
        })

        if (UserUtils.loggedIn()) {
            binding.postVoteUp.visibility = View.VISIBLE
            binding.postVoteDown.visibility = View.VISIBLE

            binding.postVoteUp.setOnClickListener {
                runOnUiThread {
                    NetworkManager().post(
                        this, hashMapOf(
                            "url" to "p/${post.postId}/upvote",
                            "type" to object : TypeToken<Post>() {},
                            "vote" to ""
                        )
                    )
                }
            }

            binding.postVoteDown.setOnClickListener {
                runOnUiThread {
                    NetworkManager().post(
                        this, hashMapOf(
                            "url" to "p/${post.postId}/downvote",
                            "type" to object : TypeToken<Post>() {},
                            "vote" to ""
                        )
                    )
                }
            }

        } else {
            binding.postVoteUp.visibility = View.GONE
            binding.postVoteDown.visibility = View.GONE
        }

        if (URLUtil.isValidUrl(post.content.audio)) {
            val button = binding.postPlayaudiobutton
            button.visibility = View.VISIBLE
            val mediaUtils = MediaUtils()
            val mediaPlayer = mediaUtils.initMediaPlayerWithUrl(
                this,
                binding.root.findViewWithTag("audioVisualizer"),
                button as MaterialButton,
                post.content.audio
            )
            binding.postPlayaudiobutton.setOnClickListener {
                val containsTuple = mediaUtilsArray.any { (util, _) -> util == mediaUtils }
                if (!containsTuple) mediaUtilsArray =
                    mediaUtilsArray.plusElement(mediaUtils to button)
                mediaUtils.playMedia(button)
            }
        }

        if (URLUtil.isValidUrl(post.content.recording)) {
            val button = binding.postPlayrecordingbutton
            button.visibility = View.VISIBLE
            val mediaUtils = MediaUtils()
            val mediaPlayer = mediaUtils.initMediaPlayerWithUrl(
                this,
                binding.root.findViewWithTag("recordingVisualizer"),
                button as MaterialButton,
                post.content.recording
            )
            binding.postPlayrecordingbutton.setOnClickListener {
                val containsTuple = mediaUtilsArray.any { (util, _) -> util == mediaUtils }
                if (!containsTuple) mediaUtilsArray =
                    mediaUtilsArray.plusElement(mediaUtils to button)
                mediaUtils.playMedia(button)
            }
        }

        postPosition = intent.getIntExtra("pos", -1)
        replies = post.replies
        setupReplyTree()

        markwon.setMarkdown(binding.postpageTitle, post.title)
        markwon.setMarkdown(binding.postpageText, post.content.text)
        binding.postpageUser.text = "u/${post.creator.username}"
        binding.postpageSub.text = "p/${post.sub.name}"
        binding.postVoteCount.text = post.vote.toString()

        if (URLUtil.isValidUrl(post.creator.avatar)) {
            val circularProgressDrawable = CircularProgressDrawable(this)
            circularProgressDrawable.strokeWidth = 3f
            circularProgressDrawable.centerRadius = 18f
            circularProgressDrawable.start()

            Glide.with(this)
                .load(post.creator.avatar)
                .placeholder(circularProgressDrawable)
                .listener(glideRequestListener)
                .into(binding.postpageAvatar)
                .view.visibility = View.VISIBLE
        }

        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    var reply: Reply? = null

                    if (result.data?.hasExtra("nestedReply")!!) {
                        val parentReply = result.data?.getParcelableExtra<Reply>("reply")!!
                        val nestedReply = result.data?.getParcelableExtra<Reply>("nestedReply")!!
                        replies.findReply(parentReply)?.replies?.add(nestedReply)
                        post.replies = replies

                    } else if (result.data?.hasExtra("reply")!!) {
                        reply = result.data?.getParcelableExtra<Reply>("reply")!!
                        replies.add(reply)
                        post.replies = replies
                    }

                    roots = createReplyTree(replies)
                    adapter.updateTreeNodes(roots)
                    adapter.expandAll()

                    val intent = intent
                    intent.putExtra("postWithReply", post)
                    intent.putExtra("pos", postPosition)
                    setResult(Activity.RESULT_OK, intent)
                }
            }

        binding.postpageReplybutton.setOnClickListener {
            val intent = Intent(this, NewReplyActivity::class.java)
            intent.putExtra("post", this.post)
            launcher.launch(intent)
        }

        mDetector = GestureDetectorCompat(this, this)
    }

    class ReplyViewHolder(itemView: View, private var activity: PostActivity) :
        TreeViewHolder(itemView) {
        private var line = itemView.findViewById<ImageView>(R.id.vertical_line)
        private var username = itemView.findViewById<TextView>(R.id.reply_username)
        private var text = itemView.findViewById<TextView>(R.id.reply_text)
        private var image = itemView.findViewById<ImageView>(R.id.reply_image)
        private var avatar = itemView.findViewById<ImageView>(R.id.reply_avatar)
        private var votes = itemView.findViewById<TextView>(R.id.reply_vote_count)
        private var upvote = itemView.findViewById<ImageView>(R.id.reply_vote_up)
        private var downvote = itemView.findViewById<ImageView>(R.id.reply_vote_down)
        override fun bindTreeNode(node: TreeNode) {
            super.bindTreeNode(node)

            val reply = node.value as Reply

            if (node.isExpanded) {
                line.setImageResource(R.drawable.vertical_line)
            } else {
                line.setImageResource(R.drawable.baseline_playlist_add_24)
            }

            image.visibility = View.GONE

            username.text = "u/${reply.creator.username}"
            votes.text = reply.vote.toString()
            activity.markwon.setMarkdown(text, reply.content.text)

            if (UserUtils.loggedIn()) {
                upvote.visibility = View.VISIBLE
                downvote.visibility = View.VISIBLE
            } else {
                upvote.visibility = View.GONE
                downvote.visibility = View.GONE
            }

            if (reply.content.image != "") {
                if (URLUtil.isValidUrl(reply.content.image)) {

                    Glide.with(this.activity)
                        .load(reply.content.image)
                        .listener(glideRequestListener)
                        .into(image)
                        .view.visibility = View.VISIBLE
                }
            }

            if (reply.creator.avatar != "") {
                if (URLUtil.isValidUrl(reply.creator.avatar)) {

                    Glide.with(this.activity)
                        .load(reply.creator.avatar)
                        .listener(glideRequestListener)
                        .into(avatar)
                        .view.visibility = View.VISIBLE
                }
            }

            itemView.findViewById<ImageView>(R.id.reply_vote_up).setOnClickListener {
                activity.runOnUiThread {
                    NetworkManager().post(
                        activity, hashMapOf(
                            "url" to "r/${reply.replyId}/upvote",
                            "type" to object : TypeToken<Reply>() {},
                            "vote" to ""
                        )
                    )
                }
            }

            itemView.findViewById<ImageView>(R.id.reply_vote_down).setOnClickListener {
                activity.runOnUiThread {
                    NetworkManager().post(
                        activity, hashMapOf(
                            "url" to "r/${reply.replyId}/downvote",
                            "type" to object : TypeToken<Reply>() {},
                            "vote" to ""
                        )
                    )
                }
            }

            itemView.findViewById<Button>(R.id.postpage_replybutton).setOnClickListener {
                val intent = Intent(activity, NewReplyActivity::class.java)
                intent.putExtra("sub", activity.post.sub)
                intent.putExtra("replyId", (node.value as Reply).replyId)
                intent.putExtra("reply", (node.value as Reply))
                intent.putExtra("post", activity.post)
                activity.launcher.launch(intent)
            }

            if (URLUtil.isValidUrl(reply.content.audio)) {
                val button = itemView.findViewById<Button>(R.id.reply_playaudiobutton)
                button.visibility = View.VISIBLE
                val mediaUtils = MediaUtils()
                val mediaPlayer = mediaUtils.initMediaPlayerWithUrl(
                    activity,
                    itemView.findViewWithTag("audioVisualizer"),
                    button as MaterialButton,
                    reply.content.audio
                )
                button.setOnClickListener {
                    val containsTuple =
                        activity.mediaUtilsArray.any { (util, _) -> util == mediaUtils }
                    if (!containsTuple) activity.mediaUtilsArray =
                        activity.mediaUtilsArray.plusElement(mediaUtils to button)
                    mediaUtils.playMedia(button)
                }
            }

            if (URLUtil.isValidUrl(reply.content.recording)) {
                val button = itemView.findViewById<Button>(R.id.reply_playrecordingbutton)
                button.visibility = View.VISIBLE
                val mediaUtils = MediaUtils()
                val mediaPlayer = mediaUtils.initMediaPlayerWithUrl(
                    activity,
                    itemView.findViewWithTag("recordingVisualizer"),
                    button as MaterialButton,
                    reply.content.recording
                )
                button.setOnClickListener {
                    val containsTuple =
                        activity.mediaUtilsArray.any { (util, _) -> util == mediaUtils }
                    if (!containsTuple) activity.mediaUtilsArray =
                        activity.mediaUtilsArray.plusElement(mediaUtils to button)
                    mediaUtils.playMedia(button)
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun onFling(
        event1: MotionEvent,
        event2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }

    override fun onDown(p0: MotionEvent): Boolean {
        return true
    }

    override fun onShowPress(p0: MotionEvent) {
    }

    override fun onLongPress(p0: MotionEvent) {
    }

    override fun onScroll(
        event1: MotionEvent,
        event2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (velocityX > TOLERANCE) {
            intent.putExtra("nextContent", true)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else if (velocityX < -TOLERANCE) {
            intent.putExtra("prevContent", true)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        return false
    }

    override fun onSingleTapUp(p0: MotionEvent): Boolean {
        return false
    }

    override fun resolveGet(content: Any) {
        post = content as Post

        if (URLUtil.isValidUrl(post.content.image)) {
            val circularProgressDrawable = CircularProgressDrawable(this)
            circularProgressDrawable.strokeWidth = 15f
            circularProgressDrawable.centerRadius = 90f
            circularProgressDrawable.start()

            Glide.with(this)
                .load(post.content.image)
                .placeholder(circularProgressDrawable)
                .listener(glideRequestListener)
                .into(findViewById(binding.postpageImage.id))
                .view.visibility = View.VISIBLE
        }

        postPosition = intent.getIntExtra("pos", -1)
        markwon.setMarkdown(binding.postpageTitle, post.title)
        markwon.setMarkdown(binding.postpageText, post.content.text)
        replies = post.replies

        audioSetup()
        setupReplyTree()
    }

    private fun audioSetup() {

    }

    private fun setupReplyTree() {
        treeNodeManager.clearNodes()
        factory = TreeViewHolderFactory { view, _ -> ReplyViewHolder(view, this) }
        adapter = TreeViewAdapter(factory, treeNodeManager)
        binding.recyclerView.adapter = adapter
        roots = createReplyTree(replies)

        adapter.updateTreeNodes(roots)
        adapter.expandAll()
    }

    override fun resolvePost(content: Any) {
        when (content) {
            is Post -> {
                post = content
                binding.postVoteCount.text = post.vote.toString()

            }

            is Reply -> {
                replies.updateReply(content)
                post.replies = replies

                roots = createReplyTree(replies)
                adapter.updateTreeNodes(roots)
                adapter.expandAll()
            }
        }

        val intent = intent
        intent.putExtra("postWithReply", post)
        intent.putExtra("pos", postPosition)
        setResult(RESULT_OK, intent)
    }

    override fun onStop() {
        super.onStop()

        mediaUtilsArray.forEach { pair ->
            pair.first.medPlayer?.stop()
            pair.second?.tag = MEDIA_STOPPED
            pair.second?.setIconResource(MEDIA_PLAY)
        }
    }

    private fun hidePost() {
        binding.postPostContainer.visibility = View.GONE

    }

    private fun unhidePost() {
        binding.postPostContainer.visibility = View.VISIBLE
    }
}


// inline fun <reified T> setupVoting(upvote: View, downvote: View, id: Long) {
//     upvote.visibility = View.VISIBLE
//     downvote.visibility = View.VISIBLE
//     var param = ""
//
//     when (T::class) {
//         Post::class -> param = "p"
//         Reply::class -> param = "r"
//     }
//
//     if (!upvote.hasOnClickListeners()) {
//         upvote.setOnClickListener {
//             runOnUiThread {
//                 NetworkManager().post(
//                     this, hashMapOf(
//                         "url" to "${param}/${id}/upvote",
//                         "type" to object : TypeToken<T>() {},
//                         "vote" to ""
//                     )
//                 )
//             }
//         }
//     }
//
//     if (!downvote.hasOnClickListeners()) {
//         downvote.setOnClickListener {
//             runOnUiThread {
//                 NetworkManager().post(
//                     this, hashMapOf(
//                         "url" to "${param}/${id}/downvote",
//                         "type" to object : TypeToken<T>() {},
//                         "vote" to ""
//                     )
//                 )
//             }
//         }
//     }
// }