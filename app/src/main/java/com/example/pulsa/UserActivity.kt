package com.example.pulsa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pulsa.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.postsbtn.setOnClickListener {
            replaceFragment(AccountPostsFragment())
        }
        binding.repliesbtn.setOnClickListener {
            replaceFragment(AccountRepliesFragment())
        }
        binding.editaccountbtn.setOnClickListener {
            replaceFragment(EditAccountFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fM = supportFragmentManager
        val fT = fM.beginTransaction()
        fT.replace(R.id.userfragment, fragment)
        fT.commit()
    }
}