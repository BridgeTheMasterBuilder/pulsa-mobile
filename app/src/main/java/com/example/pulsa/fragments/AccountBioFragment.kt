package com.example.pulsa.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pulsa.databinding.FragmentUserBioBinding
import com.example.pulsa.objects.User
import java.time.format.DateTimeFormatter


class AccountBioFragment : Fragment() {
    private lateinit var binding: FragmentUserBioBinding
    private lateinit var user: User
    private var postCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBioBinding.inflate(inflater, container, false)
        val view = binding.root
        val args = this.arguments
        if (args != null) {
            user = args.getParcelable<User>("user")!!

            binding.userRealName.text = user.realName
            binding.userEmail.text = user.email
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            binding.userJoined.text = formatter.format(user.created)
        }

        return view
    }

    fun setFields(user: User) {
        this.user = user

        binding.userRealName.text = user.realName
        binding.userEmail.text = user.email
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        binding.userJoined.text = formatter.format(user.created)
    }
}