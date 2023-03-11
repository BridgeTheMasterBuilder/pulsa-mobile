package com.example.pulsa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.example.pulsa.databinding.ActivityMainBinding

class MainActivity : BaseLayoutActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: GenericRecyclerAdapter<Post>
    private lateinit var posts: MutableList<Post>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        posts = PostService().posts
        adapter = GenericRecyclerAdapter(
            posts,
            { post -> adapterOnClick(post) },
            R.layout.list_item
        )
        binding.recyclerView.adapter = adapter

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.recyclerView.smoothScrollToPosition(0)
            }
        })

        val resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val post: Post = result.data?.getParcelableExtra("post")!!
                adapter.addItem(post)
            }
        }

        binding.newpostbtn.setOnClickListener {
            val intent = Intent(this, NewPostActivity::class.java)

            resultLauncher.launch(intent)
        }
    }

    private fun adapterOnClick(post: Post) {
        val intent = Intent(this, PostActivity::class.java)
        intent.putExtra("post", post)
        startActivity(intent)
    }

}
