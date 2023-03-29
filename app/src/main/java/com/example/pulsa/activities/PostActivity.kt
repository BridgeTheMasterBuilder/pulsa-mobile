package com.example.pulsa.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.URLUtil
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

class PostActivity : BaseLayoutActivity() {
    private lateinit var binding: ActivityPostBinding
    private lateinit var adapter: TreeViewAdapter
    private lateinit var post: Post
    private var postPosition = 0;
    private lateinit var replies: MutableList<Reply>
    private lateinit var factory: TreeViewHolderFactory
    private lateinit var roots: MutableList<TreeNode>
    private lateinit var launcher: ActivityResultLauncher<Intent>

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

        binding.replybtn.setOnClickListener {
            val intent = Intent(this, NewReplyActivity::class.java)
            intent.putExtra("post", this.post)
            launcher.launch(intent)
        }
    }

    class ReplyViewHolder(itemView: View, private var activity: PostActivity) :
        TreeViewHolder(itemView) {
        private var text = itemView.findViewById<TextView>(R.id.textView)
        private var image = itemView.findViewById<ImageView>(R.id.imageAttachment)

        override fun bindTreeNode(node: TreeNode) {
            super.bindTreeNode(node)

            val reply = node.value as Reply
            image.visibility = View.GONE

            text.text = reply.content.text
            if (reply.content.image != "") {
                Toast.makeText(activity, reply.content.image, Toast.LENGTH_SHORT).show()
                if (URLUtil.isValidUrl(reply.content.image)) {
                    val circularProgressDrawable = CircularProgressDrawable(activity)
                    circularProgressDrawable.strokeWidth = 5f
                    circularProgressDrawable.centerRadius = 30f
                    circularProgressDrawable.start()

                    Glide.with(this.activity)
                        .load(reply.content.image)
                        .placeholder(circularProgressDrawable)
                        .listener(glideRequestListener)
                        .into(image)
                        .view.visibility = View.VISIBLE
                }
            }

            itemView.findViewById<Button>(R.id.replybtn).setOnClickListener {
                val intent = Intent(activity, NewReplyActivity::class.java)
                intent.putExtra("sub", activity.post.sub)
                intent.putExtra("replyId", (node.value as Reply).replyId)
                intent.putExtra("reply", (node.value as Reply))
                intent.putExtra("post", activity.post)
                activity.launcher.launch(intent)
            }
        }
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
