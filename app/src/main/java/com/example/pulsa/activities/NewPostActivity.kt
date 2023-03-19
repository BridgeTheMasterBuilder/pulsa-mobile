package com.example.pulsa.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.pulsa.databinding.ActivityNewPostBinding
import com.example.pulsa.objects.Content
import com.example.pulsa.objects.Post
import com.example.pulsa.objects.Sub
import com.example.pulsa.objects.User
import java.time.LocalDateTime

class NewPostActivity : BaseLayoutActivity() {
    private lateinit var binding: ActivityNewPostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.postbutton.setOnClickListener {
            val title = binding.newposttitle.text.toString()
            val text = binding.newposttext.text.toString()
            val user = User(
                -1,
                "Anonymous",
                "",
                "Anonymous",
                "",
                "",
                mutableListOf(),
                mutableListOf(),
                LocalDateTime.now(),
                LocalDateTime.now()
            )

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
                    LocalDateTime.now(),
                    LocalDateTime.now()
                ),
                user,
                Sub(-1, "", "", mutableListOf(), "", 0),
                0,
                mutableListOf(),
                mutableListOf(),
                LocalDateTime.now(),
                LocalDateTime.now()
            )

            intent.putExtra("post", post)
            setResult(Activity.RESULT_OK, intent)
            finish()
            startActivity(intent)

        }
    }
}