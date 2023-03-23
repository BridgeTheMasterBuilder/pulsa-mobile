package com.example.pulsa.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.blankj.utilcode.util.UriUtils
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG
import com.example.pulsa.R
import com.example.pulsa.databinding.ActivityNewPostBinding
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.*
import com.google.android.material.button.MaterialButton
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException
import java.sql.Blob

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
private const val SELECT_PICTURE = 200
private const val SELECT_AUDIO = 2
private const val MEDIA_PLAYING = "playing"
private const val MEDIA_STOPPED = "stopped"
private const val MEDIA_PLAY = R.drawable.icons8_play_96
private const val MEDIA_STOP = R.drawable.icons8_stop_96

class NewPostActivity : BaseLayoutActivity() {
    private lateinit var binding: ActivityNewPostBinding
    private lateinit var rPlayToggle: MaterialButton
    private lateinit var aPlayToggle: MaterialButton
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var post: Post
    private lateinit var audioUri: Uri
    private lateinit var imageUri: Uri
    private lateinit var recordingUri: Uri

    private var recordingPermissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var filePermissions: Array<String> = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private var permissionGranted = false
    private var recordingPath = ""
    private var audioPlayer: MediaPlayer? = null
    private var recordingPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        verifyStoragePermissions(this);

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
            recordButtonOnClick()
        }
        binding.playrecordingbutton.setOnClickListener {
            playRecordingButtonOnClick()
        }
        binding.playaudiobutton.setOnClickListener {
            playAudioButtonOnClick()
        }
        binding.submitbutton.setOnClickListener {
            submitButtonOnClick()
        }
    }

    private fun verifyStoragePermissions(activity: NewPostActivity) {
        val permission =
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            );
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                filePermissions,
                1
            );
        }
    }

    private fun playAudioButtonOnClick() {
        if (aPlayToggle.tag == MEDIA_PLAYING) {
            audioPlayer?.stop()
            audioPlayer?.release()
            audioPlayer = null
            aPlayToggle.tag = MEDIA_STOPPED
            aPlayToggle.setIconResource(MEDIA_PLAY)
            audioPlayer = initMediaPlayer(audioUri, aPlayToggle)
        } else {
            aPlayToggle.tag = MEDIA_PLAYING
            aPlayToggle.setIconResource(MEDIA_STOP)
            audioPlayer?.start()
        }
    }

    private fun recordButtonOnClick() {
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(
                this,
                recordingPermissions,
                REQUEST_RECORD_AUDIO_PERMISSION
            )
            binding.recordbutton.visibility = View.INVISIBLE
            binding.loadingrecording.visibility = View.VISIBLE
            return
        }

        binding.recordbutton.visibility = View.INVISIBLE
        binding.loadingrecording.visibility = View.VISIBLE
        initMediaRecorder()
    }

    private fun playRecordingButtonOnClick() {
        if (rPlayToggle.tag == MEDIA_PLAYING) {
            if (!binding.recordbutton.isVisible) {
                mediaRecorder.stop()
                mediaRecorder.release()
                recordingUri = File(recordingPath).toUri()
                binding.recordbutton.visibility = View.VISIBLE
            } else {
                recordingPlayer?.stop()
                recordingPlayer?.release()
                recordingPlayer = null
            }
            rPlayToggle.tag = MEDIA_STOPPED
            rPlayToggle.setIconResource(MEDIA_PLAY)
            recordingPlayer = initMediaPlayer(recordingUri, rPlayToggle)
        } else {
            rPlayToggle.tag = MEDIA_PLAYING
            rPlayToggle.setIconResource(MEDIA_STOP)
            recordingPlayer?.start()
        }
    }

    private fun initMediaRecorder() {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(recordingPath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try { prepare() }
            catch (e: IOException) {
                val log = "MediaRecorder, $recordingPath"
                val message = e.message.toString()
                Log.e(log, message)
            }

            start()
        }
    }

    private fun initMediaPlayer(uri: Uri, playButton: MaterialButton): MediaPlayer {
        val mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(this@NewPostActivity, uri)
                prepare()
            } catch (e: IOException) {
                val log = "MediaPlayer, $uri"
                val message = e.message.toString()
                Log.e(log, message)
            }
        }

        mediaPlayer.setOnCompletionListener { mp ->
            playButton.setIconResource(MEDIA_PLAY)
            playButton.tag = MEDIA_STOPPED
        }

        return mediaPlayer
    }

    private fun submitButtonOnClick() {
        binding.submitbutton.visibility = View.INVISIBLE
        binding.submitting.visibility = View.VISIBLE

        val map: HashMap<String, Any> = HashMap()

        val sub = intent.getParcelableExtra<Sub>("sub")!!

        map["url"] = "/p/${sub.slug}/newPost"
        map["type"] = object : TypeToken<Post>() {}
        map["title"] = binding.newposttitle.text.toString()
        map["text"] = binding.newposttext.text.toString()

        if (this::imageUri.isInitialized) {
            map["image"] = UriUtils.uri2File(imageUri)
            map["imageType"] = this.contentResolver.getType(imageUri).toString()
        }
        if (this::audioUri.isInitialized) {
            map["audio"] = UriUtils.uri2File(audioUri)
            map["audioType"] = this.contentResolver.getType(audioUri).toString()
        }
        if (this::recordingUri.isInitialized) {
            map["recording"] = UriUtils.uri2File(recordingUri)
            map["recordingType"] = "video/3gpp"
        }
        map["token"] = ""

        runOnUiThread { NetworkManager().post(this, map) }
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
        initMediaRecorder()
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
                    audioPlayer = initMediaPlayer(audioUri, aPlayToggle)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()

        audioPlayer?.stop()
        audioPlayer = null
        recordingPlayer?.stop()
        recordingPlayer = null
    }

    override fun resolvePost(content: Any) {
        post = content as Post
        val intent = intent
        intent.putExtra("post", post)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}