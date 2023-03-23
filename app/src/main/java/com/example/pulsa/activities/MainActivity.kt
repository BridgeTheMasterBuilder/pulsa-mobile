package com.example.pulsa.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.example.pulsa.adapters.GenericRecyclerAdapter
import com.example.pulsa.R
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
                        "url" to "/"
                )
            )
        }

        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            allsubsbtn.setOnClickListener {
                startActivity(Intent(this@MainActivity, SubIndexActivity::class.java))
            }
            onBackPressedDispatcher.addCallback(this@MainActivity, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    recyclerView.smoothScrollToPosition(0)
                }
            })
        }

        /*val resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val post: Post = result.data?.getParcelableExtra("post")!!
                adapter.addItem(post)
            }
        }*/

        /*binding.newpostbtn.setOnClickListener {
            val intent = Intent(this, NewPostActivity::class.java)
            resultLauncher.launch(intent)
        }*/
    }

    override fun resolveGet(content: Any) {
        posts = content as MutableList<Post>
        adapter = GenericRecyclerAdapter(posts, ::adapterOnClick, R.layout.post_item)
        binding.recyclerView.adapter = adapter
    }

    private fun adapterOnClick(post: Post) {
        val intent = Intent(this, PostActivity::class.java)
        intent.putExtra("post", post)
        startActivity(intent)
    }

    public fun failure() {
        // TODO: Display failed to load posts xml
        // TODO: Rename function
        println("Failed to load posts")
    }
}
