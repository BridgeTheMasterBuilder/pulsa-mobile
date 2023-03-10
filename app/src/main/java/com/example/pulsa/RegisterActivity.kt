package com.example.pulsa

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pulsa.databinding.ActivityRegisterBinding
import java.time.LocalDateTime

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var users: MutableList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        users = UserService().users
        val realname = binding.registerrealname
        val username = binding.registerusername
        val email = binding.registeremail
        val password = binding.registerpassword
        binding.registerpagebutton.setOnClickListener {
            if (realname.toString().isNotBlank() &&
                username.toString().isNotBlank() &&
                email.toString().isNotBlank() &&
                password.toString().isNotBlank()
            ) {
                for (user in users) {
                    if (user.userName == username.toString()) {
                        Toast.makeText(this, "Username unavailable", Toast.LENGTH_SHORT).show()
                        break
                    } else if (user.email == email.toString()) {
                        Toast.makeText(this, "e-Mail unavailable", Toast.LENGTH_SHORT).show()
                        break
                    } else {
                        val lastuser = users.last().user_id
                        val newuser = User(
                            lastuser + 1,
                            username.toString(),
                            password.toString(),
                            realname.toString(),
                            "https://res.cloudinary.com/dc6h0nrwk/image/upload/v1668893599/a6zqfrxfflxw5gtspwjr.png",
                            email.toString(),
                            mutableListOf(),
                            mutableListOf(),
                            LocalDateTime.now(),
                            LocalDateTime.now()
                        )
                        users.add(newuser)
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
