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

        val title = intent.getStringExtra("title")
        val text = intent.getStringExtra("text")
        val sub = intent.getStringExtra("sub")
        val image = R.drawable.pulsa

        binding.postpageImage.setImageResource(image)
        binding.postpageText.text = text
        binding.postpageTitle.text = title

    }
}