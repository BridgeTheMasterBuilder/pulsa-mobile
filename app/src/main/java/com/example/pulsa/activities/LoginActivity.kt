package com.example.pulsa.activities

import android.os.Bundle
import com.example.pulsa.R
import com.example.pulsa.databinding.ActivityLoginBinding
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.User
import com.google.gson.reflect.TypeToken

class LoginActivity : BaseLayoutActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginpagebutton.setOnClickListener { loginButtonOnclick() }


        // for (user in UserList.users) {
        //     println("----------------------" + user.username)
        // }
        // binding.loginpagebutton.setOnClickListener {
        //     if (username.toString().isNotBlank() &&
        //         password.toString().isNotBlank()
        //     ) {
        //         for (user in UserList.users) {
        //             println("username: ${user.username}       password:${user.password}")
        //             println("username: ${username.text.toString()}       password:${password.text.toString()}")
        //             println("User of list: ${user.username}  --- Entered: ${username.text.toString()} ----> Equals?${user.username == username.text.toString()}")
        //             if (user.username == username.text.toString()) {
        //                 if (user.password == password.text.toString()) {
        //                     LoggedIn.setLoggedIn(true)
        //                     LoggedIn.setUser(user)
        //                     val i = Intent(this, MainActivity::class.java)
        //                     finish()
        //                     startActivity(i)
        //                 } else {
        //                     Toast.makeText(
        //                         this,
        //                         "Username/Password Invalid lol",
        //                         Toast.LENGTH_SHORT
        //                     )
        //                         .show()
        //                 }
        //             } else {
        //                 Toast.makeText(this, "Username/Password Invalid kek", Toast.LENGTH_SHORT)
        //                     .show()
        //             }
        //         }
        //     } else {
        //         Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        //     }
        // }
    }

    private fun loginButtonOnclick() {
        val map: HashMap<String, Any> = HashMap()

        map["username"] = binding.loginusername.text.toString()
        map["password"] = binding.loginpassword.text.toString()
        map["url"] = "login"
        map["type"] = object : TypeToken<User>() {}
        runOnUiThread { NetworkManager().post(this, map) }
    }

    override fun resolvePost(content: Any) {
        user = content as User

        val tkn = intent.extras?.getString("token")
        val sharedPref =
            this.getSharedPreferences(getString(R.string.user), MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(getString(R.string.token), tkn)
            putString(getString(R.string.avatar), user.avatar)
            putString(getString(R.string.userName), user.username)
            putBoolean(getString(R.string.loggedIn), true)
            apply()
        }

        finish()
    }
}