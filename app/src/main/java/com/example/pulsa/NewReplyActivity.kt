package com.example.pulsa

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.pulsa.databinding.ActivityNewReplyBinding
import java.io.IOException
import java.time.LocalDateTime


private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
private const val SELECT_PICTURE = 200
private const val SELECT_AUDIO = 2

class NewReplyActivity : BaseLayoutActivity() {
    private lateinit var binding: ActivityNewReplyBinding
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var mediaPlayer: MediaPlayer
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
    private lateinit var fileName: String
    private var recording = false
    private var playing = false
    private var permissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewReplyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView.visibility = View.INVISIBLE
        fileName =
            "${externalCacheDir?.absolutePath}/temp.3gp"

        binding.recordbutton.setOnClickListener {
            recordButtonOnClick()
        }

        binding.playbutton.setOnClickListener {
            playButtonOnClick()
        }

        binding.imagebutton.setOnClickListener {
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT

            startActivityForResult(Intent.createChooser(i, "Select picture"), SELECT_PICTURE)
        }

        binding.audiobutton.setOnClickListener {
            val i = Intent()
            i.type = "audio/*"
            i.action = Intent.ACTION_GET_CONTENT

            startActivityForResult(Intent.createChooser(i, "Select audio file"), SELECT_AUDIO)
        }

        binding.postbutton.setOnClickListener {
            postButtonOnClick()
        }
    }

    private fun recordButtonOnClick() {
        if (recording) {
            recording = !recording
            mediaRecorder.stop()
            mediaRecorder.release()

            initMediaPlayer()
        } else if (permissionGranted) {
            recording = !recording
            mediaRecorder.start()
        } else
            ActivityCompat.requestPermissions(
                this,
                permissions,
                REQUEST_RECORD_AUDIO_PERMISSION
            )
    }

    private fun playButtonOnClick() {
        if (!this::mediaPlayer.isInitialized) {
            Toast.makeText(this, "No audio file uploaded", Toast.LENGTH_SHORT).show()
            return
        }

        if (recording) {
            recording = false
            mediaRecorder.stop()
            mediaRecorder.release()

            initMediaPlayer()
        } else if (playing)
            mediaPlayer.stop()

        playing = !playing

        mediaPlayer.start()
    }

    private fun initMediaRecorder() {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                // todo
                System.err.println("oops")
            }

            recording = true

            start()
        }
    }

    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
            } catch (e: IOException) {
                // todo
                System.err.println("oops")
            }
        }
    }

    private fun postButtonOnClick() {
        val text = binding.newreplytext.text.toString()
        val user = User(
            1,
            "Anonymous",
            "",
            "Anonymous",
            "https://res.cloudinary.com/dc6h0nrwk/image/upload/v1668893599/a6zqfrxfflxw5gtspwjr.png",
            "",
            mutableListOf(),
            mutableListOf(),
            LocalDateTime.now(),
            LocalDateTime.now()
        )

        val intent = intent
        val reply = Reply(
            35,
            Content(
                36,
                text,
                "test",
                "test",
                "recording",
                LocalDateTime.now(),
                LocalDateTime.now()
            ),
            user,
            intent.getParcelableExtra("sub")!!,
            0,
            mutableListOf(),
            mutableListOf(),
            LocalDateTime.now(),
            LocalDateTime.now()
        )

        if (intent.getLongExtra("replyId", NO_REPLY) != NO_REPLY) {
            intent.putExtra("nestedReply", reply)
        } else {
            intent.putExtra("reply", reply)
        }

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
        initMediaRecorder()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                val image = data?.data

                binding.imageView.visibility = View.VISIBLE

                binding.imageView.setImageURI(image)
            } else if (requestCode == SELECT_AUDIO) {
                val audio = data?.data

                val ctx = this

                audio?.let {
                    mediaPlayer = MediaPlayer().apply {
                        try {
                            setDataSource(ctx, it)
                            prepare()
                        } catch (e: IOException) {
                            // todo
                            System.err.println("oops")
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()

        if (this::mediaRecorder.isInitialized) mediaRecorder.stop()
        if (this::mediaPlayer.isInitialized) mediaPlayer.stop()
    }
}