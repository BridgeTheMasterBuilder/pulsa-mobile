package com.example.pulsa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.amrdeveloper.treeview.TreeNode
import com.amrdeveloper.treeview.TreeViewAdapter
import com.amrdeveloper.treeview.TreeViewHolder
import com.amrdeveloper.treeview.TreeViewHolderFactory
import com.example.pulsa.databinding.ActivityPostBinding

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private lateinit var adapter: TreeViewAdapter
    private lateinit var replies: MutableList<Reply>
    private lateinit var factory: TreeViewHolderFactory
    private lateinit var roots: MutableList<TreeNode>
    private lateinit var launcher: ActivityResultLauncher<Intent>

    private fun createReplyTree(replies: MutableList<Reply>): MutableList<TreeNode> {
        fun aux(child: TreeNode, replies: MutableList<Reply>) {
            for (reply in replies) {
                val grandChild = TreeNode(reply, R.layout.reply)
                child.addChild(grandChild)

                reply.replies?.let {
                    aux(grandChild, it)
                }
            }
        }

        val roots = mutableListOf<TreeNode>()

        for (reply in replies) {
            val child = TreeNode(reply, R.layout.reply)
            roots.add(child)

            reply.replies?.let {
                aux(child, it)
            }
        }

        return roots
    }

    fun findNodeById(roots: MutableList<TreeNode>, id: Long): TreeNode? {
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

    fun expandAll(roots: MutableList<TreeNode>) {
        fun expand(root: TreeNode) {
            for (child in root.children) {
                child.isExpanded = true

                expand(child)
            }

        }

        for (root in roots) {
            root.isExpanded = true

            expand(root)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val intent = Intent(this, NewReplyActivity::class.java)
//
//        val reply1 = intent.getParcelableExtra<Reply>("reply")
//
//        reply1?.let {
//            print(reply1)
//        }

        val post = intent.getParcelableExtra<Post>("post")
        val image = R.drawable.pulsa

        val activity = this

        post?.let {
            it.replies?.run {
                replies = it.replies
                factory = TreeViewHolderFactory { view, _ -> ReplyViewHolder(view, activity) }
                adapter = TreeViewAdapter(factory)
                binding.recyclerView.adapter = adapter
                roots = createReplyTree(replies)

                adapter.updateTreeNodes(roots)
                adapter.expandAll()
            }
            binding.postpageImage.setImageResource(image)
            binding.postpageTitle.text = it.title
            binding.postpageText.text = it.content?.text
        }

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val reply: Reply? = result.data?.getParcelableExtra("reply")
                    reply?.let {
                        replies.add(reply)
//                        roots.add(TreeNode(reply, R.layout.reply));
                    }
                    val reply_reply: Reply? = result.data?.getParcelableExtra("replyReply")
                    reply_reply?.let {
                        val id: Long? = result.data?.getLongExtra("replyId", -1)

                        id?.let {
                            val node = findNodeById(roots, it)

                            var replies = (node?.value as Reply).replies

                            if (replies != null) {
                                (node?.value as Reply).replies?.add(reply_reply)
                            } else {
                                (node?.value as Reply).replies = mutableListOf(reply_reply)
                            }
//                            node?.addChild(TreeNode(reply_reply, R.layout.reply))
                        }
                    }
//                    adapter.clearTreeNodes()
////                    adapter.treeNodes = roots
//                    adapter.updateTreeNodes(roots)
//                    adapter.expandAll()
                    roots = createReplyTree(replies)

                    adapter.updateTreeNodes(roots)
                    adapter.expandAll()
                }
            }

        launcher = resultLauncher

        binding.replybtn.setOnClickListener {
            val intent = Intent(this, NewReplyActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    class ReplyViewHolder(itemView: View, activity: PostActivity) : TreeViewHolder(itemView) {
        private var text = itemView.findViewById<TextView>(R.id.textView)
        private var activity = activity

        override fun bindTreeNode(node: TreeNode) {
            super.bindTreeNode(node)

            val reply = node.value as Reply

            text.text = reply.content?.text

            itemView.findViewById<Button>(R.id.replybtn).setOnClickListener {
                val intent = Intent(activity, NewReplyActivity::class.java)
                intent.putExtra("replyId", (node.value as Reply).reply_id)
                activity.launcher.launch(intent)
            }
        }
    }
}