package com.example.pulsa

import android.os.Bundle
import android.view.View
import android.widget.TextView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = intent.getParcelableExtra<Post>("post")
        val image = R.drawable.pulsa

        post?.let {
            it.replies?.run {
                replies = it.replies
                factory = TreeViewHolderFactory { view, _ -> ReplyViewHolder(view) }
                adapter = TreeViewAdapter(factory)
                binding.recyclerView.adapter = adapter
                roots = createReplyTree(replies)
            }
            binding.postpageImage.setImageResource(image)
            binding.postpageTitle.text = it.title
            binding.postpageText.text = it.content?.text
        }

        adapter.updateTreeNodes(roots)
        adapter.expandAll()
    }

    class ReplyViewHolder(itemView: View) : TreeViewHolder(itemView) {
        private var text = itemView.findViewById<TextView>(R.id.textView)

        override fun bindTreeNode(node: TreeNode) {
            super.bindTreeNode(node)

            val reply = node.value as Reply

            text.text = reply.content?.text
        }
    }
}