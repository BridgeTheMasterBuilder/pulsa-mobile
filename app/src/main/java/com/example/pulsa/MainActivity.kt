package com.example.pulsa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.example.pulsa.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: FrontPageAdapter
    private lateinit var posts: MutableList<Post>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        posts = PostService().posts
        adapter = FrontPageAdapter(posts) { post -> adapterOnClick(post) }
        binding.recyclerView.adapter = adapter

        val resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val post: Post? = result.data?.getParcelableExtra("post")
                if (post != null) posts.add(post)
                adapter.notifyDataSetChanged()
            }
        }

        binding.newpostbtn.setOnClickListener {
            val intent = Intent(this, NewPostActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    fun adapterOnClick(post: Post) {
        val intent = Intent(this, PostActivity::class.java)
        intent.putExtra("post", post)
        startActivity(intent)
    }

}
