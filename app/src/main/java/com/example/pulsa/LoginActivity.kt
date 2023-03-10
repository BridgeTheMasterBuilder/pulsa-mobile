package com.example.pulsa

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pulsa.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var users: MutableList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val username = binding.loginusername
        val password = binding.loginpassword
        users = UserService().users
        val sp = getSharedPreferences("LOGIN", MODE_PRIVATE)
        binding.loginpagebutton.setOnClickListener {
            if (username.toString().isNotBlank() &&
                password.toString().isNotBlank()
            ) {
                for (user in users) {
                    if (user.userName == username.toString()) {
                        if (user.password == password.toString()) {
                            val editor = sp.edit()
                            editor.putString("USER", username.toString())
                            editor.apply()
                            val i = Intent(this, MainActivity::class.java)
                            startActivity(i)
                        } else {
                            Toast.makeText(this, "Username/Password Invalid", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(this, "Username/Password Invalid", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}