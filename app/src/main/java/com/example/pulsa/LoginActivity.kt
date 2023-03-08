package com.example.pulsa

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pulsa.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginpagebutton.setOnClickListener {
            var username = binding.loginusername
            var password = binding.loginpassword
            if (!username.equals("admin") || !password.equals("123")) {
                Toast.makeText(this, "Incorrect Username/Password", Toast.LENGTH_SHORT).show()
            } else {
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
            }
        }
    }
}