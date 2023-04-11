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
import com.example.pulsa.services.UserService
import com.example.pulsa.utils.MediaUtils
import com.google.android.material.button.MaterialButton
import com.google.gson.reflect.TypeToken

private const val MEDIA_STOP = R.drawable.icons8_stop_96
private const val MEDIA_PLAY = R.drawable.icons8_play_96
private const val MEDIA_STOPPED = "stopped"


class MainActivity : BaseLayoutActivity(), ActivityRing<Post> {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: GenericRecyclerAdapter<Post>
    private lateinit var posts: MutableList<Post>
    private var mediaUtilsArray = arrayOf<Pair<MediaUtils, MaterialButton?>>()


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
    }

    val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.extras
                val pos = data?.getInt("pos")!!

                if (data.getBoolean("nextPost", false)) {
                    val (post, position) = next(posts, pos)

                    dispatch(post, position, ::adapterOnClick)
                } else if (data.getBoolean("prevPost", false)) {
                    val (post, position) = prev(posts, pos)

                    dispatch(post, position, ::adapterOnClick)
                } else {
                    val post: Post = result.data?.extras?.getParcelable("postWithReply")!!

                    post?.let { it ->
                        adapter.updateItem(post, pos)
                        posts[pos] = post
                    }

                }
            }
        }

    override fun resolveGet(content: Any) {
        posts = content as MutableList<Post>
        adapter = GenericRecyclerAdapter(posts, ::adapterOnClick, R.layout.post_item)
        voteOnClickSetup()
        audioOnClickSetup()
        subOnClickSetup()
        binding.recyclerView.adapter = adapter
    }

    private fun adapterOnClick(post: Post, position: Int) {
        val intent = Intent(this, PostActivity::class.java)
        // TODO: Fjarlægja eftir að user dótið er lagað
        post.creator = UserService.user
        for (reply in post.replies) {
            reply.creator = UserService.user
        }
        intent.putExtra("post", post)
        intent.putExtra("pos", position)
        resultLauncher.launch(intent)
    }

    public fun failure() {
        // TODO: Display failed to load posts xml
        // TODO: Rename function
        println("Failed to load posts")
    }

    override fun onResume() {
        super.onResume()

        if (this::adapter.isInitialized) adapter.notifyDataSetChanged()
        super.setupUserMenu()
    }

    override fun dispatch(content: Post, position: Int, launcher: (Post, Int) -> Unit) {
        adapterOnClick(content, position)
    }

    private fun voteOnClickSetup() {
        adapter.upvoteOnClick { id, pos ->
            runOnUiThread {
                NetworkManager().post(
                    this, hashMapOf(
                        "type" to object : TypeToken<Post>() {},
                        "url" to "p/${id}/upvote",
                        "vote" to ""
                    )
                )
            }
            intent.putExtra("pos", pos)
        }

        adapter.downvoteOnClick { id, pos ->
            runOnUiThread {
                NetworkManager().post(
                    this, hashMapOf(
                        "type" to object : TypeToken<Post>() {},
                        "url" to "p/${id}/downvote",
                        "vote" to ""
                    )
                )
            }
            intent.putExtra("pos", pos)
        }
    }

    private fun audioOnClickSetup() {
        adapter.playAudioOnClick { button, mediaUtils ->
            val containsTuple = mediaUtilsArray.any { (util, _) -> util == mediaUtils }
            if (!containsTuple) mediaUtilsArray = mediaUtilsArray.plusElement(mediaUtils to button)
            mediaUtils.playMedia(button)
        }

        adapter.playRecordingOnClick { button, mediaUtils ->
            val containsTuple = mediaUtilsArray.any { (util, _) -> util == mediaUtils }
            if (!containsTuple) mediaUtilsArray = mediaUtilsArray.plusElement(mediaUtils to button)
            mediaUtils.playMedia(button)
        }
    }

    private fun subOnClickSetup() {
        adapter.subOnClick { sub, position ->
            val intent = Intent(this, SubActivity::class.java)
            intent.putExtra("sub", sub)
            intent.putExtra("pos", position)
            resultLauncher.launch(intent)
        }
    }

    override fun resolvePost(content: Any) {
        val votedPost = content as Post
        val position = intent.getIntExtra("pos", -1)!!
        adapter.updateItem(votedPost, position)
    }

    override fun onStop() {
        super.onStop()

        mediaUtilsArray.forEach { pair ->
            pair.first.medPlayer?.stop()
            pair.second?.tag = MEDIA_STOPPED
            pair.second?.setIconResource(MEDIA_PLAY)
        }
    }
}
