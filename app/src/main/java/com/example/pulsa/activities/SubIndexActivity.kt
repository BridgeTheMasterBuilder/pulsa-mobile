package com.example.pulsa.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.example.pulsa.R
import com.example.pulsa.adapters.GenericRecyclerAdapter
import com.example.pulsa.databinding.ActivitySubIndexBinding
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.Sub
import com.google.gson.reflect.TypeToken

class SubIndexActivity : BaseLayoutActivity() {

    private lateinit var binding: ActivitySubIndexBinding
    private lateinit var adapter: GenericRecyclerAdapter<Sub>
    private lateinit var subs: MutableList<Sub>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var map: HashMap<String, Any> = HashMap()
        map["type"] = object : TypeToken<List<Sub>>() {}
        map["url"] = "p/"
        runOnUiThread { NetworkManager().get(this, map) }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.recyclerView.smoothScrollToPosition(0)
            }
        })

        binding = ActivitySubIndexBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun resolveGet(content: Any) {
        subs = content as MutableList<Sub>

        adapter = GenericRecyclerAdapter(
            subs,
            { sub, position -> adapterOnClick(sub, position) },
            R.layout.sub_item
        )
        binding.recyclerView.adapter = adapter
    }

    private fun adapterOnClick(sub: Sub, position: Int) {
        val intent = Intent(this, SubActivity::class.java)
        intent.putExtra("sub", sub)
        intent.putExtra("pos", position)
        startActivity(intent)
    }

    public fun failure() {
        // TODO: Display failed to load posts xml
        // TODO: Rename function
        println("Failed to load subs")
    }
}