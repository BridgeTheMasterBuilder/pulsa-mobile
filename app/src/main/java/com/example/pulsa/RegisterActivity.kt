package com.example.pulsa

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pulsa.databinding.ActivityRegisterBinding
import java.time.LocalDateTime

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var realname = binding.registerrealname
        var username = binding.registerusername
        var email = binding.registeremail
        var password = binding.registerpassword
        binding.registerpagebutton.setOnClickListener {
            if (realname.toString().isNotBlank() &&
                username.toString().isNotBlank() &&
                email.toString().isNotBlank() &&
                password.toString().isNotBlank()
            ) {
                for (user in UserList.users) {
                    if (user.userName.equals(username.toString())) {
                        Toast.makeText(this, "Username unavailable", Toast.LENGTH_SHORT).show()
                        break
                    } else if (user.email.equals(email.toString())) {
                        Toast.makeText(this, "e-Mail unavailable", Toast.LENGTH_SHORT).show()
                        break
                    } else {
                        val lastuser = UserList.users.last().user_id
                        val newuser = User(
                            lastuser + 1,
                            username.text.toString(),
                            password.text.toString(),
                            realname.text.toString(),
                            "https://res.cloudinary.com/dc6h0nrwk/image/upload/v1668893599/a6zqfrxfflxw5gtspwjr.png",
                            email.text.toString(),
                            mutableListOf(),
                            mutableListOf(),
                            LocalDateTime.now(),
                            LocalDateTime.now()
                        )
                        UserList.users.add(newuser)
                        for (user in UserList.users) {
                            println("++++++++++++++++++++++" + user.userName.toString())
                        }

                        val i = Intent(this, LoginActivity::class.java)
                        startActivity(i)
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
