package com.example.pulsa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pulsa.databinding.ActivityPostBinding

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private lateinit var adapter: PostPageAdapter
    private lateinit var replies: ArrayList<Reply>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = intent.getParcelableExtra<Post>("post")
        val image = R.drawable.pulsa

        if (post != null)
            replies = post.replies
        adapter = PostPageAdapter(replies)

        binding.postpageImage.setImageResource(image)
        binding.postpageText.text = post?.content?.text
        binding.postpageTitle.text = post?.title
        binding.recyclerView.adapter = adapter
    }
}