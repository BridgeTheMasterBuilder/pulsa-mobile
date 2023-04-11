package com.example.pulsa.fragments

import android.os.Bundle
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.example.pulsa.R
import com.example.pulsa.activities.UserActivity
import com.example.pulsa.activities.UserPageActivity
import com.example.pulsa.adapters.GenericRecyclerAdapter
import com.example.pulsa.databinding.FragmentAccountRepliesBinding
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.Post
import com.example.pulsa.objects.Reply
import com.example.pulsa.utils.MediaUtils
import com.google.android.material.button.MaterialButton
import com.google.gson.reflect.TypeToken

private const val MEDIA_PLAY = R.drawable.icons8_play_96
private const val MEDIA_STOPPED = "stopped"

class AccountRepliesFragment : Fragment() {

    private lateinit var binding: FragmentAccountRepliesBinding
    private lateinit var adapter: GenericRecyclerAdapter<Reply>
    private lateinit var replies: MutableList<Reply>
    private var mediaUtilsArray = arrayOf<Pair<MediaUtils, MaterialButton?>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountRepliesBinding.inflate(inflater, container, false)
        val view = binding.root
        replies = (this.arguments?.getParcelableArrayList<Reply>("replies") as MutableList<Reply>)
        adapter = GenericRecyclerAdapter(replies, ::adapterOnClick, R.layout.reply)
        binding.recyclerView.adapter = adapter
        audioOnClickSetup()
        voteOnClickSetup()
        return view
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
            runOnUiThread {
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
            runOnUiThread {
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

    private fun adapterOnClick(reply: Reply, position: Int) {
        //fuck
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