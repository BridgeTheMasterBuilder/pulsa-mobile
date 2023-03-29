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
import com.amrdeveloper.treeview.TreeNode
import com.amrdeveloper.treeview.TreeViewAdapter
import com.amrdeveloper.treeview.TreeViewHolder
import com.amrdeveloper.treeview.TreeViewHolderFactory
import com.bumptech.glide.Glide
import com.example.pulsa.R
import com.example.pulsa.databinding.ActivityPostBinding
import com.example.pulsa.objects.Post
import com.example.pulsa.objects.Reply
import com.example.pulsa.utils.glideRequestListener

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

    private fun findNodeById(roots: MutableList<TreeNode>, id: Long): TreeNode? {
        fun aux(root: TreeNode, id: Long): TreeNode? {
            if ((root.value as Reply).replyId == id)
                return root
            else if (root.children.size > 0) {
                for (child in root.children) {
                    val result = aux(child, id)

                    if (result != null) {
                        return result
                    }
                }

                return null
            } else return null
        }

        for (root in roots) {
            val result = aux(root, id)

            if (result != null) return result
        }

        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post: Post = intent.getParcelableExtra("post")!!
        if (URLUtil.isValidUrl(post.content.image))
            Glide.with(this)
                .load(post.content.image)
                .listener(glideRequestListener)
                .into(findViewById(binding.postpageImage.id))
                .view.visibility = View.VISIBLE

        this.post = post
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

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val reply: Reply? = result.data?.getParcelableExtra("reply")!!
                    println("Recieving reply ${reply?.content?.text}")
                    reply?.let { it ->
                        replies.add(reply)
                        post.replies.add(reply)
                    }

                    val nestedReply: Reply? = result.data?.getParcelableExtra("nestedReply")

                    nestedReply?.let {
                        val id: Long = result.data?.getLongExtra("replyId", NO_REPLY)!!
                        val node = findNodeById(roots, id)
                        val replies = (node?.value as Reply).replies

                        replies.add(nestedReply)
                        println("nestedReply added to replies")
                    }


                    println("replies array updated on post")
                    post.replies.forEach { it ->
                        println("${it.replyId}: ${it.content.text}")
                    }

                    println("New replies array content")
                    post.replies.forEach { it ->
                        println("${it.replyId}: ${it.content.text}")
                    }
                    roots = createReplyTree(replies)
                    adapter.updateTreeNodes(roots)
                    adapter.expandAll()

                    println("post with new reply added in intent as extra")
                    val intent = intent
                    intent.putExtra("postWithReply", post)
                    intent.putExtra("pos", postPosition)
                    println("added to result")
                    setResult(Activity.RESULT_OK, intent)
                    println("Success setting result ok")
                }
            }

        launcher = resultLauncher

        binding.replybtn.setOnClickListener {
            val intent = Intent(this, NewReplyActivity::class.java)
            intent.putExtra("post", this.post)
            resultLauncher.launch(intent)

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
                if (URLUtil.isValidUrl(reply.content.image))
                    Glide.with(this.activity)
                        .load(reply.content.image)
                        .listener(glideRequestListener)
                        .into(image)
                        .view.visibility = View.VISIBLE
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
}