package com.example.pulsa.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.pulsa.R
import com.example.pulsa.databinding.ActivityUserPageBinding
import com.example.pulsa.fragments.AccountBioFragment
import com.example.pulsa.fragments.AccountPostsFragment
import com.example.pulsa.fragments.AccountRepliesFragment
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.Post
import com.example.pulsa.objects.Reply
import com.example.pulsa.objects.User
import com.example.pulsa.utils.UserUtils
import com.google.gson.reflect.TypeToken

class UserPageActivity : BaseLayoutActivity() {
    private lateinit var binding: ActivityUserPageBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var user: User
    private lateinit var accountPostsFragment: AccountPostsFragment
    private lateinit var accountRepliesFragment: AccountRepliesFragment
    private lateinit var accountBioFragment: AccountBioFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fragmentManager = supportFragmentManager
        var map: HashMap<String, Any> = HashMap()

        val userName = UserUtils.getUserName(this)
        map["type"] = object : TypeToken<User>() {}
        map["url"] = "u/${userName}/"

        runOnUiThread { NetworkManager().get(this, map) }

        binding.biobtn.setOnClickListener {
            val args = Bundle()
            args.putParcelable("user", user)
            accountBioFragment = AccountBioFragment()
            accountBioFragment.arguments = args
            replaceFragment(accountBioFragment)
        }

        binding.postsbtn.setOnClickListener {
            val args = Bundle()
            args.putParcelableArrayList("posts", (user.posts as ArrayList<Post>))
            accountPostsFragment = AccountPostsFragment()
            accountPostsFragment.arguments = args
            replaceFragment(accountPostsFragment)
        }

        binding.repliesbtn.setOnClickListener {
            val args = Bundle()
            args.putParcelableArrayList("replies", (user.replies as ArrayList<Reply>))
            accountRepliesFragment = AccountRepliesFragment()
            accountRepliesFragment.arguments = args
            replaceFragment(accountRepliesFragment)
        }
    }

    override fun resolveGet(content: Any) {
        user = content as User
        val args = Bundle()
        args.putParcelable("user", user)
        accountBioFragment = getCurrentFragment() as AccountBioFragment
        accountBioFragment.arguments = args
        accountBioFragment.setFields(user)
    }

    private fun replaceFragment(fragment: Fragment) {
        fragmentManager.beginTransaction().replace(R.id.userfragment, fragment).commit()
    }


    private fun getCurrentFragment(): Fragment? {
        return fragmentManager.findFragmentById(R.id.userfragment)
    }
}