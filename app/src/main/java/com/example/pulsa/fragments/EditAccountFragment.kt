package com.example.pulsa.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.blankj.utilcode.util.UriUtils
import com.bumptech.glide.Glide
import com.example.pulsa.activities.SubIndexActivity
import com.example.pulsa.activities.UserActivity
import com.example.pulsa.activities.UserPageActivity
import com.example.pulsa.databinding.FragmentEditAccountBinding
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.User
import com.example.pulsa.utils.glideRequestListener
import com.google.gson.reflect.TypeToken

private const val SELECT_PICTURE = 200

class EditAccountFragment : Fragment() {
    private lateinit var binding: FragmentEditAccountBinding
    private lateinit var avatarUri: Uri
    private lateinit var user: User
    private var postCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditAccountBinding.inflate(inflater, container, false)
        val view = binding.root
        val args = this.arguments
        val userName = args?.getString("username")
        val realName = args?.getString("realName")
        val email = args?.getString("email")

        if (userName != null) {
            binding.editaccusername.hint = userName.toString()
            binding.editaccemail.hint = email.toString()
            binding.editaccrealname.hint = realName.toString()
        }

        binding.editaccimgbtn.setOnClickListener {
            val i = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(Intent.createChooser(i, "Select picture"), SELECT_PICTURE)
        }

        binding.editaccsavebtn.setOnClickListener {
            binding.editaccsavebtn.visibility = View.INVISIBLE
            binding.editsubmitting.visibility = View.VISIBLE

            val map = HashMap<String, Any>()
            map["type"] = object : TypeToken<User>() {}
            map["realName"] = binding.editaccrealname.text.toString()
            map["username"] = binding.editaccusername.text.toString()
            map["password"] = binding.editaccpassword.text.toString()
            map["email"] = binding.editaccemail.text.toString()

            if ((map["realName"] as String).isEmpty()) {
                invalidField("Real Name")
                return@setOnClickListener
            }

            if ((map["username"] as String).isEmpty()) {
                invalidField("Username")
                return@setOnClickListener
            }

            if ((map["username"] as String).length < 4) {
                invalidField("Username", "Must not be less than 4 characters")
                return@setOnClickListener
            }

            if ((map["email"] as String).isEmpty()) {
                invalidField("Email")
                return@setOnClickListener
            }

            if (!(map["email"] as String).matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9]+.[A-Za-z]+\$".toRegex())) {
                Toast.makeText(context, "${map["email"]} is not a valid email", Toast.LENGTH_SHORT).show()
                binding.editsubmitting.visibility = View.INVISIBLE
                binding.editaccsavebtn.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if ((map["password"] as String).isNotEmpty() && (map["password"] as String).length < 6) {
                invalidField("Password", "Must not be less than 6 characters")
                return@setOnClickListener
            }


            if ((map["realName"] as String) != user.realName) {
                map["url"] = "user/${this.user.user_id}/edit"
                runOnUiThread { NetworkManager().post(this.context as UserActivity, map) }
                postCount++
            }

            if ((map["username"] as String) != user.username) {
                map["url"] = "user/${this.user.user_id}/edit/username"
                runOnUiThread { NetworkManager().post(this.context as UserActivity, map) }
                postCount++
            }

            if ((map["email"] as String) != user.email) {
                map["url"] = "user/${this.user.user_id}/edit/email"
                runOnUiThread { NetworkManager().post(this.context as UserActivity, map) }
                postCount++
            }

            if ((map["password"] as String).isNotEmpty()) {
                map["url"] = "user/${this.user.user_id}/edit/password"
                runOnUiThread { NetworkManager().post(this.context as UserActivity, map) }
                postCount++
            }

            if (this::avatarUri.isInitialized) {
                map["avatar"] = UriUtils.uri2File(avatarUri)
                map["avatarType"] = this.context?.contentResolver?.getType(avatarUri).toString()
                map["url"] = "user/${this.user.user_id}/edit/avatar"
                runOnUiThread { NetworkManager().post(this.context as UserActivity, map) }
                postCount++
            }

            if (postCount == 0) displayResultMessage("Nothing was changed")
        }

        binding.userpage.setOnClickListener {
            startActivity(Intent(this.context, UserPageActivity::class.java).putExtra("user", user))
        }

        return view
    }

    fun setUserFields(user: User) {
        this.user = user
        if (URLUtil.isValidUrl(user.avatar)) {
            val circularProgressDrawable = CircularProgressDrawable(this.requireContext())
            circularProgressDrawable.strokeWidth = 3f
            circularProgressDrawable.centerRadius = 18f
            circularProgressDrawable.start()

            Glide.with(this)
                .load(user.avatar)
                .placeholder(circularProgressDrawable)
                .listener(glideRequestListener)
                .into(binding.userAvatar)
                .view.visibility = View.VISIBLE
        }
        binding.username.setText(user.username)
        binding.editaccrealname.setText(user.realName)
        binding.editaccusername.setText(user.username)
        binding.editaccemail.setText(user.email)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != AppCompatActivity.RESULT_OK) return

        when (requestCode) {
            SELECT_PICTURE -> {
                data?.data?.let { image ->
                    avatarUri = image
                    binding.userAvatar.apply {
                        setImageURI(image)
                    }
                }
            }
        }
    }

    private fun invalidField(field: String, message: String = "cannot be empty") {
        Toast.makeText(context, "${field} ${message}", Toast.LENGTH_SHORT).show()
        binding.editsubmitting.visibility = View.INVISIBLE
        binding.editaccsavebtn.visibility = View.VISIBLE
    }

    fun getEditCount(): Int {
        return this.postCount
    }

    fun resetEditCount() {
        this.postCount = 0
    }

    fun displayResultMessage(message: String = "Account Edited") {
        binding.editaccsavebtn.visibility = View.VISIBLE
        binding.editsubmitting.visibility = View.INVISIBLE
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}