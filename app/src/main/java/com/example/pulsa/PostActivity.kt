package com.example.pulsa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.pulsa.databinding.ActivityPostBinding

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private lateinit var adapter: PostPageAdapter
    private lateinit var replies: MutableList<Reply>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = intent.getParcelableExtra<Post>("post")
        val image = R.drawable.pulsa

        if (post != null)
            replies = post.replies!!

        adapter = PostPageAdapter(replies)

        binding.postpageImage.setImageResource(image)
        binding.postpageText.text = post?.content?.text
        binding.postpageTitle.text = post?.title
        binding.recyclerView.adapter = adapter

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val reply: Reply? = result.data?.getParcelableExtra("reply")
                    if (reply != null) replies.add(reply)
                    adapter.notifyDataSetChanged()
                }
            }

        binding.replybtn.setOnClickListener {
            val intent = Intent(this, NewReplyActivity::class.java)
            resultLauncher.launch(intent)
        }
    }
}