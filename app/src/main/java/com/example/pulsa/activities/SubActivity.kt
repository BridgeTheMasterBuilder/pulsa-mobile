package com.example.pulsa.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.pulsa.R
import com.example.pulsa.adapters.GenericRecyclerAdapter
import com.example.pulsa.databinding.ActivitySubBinding
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.Post
import com.example.pulsa.objects.Sub
import com.google.gson.reflect.TypeToken

class SubActivity : BaseLayoutActivity() {
    private lateinit var binding: ActivitySubBinding
    private lateinit var adapter: GenericRecyclerAdapter<Post>
    private lateinit var posts: MutableList<Post>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sub: Sub = intent.getParcelableExtra("sub")!!
        val slug = sub.slug
        var map: HashMap<String, Any> = HashMap()
        map["type"] = object : TypeToken<List<Post>>() {}
        map["url"] = "/p/${slug}/"
        runOnUiThread { NetworkManager().get(this, map) }

        binding = ActivitySubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.recyclerView.smoothScrollToPosition(0)
            }
        })

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val post: Post = result.data?.getParcelableExtra("post")!!
                    adapter.addItem(post)
                }
            }

        binding.newpostbtn.setOnClickListener {
            val intent = Intent(this, NewPostActivity::class.java)
            intent.putExtra("sub", sub)
            resultLauncher.launch(intent)
        }
    }

    override fun resolveGet(content: Any) {
        posts = content as MutableList<Post>

        adapter = GenericRecyclerAdapter(
            posts,
            { post, position -> adapterOnClick(post, position) },
            R.layout.post_item
        )
        binding.recyclerView.adapter = adapter
    }

    private fun adapterOnClick(post: Post, position: Int) {
        val intent = Intent(this, PostActivity::class.java)
        intent.putExtra("post", post)
        intent.putExtra("pos", position)
        startActivity(intent)
    }

    public fun failure() {
        // TODO: Display failed to load posts xml
        // TODO: Rename function
        println("Failed to load posts")
    }
}
