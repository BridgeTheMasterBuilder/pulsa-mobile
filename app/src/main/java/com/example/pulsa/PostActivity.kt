package com.example.pulsa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.amrdeveloper.treeview.TreeNode
import com.amrdeveloper.treeview.TreeViewAdapter
import com.amrdeveloper.treeview.TreeViewHolder
import com.amrdeveloper.treeview.TreeViewHolderFactory
import com.example.pulsa.databinding.ActivityPostBinding

const val NO_REPLY = -1L

class PostActivity : BaseLayoutActivity() {
    private lateinit var binding: ActivityPostBinding
    private lateinit var adapter: TreeViewAdapter
    private lateinit var post: Post
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
            if ((root.value as Reply).reply_id == id)
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
        val image = R.drawable.pulsa

        this.post = post
        replies = post.replies
        factory = TreeViewHolderFactory { view, _ -> ReplyViewHolder(view, this) }
        adapter = TreeViewAdapter(factory)
        binding.recyclerView.adapter = adapter
        roots = createReplyTree(replies)

        adapter.updateTreeNodes(roots)
        adapter.expandAll()

        binding.postpageImage.setImageResource(image)
        binding.postpageTitle.text = post.title
        binding.postpageText.text = post.content.text

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val reply: Reply? = result.data?.getParcelableExtra("reply")

                    reply?.let { replies.add(reply) }

                    val nestedReply: Reply? = result.data?.getParcelableExtra("nestedReply")

                    nestedReply?.let {
                        val id: Long = result.data?.getLongExtra("replyId", NO_REPLY)!!
                        val node = findNodeById(roots, id)
                        val replies = (node?.value as Reply).replies

                        replies.add(nestedReply)
                    }

                    roots = createReplyTree(replies)
                    adapter.updateTreeNodes(roots)
                    adapter.expandAll()
                }
            }

        launcher = resultLauncher

        binding.replybtn.setOnClickListener {
            val intent = Intent(this, NewReplyActivity::class.java)
            intent.putExtra("sub", this.post.sub)
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
//            if (reply.content.image != "") {
//                Toast.makeText(activity, "${reply.content.image}", Toast.LENGTH_SHORT).show()
//                image.setImageURI(Uri.parse(reply.content.image))
//                image.visibility = View.VISIBLE
//            }

            itemView.findViewById<Button>(R.id.replybtn).setOnClickListener {
                val intent = Intent(activity, NewReplyActivity::class.java)
                intent.putExtra("sub", activity.post.sub)
                intent.putExtra("replyId", (node.value as Reply).reply_id)
                activity.launcher.launch(intent)
            }
        }
    }
}