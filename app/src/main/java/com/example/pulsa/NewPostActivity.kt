package com.example.pulsa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.example.pulsa.databinding.ActivityNewPostBinding

class NewPostActivity : AppCompatActivity() {
    private lateinit var binding : ActivityNewPostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.postbutton.setOnClickListener {
            val title = binding.newposttitle.text.toString()
            val text = binding.newposttext.text.toString()
            val i = Intent(this, PostActivity::class.java)

            i.putExtra("title", title)
            i.putExtra("text", text)
            i.putExtra("sub", "FringeSub")

            startActivity(i)
        }


    }
}