package com.example.pulsa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pulsa.databinding.ActivityPostBinding

class PostActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = intent.getParcelableExtra<Post>("post")
        val image = R.drawable.pulsa

        binding.postpageImage.setImageResource(image)
        binding.postpageText.text = post?.content?.text
        binding.postpageTitle.text = post?.title

    }
}