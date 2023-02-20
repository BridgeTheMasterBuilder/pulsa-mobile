package com.example.pulsa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pulsa.databinding.ActivityNewPostBinding

class NewPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewPostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.postbutton.setOnClickListener {
            val title = binding.newposttitle.text.toString()
            val text = binding.newposttext.text.toString()
            val user = User(null, "Anonymous", null, "Anonymous")

            val intent = Intent(this, PostActivity::class.java)
            val post = Post(
                100,
                title,
                Content(
                    20,
                    text,
                    "test",
                    "test",
                    "recording",
                ),
                user
            )

            intent.putExtra("post", post)
            setResult(Activity.RESULT_OK, intent)
            startActivity(intent)
        }
    }
}