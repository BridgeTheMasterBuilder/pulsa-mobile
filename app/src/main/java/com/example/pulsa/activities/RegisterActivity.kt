package com.example.pulsa.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.forEach
import com.example.pulsa.databinding.ActivityRegisterBinding
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.User
import com.google.gson.reflect.TypeToken

class RegisterActivity : BaseLayoutActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userDetails: ArrayList<EditText>
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        userDetails = ArrayList<EditText>()
        binding.registerlayout.forEach { view ->
            if (view is EditText) userDetails.add(view)

        }
        binding.registerpagebutton.setOnClickListener { registerButtonOnclick() }

        // var realname = binding.registerrealname
        // var username = binding.registerusername
        // var email = binding.registeremail
        // var password = binding.registerpassword
        // binding.registerpagebutton.setOnClickListener {
        //     if (realname.toString().isNotBlank() &&
        //         username.toString().isNotBlank() &&
        //         email.toString().isNotBlank() &&
        //         password.toString().isNotBlank()
        //     ) {
        //         for (user in UserList.users) {
        //             if (user.username.equals(username.toString())) {
        //                 Toast.makeText(this, "Username unavailable", Toast.LENGTH_SHORT).show()
        //                 break
        //             } else if (user.email.equals(email.toString())) {
        //                 Toast.makeText(this, "e-Mail unavailable", Toast.LENGTH_SHORT).show()
        //                 break
        //             } else {
        //                 val lastuser = UserList.users.last().user_id
        //                 val newuser = User(
        //                     lastuser + 1,
        //                     username.text.toString(),
        //                     password.text.toString(),
        //                     realname.text.toString(),
        //                     "https://res.cloudinary.com/dc6h0nrwk/image/upload/v1668893599/a6zqfrxfflxw5gtspwjr.png",
        //                     email.text.toString(),
        //                     mutableListOf(),
        //                     mutableListOf(),
        //                     LocalDateTime.now(),
        //                     LocalDateTime.now()
        //                 )
        //                 UserList.users.add(newuser)
        //                 for (user in UserList.users) {
        //                     println("++++++++++++++++++++++" + user.username.toString())
        //                 }
//
        //                 val i = Intent(this, LoginActivity::class.java)
        //                 startActivity(i)
        //             }
        //         }
        //     } else {
        //         Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        //     }
        // }
    }

    fun registerButtonOnclick() {
        val map: HashMap<String, Any> = HashMap()

        userDetails.forEach { it ->
            if (it.text.toString().isBlank()) return invalidField(it.hint.toString())
            else map["${it.hint.toString().lowercase()}"] = it.text.toString()
        }
        map["url"] = "register"
        map["type"] = object : TypeToken<User>() {}
        map.forEach { it ->
            println("${it.key}: ${it.value}")
        }
        runOnUiThread { NetworkManager().post(this, map) }

    }

    fun invalidField(field: String) {
        Toast.makeText(applicationContext, "${field} cannot be empty", Toast.LENGTH_SHORT).show()
    }

    override fun resolvePost(content: Any) {
        user = content as User
        println("---------------------USER INFO---------------------")
        println("Id:${user.user_id}")
        println("Real name:${user.realName}")
        println("Username:${user.username}")
        println("Email:${user.email}")
        println("Password:${user.password}")
        finish()
    }
}
