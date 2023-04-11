package com.example.pulsa.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GestureDetectorCompat
import com.example.pulsa.R
import com.example.pulsa.adapters.GenericRecyclerAdapter
import com.example.pulsa.databinding.ActivitySubBinding
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.Post
import com.example.pulsa.objects.Sub
import com.example.pulsa.utils.MediaUtils
import com.google.android.material.button.MaterialButton
import com.google.gson.reflect.TypeToken

private const val MEDIA_PLAY = R.drawable.icons8_play_96
private const val MEDIA_STOPPED = "stopped"

class SubActivity : BaseLayoutActivity(), GestureDetector.OnGestureListener, ActivityRing<Post> {
    private lateinit var binding: ActivitySubBinding
    private lateinit var adapter: GenericRecyclerAdapter<Post>
    private lateinit var posts: MutableList<Post>
    private lateinit var mDetector: GestureDetectorCompat
    private var mediaUtilsArray = arrayOf<Pair<MediaUtils, MaterialButton?>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sub: Sub = intent.getParcelableExtra("sub")!!
        val slug = sub.slug
        var map: HashMap<String, Any> = HashMap()
        map["type"] = object : TypeToken<List<Post>>() {}
        map["url"] = "p/${slug}/"
        runOnUiThread { NetworkManager().get(this, map) }

        binding = ActivitySubBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.setOnTouchListener(View.OnTouchListener { v, event ->
            v.performClick()
            mDetector.onTouchEvent(event)
        })


        binding.newpostbtn.setOnClickListener {
            val intent = Intent(this, NewPostActivity::class.java)
            intent.putExtra("sub", sub)
            resultLauncher.launch(intent)
        }

        mDetector = GestureDetectorCompat(this, this)
    }

    val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                println("IN MAIN ACTIVITY AGAIN")
                val data = result.data?.extras
                val pos = data?.getInt("pos")!!

                if (data.getBoolean("nextPost", false)) {
                    val (post, position) = next(posts, pos)

                    dispatch(post, position, ::adapterOnClick)
                } else if (data.getBoolean("prevPost", false)) {
                    val (post, position) = prev(posts, pos)

                    dispatch(post, position, ::adapterOnClick)
                } else {
                    val post: Post = result.data?.getParcelableExtra("post")!!
                    adapter.addItem(post)
                }
            }
        }

    override fun resolveGet(content: Any) {
        posts = content as MutableList<Post>

        adapter = GenericRecyclerAdapter(
            posts,
            { post, position -> adapterOnClick(post, position) },
            R.layout.post_item
        )
        audioOnClickSetup()
        binding.recyclerView.adapter = adapter
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

    private fun adapterOnClick(post: Post, position: Int) {
        val intent = Intent(this, PostActivity::class.java)
        intent.putExtra("post", post)
        intent.putExtra("pos", position)
        resultLauncher.launch(intent)
    }

    public fun failure() {
        // TODO: Display failed to load posts xml
        // TODO: Rename function
        println("Failed to load posts")
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun onFling(
        event1: MotionEvent,
        event2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }

    override fun onDown(p0: MotionEvent): Boolean {
        return true
    }

    override fun onShowPress(p0: MotionEvent) {
    }

    override fun onLongPress(p0: MotionEvent) {
    }

    override fun onScroll(
        event1: MotionEvent,
        event2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (velocityX > 0.0) {
            intent.putExtra("nextSub", true)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else if (velocityX < 0.0) {
            intent.putExtra("prevSub", true)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        return true
    }

    override fun onSingleTapUp(p0: MotionEvent): Boolean {
        return false
    }

    override fun dispatch(content: Post, position: Int, launcher: (Post, Int) -> Unit) {
        adapterOnClick(content, position)
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
