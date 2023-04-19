/*
package com.example.pulsa.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.example.pulsa.adapters.GenericRecyclerAdapter
import com.example.pulsa.R
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.Sub
import com.google.gson.reflect.TypeToken

class SubIndexActivitytest : BaseLayoutActivity() {
    private lateinit var binding: ActivitySubIndexBinding
    private lateinit var subs: MutableList<Sub>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var map: HashMap<String, Any> = HashMap()
        map["type"] = object: TypeToken<List<Sub>>(){}
        map["url"] = "/p/"
        runOnUiThread{ NetworkManager().get(this, map) }

        val resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val sub: Sub = result.data?.getParcelableExtra("post")!!
                adapter.addItem(sub)
            }
        }

        binding = ActivitySubIndexBinding.inflate(layoutInflater)
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
        subs = content as MutableList<Sub>

        adapter = GenericRecyclerAdapter(
            subs,
            { sub -> adapterOnClick(sub) },
            R.layout.list_item
        )
        binding.recyclerView.adapter = adapter
    }

    private fun adapterOnClick(sub: Sub) {
        val intent = Intent(this, SubActivity::class.java)
        intent.putExtra("sub", sub)
        startActivity(intent)
    }

    public fun failure() {
        // TODO: Display failed to load posts xml
        // TODO: Rename function
        println("Failed to load posts")
    }
}
*/
