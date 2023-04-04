package com.example.pulsa.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pulsa.databinding.FragmentEditAccountBinding
import com.example.pulsa.objects.User

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
        return binding.root
    }

    fun setUserFields(user: User) {
        binding.editaccrealname.setText(user.realName)
        binding.editaccusername.setText(user.username)
        binding.editaccemail.setText(user.email)
    }

}