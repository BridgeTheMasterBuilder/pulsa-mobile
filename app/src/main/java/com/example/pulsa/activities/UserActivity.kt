package com.example.pulsa.activities

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.pulsa.R
import com.example.pulsa.databinding.ActivityUserBinding
import com.example.pulsa.fragments.AccountPostsFragment
import com.example.pulsa.fragments.AccountRepliesFragment
import com.example.pulsa.fragments.EditAccountFragment
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.Post
import com.example.pulsa.objects.User
import com.google.gson.reflect.TypeToken

class UserActivity : BaseLayoutActivity() {
    private lateinit var binding: ActivityUserBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fragmentManager = supportFragmentManager
        var map: HashMap<String, Any> = HashMap()

        //VANTAR USER I SHAREDPREFERENCES
        map["type"] = object : TypeToken<User>() {}
        map["url"] = "u/test/"

        runOnUiThread { NetworkManager().get(this, map) }
        binding.postsbtn.setOnClickListener {
            /*
            val args = Bundle()
            args.putParcelableArrayList("posts", ArrayList(user.posts))
            AccountPostsFragment().arguments = args*/
            replaceFragment(AccountPostsFragment())
        }
        binding.repliesbtn.setOnClickListener {
            /* val args = Bundle()
             args.putParcelableArrayList("replies", ArrayList(user.replies))
             AccountRepliesFragment().arguments = args*/
            replaceFragment(AccountRepliesFragment())
        }
        binding.editaccountbtn.setOnClickListener {
            val args = Bundle()
            args.putString("username", user.username)
            args.putString("realName", user.realName)
            args.putString("email", user.email)
            args.putString("password", user.password)
            args.putString("avatar", user.avatar)
            AccountRepliesFragment().arguments = args
            replaceFragment(EditAccountFragment())
        }
    }

    override fun resolveGet(content: Any) {
        user = content as User
        val editAccountFragment = getCurrentFragment() as EditAccountFragment
        editAccountFragment.setUserFields(user)
    }

    internal fun startPostActivity(post: Post, pos: Int) {
        val i = Intent(this, PostActivity::class.java)
        startActivity(i)
    }

    private fun replaceFragment(fragment: Fragment) {
        fragmentManager.beginTransaction().replace(R.id.userfragment, fragment).commit()
    }


    private fun getCurrentFragment(): Fragment? {
        return fragmentManager.findFragmentById(R.id.userfragment)
    }
}