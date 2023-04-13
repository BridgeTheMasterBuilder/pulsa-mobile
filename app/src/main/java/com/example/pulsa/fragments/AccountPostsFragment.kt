package com.example.pulsa.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ThreadUtils
import com.example.pulsa.R
import com.example.pulsa.activities.ActivityRing
import com.example.pulsa.activities.PostActivity
import com.example.pulsa.activities.SubActivity
import com.example.pulsa.activities.UserPageActivity
import com.example.pulsa.adapters.GenericRecyclerAdapter
import com.example.pulsa.databinding.FragmentAccountPostsBinding
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.Post
import com.example.pulsa.utils.MediaUtils
import com.google.android.material.button.MaterialButton
import com.google.gson.reflect.TypeToken

private const val MEDIA_PLAY = R.drawable.icons8_play_96
private const val MEDIA_STOPPED = "stopped"

class AccountPostsFragment : Fragment(), ActivityRing<Post> {
    private lateinit var binding: FragmentAccountPostsBinding
    lateinit var adapter: GenericRecyclerAdapter<Post>
    private lateinit var posts: MutableList<Post>
    private var mediaUtilsArray = arrayOf<Pair<MediaUtils, MaterialButton?>>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountPostsBinding.inflate(inflater, container, false)
        val view = binding.root
        posts = (this.arguments?.getParcelableArrayList<Post>("posts") as MutableList<Post>)
        adapter = GenericRecyclerAdapter(posts, ::adapterOnClick, R.layout.post_item)
        binding.recyclerView.adapter = adapter
        audioOnClickSetup()
        voteOnClickSetup()
        userOnClickSetup()
        subOnClickSetup()
        return view
    }

    private fun userOnClickSetup() {
        adapter.userOnClick { user, position ->
            val intent = Intent(this.context, UserPageActivity::class.java)
            intent.putExtra("user", user)
            intent.putExtra("pos", position)
            startActivity(intent)
        }
    }

    private fun subOnClickSetup() {
        adapter.subOnClick { sub, position ->
            val intent = Intent(this.context, SubActivity::class.java)
            intent.putExtra("sub", sub)
            intent.putExtra("pos", position)
            startActivity(intent)
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

    private fun voteOnClickSetup() {
        adapter.upvoteOnClick { id, pos ->
            ThreadUtils.runOnUiThread {
                NetworkManager().post(
                    (this.context as UserPageActivity), hashMapOf(
                        "type" to object : TypeToken<Post>() {},
                        "url" to "p/${id}/upvote",
                        "vote" to ""
                    )
                )
            }
            Intent().putExtra("pos", pos)
        }

        adapter.downvoteOnClick { id, pos ->
            ThreadUtils.runOnUiThread {
                NetworkManager().post(
                    (this.context as UserPageActivity), hashMapOf(
                        "type" to object : TypeToken<Post>() {},
                        "url" to "p/${id}/downvote",
                        "vote" to ""
                    )
                )
            }
            Intent().putExtra("pos", pos)
        }
    }

    private fun adapterOnClick(post: Post, position: Int) {
        val intent = Intent(this.context, PostActivity::class.java)

        intent.putExtra("post", post)
        intent.putExtra("pos", position)
        resultLauncher.launch(intent)
    }

    val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.extras
                val pos = data?.getInt("pos")!!

                if (data.getBoolean("nextPost", false)) {
                    val (post, position) = next(posts, pos)

                    dispatch(post, position)
                } else if (data.getBoolean("prevPost", false)) {
                    val (post, position) = prev(posts, pos)

                    dispatch(post, position)
                } else {
                    val post: Post = result.data?.extras?.getParcelable("postWithReply")!!

                    post.let { it ->
                        adapter.updateItem(post, pos)
                        posts[pos] = post
                    }

                }
            }
        }

    override fun onStop() {
        super.onStop()

        mediaUtilsArray.forEach { pair ->
            pair.first.medPlayer?.stop()
            pair.second?.tag = MEDIA_STOPPED
            pair.second?.setIconResource(MEDIA_PLAY)
        }
    }

    override fun dispatch(content: Post, pos: Int) {
        adapterOnClick(content, pos)
    }

}
