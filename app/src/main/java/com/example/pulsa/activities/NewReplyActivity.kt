package com.example.pulsa.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.UriUtils
import com.example.pulsa.R
import com.example.pulsa.databinding.ActivityNewReplyBinding
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.*
import com.example.pulsa.utils.MediaUtils
import com.google.android.material.button.MaterialButton
import com.google.gson.reflect.TypeToken


private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
private const val SELECT_PICTURE = 200
private const val SELECT_AUDIO = 2
private const val MEDIA_STOP = R.drawable.icons8_stop_96

class NewReplyActivity : BaseLayoutActivity() {
    private lateinit var binding: ActivityNewReplyBinding
    private lateinit var rPlayToggle: MaterialButton
    private lateinit var aPlayToggle: MaterialButton
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var reply: Reply
    private lateinit var audioUri: Uri
    private lateinit var imageUri: Uri

    private var recordingPath = ""
    private var audioPlayer: MediaPlayer? = null
    private var mediaUtils: MediaUtils = MediaUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewReplyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reply = intent.getParcelableExtra<Reply>("reply")!!

        mediaUtils.verifyStoragePermissions(this);

        recordingPath = "${externalCacheDir?.absolutePath}/tempRecording.3gp"
        rPlayToggle = (binding.playrecordingbutton as MaterialButton)
        aPlayToggle = (binding.playaudiobutton as MaterialButton)


        binding.imagebutton.setOnClickListener {
            val i = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(Intent.createChooser(i, "Select picture"), SELECT_PICTURE)
        }

        binding.audiobutton.setOnClickListener {
            val i = Intent().apply {
                type = "audio/*"
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(Intent.createChooser(i, "Select audio file"), SELECT_AUDIO)
        }

        binding.recordbutton.setOnClickListener {
            mediaUtils.record(
                binding.recordbutton,
                binding.loadingrecording,
                this,
                recordingPath
            )
        }

        binding.playrecordingbutton.setOnClickListener {
            mediaUtils.playRecording(
                this,
                rPlayToggle,
                binding.recordbutton,
                recordingPath
            )
        }

        binding.playaudiobutton.setOnClickListener {
            mediaUtils.playAudio(
                aPlayToggle,
                audioUri,
                this)
        }

        binding.submitbutton.setOnClickListener {
            submitButtonOnClick()
        }
    }

    private fun submitButtonOnClick() {
        binding.submitbutton.visibility = View.INVISIBLE
        binding.submitting.visibility = View.VISIBLE

        val map: HashMap<String, Any> = HashMap()

        val post = intent.getParcelableExtra<Post>("post")!!

        val reply = if (intent.hasExtra("reply")) {
            intent.getParcelableExtra<Reply>("reply")
        } else {
            null
        }

        if (reply != null) {
            map["url"] = "p/${post.sub.slug}/${post.postId}/${reply.replyId}"
            map["nestedReply"] = true
        } else {
            map["url"] = "p/${post.sub.slug}/${post.postId}"
        }

        map["type"] = object : TypeToken<Reply>() {}
        map["text"] = binding.newreplytext.text.toString()

        if (this::imageUri.isInitialized) {
            map["image"] = UriUtils.uri2File(imageUri)
            map["imageType"] = this.contentResolver.getType(imageUri).toString()
        }
        if (this::audioUri.isInitialized) {
            map["audio"] = UriUtils.uri2File(audioUri)
            map["audioType"] = this.contentResolver.getType(audioUri).toString()
        }
        if (mediaUtils.recordingUri !== null) {
            map["recording"] = UriUtils.uri2File(mediaUtils.recordingUri)
            map["recordingType"] = "video/3gpp"
        }
        map["token"] = ""

        runOnUiThread { NetworkManager().post(this, map) }
    }

    override fun resolvePost(content: Any) {
        val intent = intent
        if (content is Map<*, *>) {
            val nestedReply = content["reply"] as Reply
            intent.putExtra("nestedReply", nestedReply)
        } else {
            reply = content as Reply
        }
        intent.putExtra("reply", reply)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            onPermissionsGranted()
    }

    private fun onPermissionsGranted() {
        mediaRecorder = mediaUtils.initMediaRecorder(recordingPath)
        mediaUtils.mediaRecorder = mediaRecorder
        rPlayToggle.setIconResource(MEDIA_STOP)
        binding.playrecordingbutton.visibility = View.VISIBLE
        binding.loadingrecording.visibility = View.INVISIBLE
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return

        when (requestCode) {
            SELECT_PICTURE -> {
                data?.data?.let { image ->
                    imageUri = image
                    binding.imageView.visibility = View.VISIBLE
                    binding.imageView.apply {
                        visibility = View.VISIBLE
                        setImageURI(image)
                    }
                }
            }
            SELECT_AUDIO -> {
                data?.data?.let { audio ->
                    audioUri = audio
                    binding.playaudiobutton.visibility = View.VISIBLE
                    audioPlayer = mediaUtils.initMediaPlayer(audioUri, aPlayToggle, this)
                    mediaUtils.audioPlayer = audioPlayer as MediaPlayer
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()

        mediaUtils.audioPlayer?.stop()
        mediaUtils.recordingPlayer?.stop()
    }
}