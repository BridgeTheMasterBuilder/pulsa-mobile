package com.example.pulsa.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pulsa.R
import com.example.pulsa.adapters.ReplyRecyclerAdapter
import com.example.pulsa.databinding.FragmentAccountRepliesBinding
import com.example.pulsa.objects.Reply
import com.example.pulsa.services.PostService

class AccountRepliesFragment : Fragment() {

    private lateinit var binding: FragmentAccountRepliesBinding
    private lateinit var adapter: ReplyRecyclerAdapter<Reply>
    private lateinit var replies: MutableList<Reply>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountRepliesBinding.inflate(inflater, container, false)
        val view = binding.root
        replies = PostService().replies
        adapter = ReplyRecyclerAdapter(replies, ::adapterOnClick, R.layout.reply)
        binding.recyclerView.adapter = adapter
        return view
    }

    private fun adapterOnClick(reply: Reply, position: Int) {
        //fuck
    }

}