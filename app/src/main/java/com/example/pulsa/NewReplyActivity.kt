package com.example.pulsa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pulsa.databinding.ActivityNewReplyBinding

class NewReplyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewReplyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewReplyBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.postbutton.setOnClickListener {
            val text = binding.newreplytext.text.toString()

            val intent = Intent(this, PostActivity::class.java)
            val reply = Reply(Content(text, "test", "test"))

            intent.putExtra("reply", reply)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}