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

        val post = intent?.getParcelableExtra<Post>("post")
        val image = R.drawable.pulsa

        println(post?.title)
        println(post?.content?.text)


        if (post != null) {
            replies = post.replies
            println(replies.size);
        }

        adapter = PostPageAdapter(replies)
        binding.recyclerView.adapter = adapter


        binding.postpageImage.setImageResource(image)
        binding.postpageText.text = post?.content?.text
        binding.postpageTitle.text = post?.title

    }
}