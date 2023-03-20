package com.example.pulsa.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.example.pulsa.adapters.GenericRecyclerAdapter
import com.example.pulsa.services.PostService
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
        var map: HashMap<String, Any> = HashMap()
        map["type"] = object: TypeToken<List<Post>>(){}
        map["url"] = "/"
        runOnUiThread{ NetworkManager().get(this, map) }

        val resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val post: Post = result.data?.getParcelableExtra("post")!!
                adapter.addItem(post)
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.recyclerView.smoothScrollToPosition(0)
            }
        })

        binding.newpostbtn.setOnClickListener {
            val intent = Intent(this, NewPostActivity::class.java)

            resultLauncher.launch(intent)
        }
    }

    override fun resolveGet(content: Any) {
        posts = content as MutableList<Post>

        adapter = GenericRecyclerAdapter(
            posts,
            { post -> adapterOnClick(post) },
            R.layout.list_item
        )
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
