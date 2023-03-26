package com.example.pulsa.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pulsa.databinding.ActivityLoginBinding
import com.example.pulsa.utils.LoggedIn
import com.example.pulsa.utils.UserList

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var username = binding.loginusername
        var password = binding.loginpassword


        for (user in UserList.users) {
            println("----------------------" + user.username)
        }
        binding.loginpagebutton.setOnClickListener {
            if (username.toString().isNotBlank() &&
                password.toString().isNotBlank()
            ) {
                for (user in UserList.users) {
                    println("username: ${user.username}       password:${user.password}")
                    println("username: ${username.text.toString()}       password:${password.text.toString()}")
                    println("User of list: ${user.username}  --- Entered: ${username.text.toString()} ----> Equals?${user.username == username.text.toString()}")
                    if (user.username == username.text.toString()) {
                        if (user.password == password.text.toString()) {
                            LoggedIn.setLoggedIn(true)
                            LoggedIn.setUser(user)
                            val i = Intent(this, MainActivity::class.java)
                            finish()
                            startActivity(i)
                        } else {
                            Toast.makeText(
                                this,
                                "Username/Password Invalid lol",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else {
                        Toast.makeText(this, "Username/Password Invalid kek", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}