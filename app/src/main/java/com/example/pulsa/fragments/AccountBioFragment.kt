package com.example.pulsa.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pulsa.databinding.FragmentAccountBioBinding

class AccountBioFragment : Fragment() {
    private lateinit var binding: FragmentAccountBioBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBioBinding.inflate(inflater, container, false)
        binding.editaccountusername.setText("LOL")
        val view = binding.root
        return view
    }

}