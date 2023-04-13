package com.example.pulsa.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.forEach
import com.example.pulsa.databinding.ActivityRegisterBinding
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.User
import com.google.gson.reflect.TypeToken
import okhttp3.Response

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
    }

    fun registerButtonOnclick() {
        val map: HashMap<String, Any> = HashMap()

        userDetails.forEach { it ->
            if (it.text.toString().isBlank()) return invalidField(it.hint.toString())
            else map["${it.hint.toString().lowercase()}"] = it.text.toString()
        }
        map["url"] = "register"
        map["type"] = object : TypeToken<User>() {}

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

    override fun resolveFailure(response: Response) {
        println("Error: " + response.code)
    }
}
