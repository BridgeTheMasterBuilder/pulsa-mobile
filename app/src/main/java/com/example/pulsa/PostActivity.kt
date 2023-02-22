package com.example.pulsa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.pulsa.databinding.ActivityPostBinding

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private lateinit var adapter: GenericRecyclerAdapter<Reply>
    private lateinit var replies: MutableList<Reply>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = intent.getParcelableExtra<Post>("post")
        val image = R.drawable.pulsa

        post?.let() {
            it.replies?.let {
                replies = it
                adapter = GenericRecyclerAdapter<Reply>(replies, null, R.layout.reply)
                binding.recyclerView.adapter = adapter
            } ?: run {
                replies = ArrayList<Reply>()
                adapter = GenericRecyclerAdapter<Reply>(
                    replies,
                    null,
                    R.layout.reply
                )
                binding.recyclerView.adapter = adapter
            }
            binding.postpageImage.setImageResource(image)
            binding.postpageTitle.text = it.title
            binding.postpageText.text = it.content?.text
        }

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val reply: Reply? = result.data?.getParcelableExtra("reply")
                    reply?.let { adapter.addItem(reply) }
                }
            }

        binding.replybtn.setOnClickListener {
            val intent = Intent(this, NewReplyActivity::class.java)
            resultLauncher.launch(intent)
        }
    }
}