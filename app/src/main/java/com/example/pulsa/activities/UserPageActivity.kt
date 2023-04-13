package com.example.pulsa.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.webkit.URLUtil
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.example.pulsa.R
import com.example.pulsa.databinding.ActivityUserPageBinding
import com.example.pulsa.fragments.AccountBioFragment
import com.example.pulsa.fragments.AccountPostsFragment
import com.example.pulsa.fragments.AccountRepliesFragment
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.Post
import com.example.pulsa.objects.Reply
import com.example.pulsa.objects.User
import com.example.pulsa.utils.glideRequestListener
import com.google.gson.reflect.TypeToken

class UserPageActivity : BaseLayoutActivity(), ActivityRing<Post> {
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

        user = intent.getParcelableExtra<User>("user")!!

        var map: HashMap<String, Any> = HashMap()

        map["type"] = object : TypeToken<User>() {}
        map["url"] = "u/${user.username}/"

        runOnUiThread { NetworkManager().get(this, map) }

        binding.biobtn.setOnClickListener {
            replaceFragment(accountBioFragment)
        }

        binding.postsbtn.setOnClickListener {
            replaceFragment(accountPostsFragment)
        }

        binding.repliesbtn.setOnClickListener {
            replaceFragment(accountRepliesFragment)
        }
    }

    override fun resolveGet(content: Any) {
        user = content as User

        if (URLUtil.isValidUrl(user.avatar)) {
            binding.userAvatar.let {
                Glide.with(this)
                    .load(user.avatar)
                    .listener(glideRequestListener)
                    .into(it)
            }
        }

        val args = Bundle()
        args.putParcelable("user", user)
        args.putParcelableArrayList("posts", (user.posts as ArrayList<Post>))
        args.putParcelableArrayList("replies", (user.replies as ArrayList<Reply>))

        accountBioFragment = getCurrentFragment() as AccountBioFragment
        accountBioFragment.arguments = args
        accountBioFragment.setFields(user)

        accountPostsFragment = AccountPostsFragment()
        accountPostsFragment.arguments = args

        accountRepliesFragment = AccountRepliesFragment()
        accountRepliesFragment.arguments = args
    }

    private fun adapterOnClick(post: Post, position: Int) {
        val intent = Intent(this, PostActivity::class.java)

        intent.putExtra("post", post)
        intent.putExtra("pos", position)
        resultLauncher.launch(intent)
    }

    private fun adapterOnClickReply(reply: Reply, position: Int) {
        //fuck
    }

    val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.extras
                val pos = data?.getInt("pos")!!

                if (data.getBoolean("nextPost", false)) {
                    val (post, position) = next(user.posts, pos)

                    dispatch(post, position)
                } else if (data.getBoolean("prevPost", false)) {
                    val (post, position) = prev(user.posts, pos)

                    dispatch(post, position)
                } else {
                    val post: Post = result.data?.extras?.getParcelable("postWithReply")!!

                    post.let { it ->
                        accountPostsFragment.adapter.updateItem(post, pos)
                        user.posts[pos] = post
                    }
                }
            }
        }

    override fun dispatch(content: Post, position: Int) {
        adapterOnClick(content, position)
    }

    private fun replaceFragment(fragment: Fragment) {
        fragmentManager.beginTransaction().replace(R.id.userfragment, fragment).commit()
    }


    private fun getCurrentFragment(): Fragment? {
        return fragmentManager.findFragmentById(R.id.userfragment)
    }
}