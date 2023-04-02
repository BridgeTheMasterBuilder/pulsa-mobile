package com.example.pulsa.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pulsa.R
import com.example.pulsa.adapters.GenericRecyclerAdapter
import com.example.pulsa.databinding.FragmentAccountRepliesBinding
import com.example.pulsa.objects.Reply
import com.example.pulsa.services.PostService

class AccountRepliesFragment : Fragment() {

    private lateinit var binding: FragmentAccountRepliesBinding
    private lateinit var adapter: GenericRecyclerAdapter<Reply>
    private lateinit var posts: MutableList<Reply>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountRepliesBinding.inflate(inflater, container, false)
        val view = binding.root
        posts = PostService().replies
        adapter = GenericRecyclerAdapter(posts, ::adapterOnClick, R.layout.post_item)
        binding.recyclerView.adapter = adapter
        return view
    }

    private fun adapterOnClick(reply: Reply, position: Int) {
        //fuck
    }

}