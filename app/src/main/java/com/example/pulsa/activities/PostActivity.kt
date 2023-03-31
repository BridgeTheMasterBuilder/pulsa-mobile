package com.example.pulsa.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.amrdeveloper.treeview.TreeNode
import com.amrdeveloper.treeview.TreeViewAdapter
import com.amrdeveloper.treeview.TreeViewHolder
import com.amrdeveloper.treeview.TreeViewHolderFactory
import com.bumptech.glide.Glide
import com.example.pulsa.R
import com.example.pulsa.databinding.ActivityPostBinding
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.Post
import com.example.pulsa.objects.Reply
import com.example.pulsa.utils.glideRequestListener
import com.google.gson.reflect.TypeToken

const val NO_REPLY = -1L

class PostActivity : BaseLayoutActivity(), GestureDetector.OnGestureListener {
    private lateinit var binding: ActivityPostBinding
    private lateinit var adapter: TreeViewAdapter
    private lateinit var post: Post
    private var postPosition = 0;
    private lateinit var replies: MutableList<Reply>
    private lateinit var factory: TreeViewHolderFactory
    private lateinit var roots: MutableList<TreeNode>
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var mDetector: GestureDetectorCompat

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.setOnTouchListener(View.OnTouchListener { v, event ->
            v.performClick()
            mDetector.onTouchEvent(event)
        })

        postPosition = intent.getIntExtra("pos", -1)
        replies = post.replies
        factory = TreeViewHolderFactory { view, _ -> ReplyViewHolder(view, this) }
        adapter = TreeViewAdapter(factory)
        binding.recyclerView.adapter = adapter
        roots = createReplyTree(replies)

        adapter.updateTreeNodes(roots)
        adapter.expandAll()

        binding.postpageTitle.text = post.title
        binding.postpageText.text = post.content.text
        binding.postpageUser.text = "u/${post.creator.username}"
        binding.postpageSub.text = "p/${post.sub.slug}"

        if (URLUtil.isValidUrl(post.creator.avatar)) {
            val circularProgressDrawable = CircularProgressDrawable(this)
            circularProgressDrawable.strokeWidth = 3f
            circularProgressDrawable.centerRadius = 18f
            circularProgressDrawable.start()

            Glide.with(this)
                .load(post.creator.avatar)
                .placeholder(circularProgressDrawable)
                .listener(glideRequestListener)
                .into(binding.postpageAvatarImage)
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
                        post.replies.findReply(parentReply)?.replies?.add(nestedReply)
                    } else if (result.data?.hasExtra("reply")!!) {
                        reply = result.data?.getParcelableExtra<Reply>("reply")!!
                        replies.add(reply)
                        post.replies.add(reply)
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
        private var username = itemView.findViewById<TextView>(R.id.reply_username)
        private var text = itemView.findViewById<TextView>(R.id.reply_text)
        private var image = itemView.findViewById<ImageView>(R.id.reply_image)
        private var avatar = itemView.findViewById<ImageView>(R.id.reply_avatar)

        override fun bindTreeNode(node: TreeNode) {
            super.bindTreeNode(node)

            val reply = node.value as Reply
            image.visibility = View.GONE

            username.text = "u/${reply.creator.username}"
            text.text = reply.content.text

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

            itemView.findViewById<Button>(R.id.postpage_replybutton).setOnClickListener {
                val intent = Intent(activity, NewReplyActivity::class.java)
                intent.putExtra("sub", activity.post.sub)
                intent.putExtra("replyId", (node.value as Reply).replyId)
                intent.putExtra("reply", (node.value as Reply))
                intent.putExtra("post", activity.post)
                activity.launcher.launch(intent)
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
        if (velocityX > 0.0) {
            intent.putExtra("nextPost", true)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else if (velocityX < 0.0) {
            intent.putExtra("prevPost", true)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        return true
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
        replies = post.replies
        factory = TreeViewHolderFactory { view, _ -> ReplyViewHolder(view, this) }
        adapter = TreeViewAdapter(factory)
        binding.recyclerView.adapter = adapter
        roots = createReplyTree(replies)

        adapter.updateTreeNodes(roots)
        adapter.expandAll()

        binding.postpageTitle.text = post.title
        binding.postpageText.text = post.content.text
    }
}
