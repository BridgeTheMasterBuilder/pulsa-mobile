package com.example.pulsa.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pulsa.R
import com.example.pulsa.activities.UserActivity
import com.example.pulsa.adapters.GenericRecyclerAdapter
import com.example.pulsa.databinding.FragmentAccountPostsBinding
import com.example.pulsa.objects.Post
import com.example.pulsa.services.PostService


class AccountPostsFragment : Fragment() {
    private lateinit var binding: FragmentAccountPostsBinding
    private lateinit var adapter: GenericRecyclerAdapter<Post>
    private lateinit var posts: MutableList<Post>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountPostsBinding.inflate(inflater, container, false)
        val view = binding.root
        posts = PostService().posts
        adapter = GenericRecyclerAdapter(posts, ::adapterOnClick, R.layout.post_item)
        binding.recyclerView.adapter = adapter
        return view
    }

    private fun adapterOnClick(post: Post, position: Int) {
        (activity as UserActivity).startPostActivity(post, position)
    }

}
