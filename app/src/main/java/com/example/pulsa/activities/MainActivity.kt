package com.example.pulsa.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.pulsa.R
import com.example.pulsa.adapters.GenericRecyclerAdapter
import com.example.pulsa.databinding.ActivityMainBinding
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.Post
import com.google.gson.reflect.TypeToken

class MainActivity : BaseLayoutActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: GenericRecyclerAdapter<Post>
    private lateinit var posts: MutableList<Post>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runOnUiThread {
            NetworkManager().get(
                this,
                hashMapOf(
                    "type" to object : TypeToken<List<Post>>() {},
                    "url" to ""
                )
            )
        }

        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            allsubsbtn.setOnClickListener {
                startActivity(Intent(this@MainActivity, SubIndexActivity::class.java))
            }
            onBackPressedDispatcher.addCallback(
                this@MainActivity,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        recyclerView.smoothScrollToPosition(0)
                    }
                }
            )
        }


        /*binding.newpostbtn.setOnClickListener {
            val intent = Intent(this, NewPostActivity::class.java)
            resultLauncher.launch(intent)
        }*/
    }

    val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                println("IN MAIN ACTIVITY AGAIN")
                val post: Post = result.data?.extras?.getParcelable("postWithReply")!!
                var pos = result.data?.extras?.getInt("pos")!!
                println("size of replies after adding reply to post:${post.replies.size}")
                println("Position in array${pos}")
                println("DID I RECIEVE POST?: ${post?.title}")
                println("post size before added to adapter:${posts.size}")
                post?.let { it ->
                    adapter.updateItem(post, pos)
                    posts[pos] = post
                }
                println("post size after added to adapter:${posts.size}")

            }
        }

    override fun resolveGet(content: Any) {
        posts = content as MutableList<Post>
        adapter = GenericRecyclerAdapter(posts, ::adapterOnClick, R.layout.post_item)

        binding.recyclerView.adapter = adapter
    }

    private fun adapterOnClick(post: Post, position: Int) {
        val intent = Intent(this, PostActivity::class.java)
        intent.putExtra("post", post)
        intent.putExtra("pos", position)
        println("BEFORE LAuNCHING intent with post: Size of replies:${post.replies.size}")
        resultLauncher.launch(intent)
    }

    public fun failure() {
        // TODO: Display failed to load posts xml
        // TODO: Rename function
        println("Failed to load posts")
    }
}
