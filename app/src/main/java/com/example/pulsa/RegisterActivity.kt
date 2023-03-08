package com.example.pulsa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.pulsa.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerpagebutton.setOnClickListener {
            var realname = binding.registerrealname
            var username = binding.registerusername
            var email = binding.registeremail
            var password = binding.registerpassword
            if(username.equals("IceHot1")){
                Toast.makeText(this, "Username Unavailable", Toast.LENGTH_SHORT).show()
            }else if( email.equals("lol@email.com")){
                Toast.makeText(this, "e-Mail already registered", Toast.LENGTH_SHORT).show()
            }else if(realname.equals("") || username.equals("") || email.equals("") || password.equals("") ) {
                Toast.makeText(this, "Fill all fields please", Toast.LENGTH_SHORT).show()
            }else{
                val i = Intent(this, LoginActivity::class.java)
                startActivity(i)
            }
        }
    }
}