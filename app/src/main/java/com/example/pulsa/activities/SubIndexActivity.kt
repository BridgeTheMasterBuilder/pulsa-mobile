package com.example.pulsa.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.pulsa.R
import com.example.pulsa.adapters.GenericRecyclerAdapter
import com.example.pulsa.databinding.ActivitySubIndexBinding
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.Sub
import com.google.gson.reflect.TypeToken

class SubIndexActivity : BaseLayoutActivity(), ActivityRing<Sub> {

    private lateinit var binding: ActivitySubIndexBinding
    private lateinit var adapter: GenericRecyclerAdapter<Sub>
    private lateinit var subs: MutableList<Sub>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var map: HashMap<String, Any> = HashMap()
        map["type"] = object : TypeToken<List<Sub>>() {}
        map["url"] = "p/"
        runOnUiThread { NetworkManager().get(this, map) }

        binding = ActivitySubIndexBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.subIndexNewSubButton.setOnClickListener {
            val i = Intent(this, NewSubActivity::class.java)
            startActivity(i)
        }
    }

    val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.extras
                val pos = data?.getInt("pos")!!

                handle(data, subs, pos)
            }
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
        resultLauncher.launch(intent)
    }

    public fun failure() {
        // TODO: Display failed to load posts xml
        // TODO: Rename function
        println("Failed to load subs")
    }

    override fun dispatch(content: Sub, position: Int) {
        adapterOnClick(content, position)
    }
}