package com.example.pulsa.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.pulsa.databinding.FragmentEditAccountBinding
import com.example.pulsa.objects.User
import com.example.pulsa.utils.glideRequestListener

class EditAccountFragment : Fragment() {
    private lateinit var binding: FragmentEditAccountBinding
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
        val avatar = args?.getString("avatar")

        if (userName != null) {
            binding.editaccusername.hint = userName.toString()
            binding.editaccemail.hint = email.toString()
            binding.editaccrealname.hint = realName.toString()
            if (URLUtil.isValidUrl(avatar)) {
                val circularProgressDrawable = CircularProgressDrawable(requireContext())
                circularProgressDrawable.strokeWidth = 3f
                circularProgressDrawable.centerRadius = 18f
                circularProgressDrawable.start()

                Glide.with(this)
                    .load(avatar)
                    .placeholder(circularProgressDrawable)
                    .listener(glideRequestListener)
                    .into(binding.editaccountimage)
                    .view.visibility = View.VISIBLE
            }
        }
        return view
    }

    fun setUserFields(user: User) {
        binding.editaccrealname.setText(user.realName)
        binding.editaccusername.setText(user.username)
        binding.editaccemail.setText(user.email)
    }

}