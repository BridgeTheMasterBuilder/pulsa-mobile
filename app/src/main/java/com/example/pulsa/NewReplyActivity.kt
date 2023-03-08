package com.example.pulsa

import android.app.Activity
import android.os.Bundle
import com.example.pulsa.databinding.ActivityNewReplyBinding
import java.time.LocalDateTime

class NewReplyActivity : BaseLayoutActivity() {
    private lateinit var binding: ActivityNewReplyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewReplyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.postbutton.setOnClickListener {
            val text = binding.newreplytext.text.toString()
            val user = User(
                1,
                "Anonymous",
                "",
                "Anonymous",
                "https://res.cloudinary.com/dc6h0nrwk/image/upload/v1668893599/a6zqfrxfflxw5gtspwjr.png",
                "",
                mutableListOf(),
                mutableListOf(),
                LocalDateTime.now(),
                LocalDateTime.now()
            )

            val intent = intent
            val reply = Reply(
                35,
                Content(
                    36,
                    text,
                    "test",
                    "test",
                    "recording",
                    LocalDateTime.now(),
                    LocalDateTime.now()
                ),
                user,
                intent.getParcelableExtra("sub")!!,
                0,
                mutableListOf(),
                mutableListOf(),
                LocalDateTime.now(),
                LocalDateTime.now()
            )

            if (intent.getLongExtra("replyId", NO_REPLY) != NO_REPLY) {
                intent.putExtra("nestedReply", reply)
            } else {
                intent.putExtra("reply", reply)
            }

            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}