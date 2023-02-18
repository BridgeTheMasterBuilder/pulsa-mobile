package com.example.pulsa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.pulsa.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var postArray : ArrayList<Post>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postArray = ArrayList()

        val post1 = Post("Pólitík",
                            Content("Hægri vinstri upp og niður",
                                    "suð",
                                    "blóm"),
                            "Áttaviti")
        val post2 = Post("Listaverk",
                            Content("Píkatsjú málverk",
                                  "suð",
                                "blóm"),
                        "Lista-Verktakar")
        val post3 = Post("Fyndið",
                            Content("Brandari, haha",
                                    "suð",
                                    "blóm"),
                        "Brandarar")
        postArray.add(post1)
        postArray.add(post2)
        postArray.add(post3)

        binding.listview.isClickable = true
        binding.listview.adapter = FrontPageAdapter(this,postArray)
        binding.listview.setOnItemClickListener { parent, view, position, id ->

            val post = postArray[position]
            val title = post.title
            val text = post.content.text
            val sub = post.sub

            val i = Intent(this,PostActivity::class.java)
            i.putExtra("title", title)
            i.putExtra("text", text)
            i.putExtra("sub", sub)
            startActivity(i)
        }

        binding.newpostbtn.setOnClickListener {
            val i = Intent(this, NewPostActivity::class.java)
            startActivity(i)
        }
    }
}