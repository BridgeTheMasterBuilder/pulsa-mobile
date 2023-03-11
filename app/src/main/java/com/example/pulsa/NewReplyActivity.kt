package com.example.pulsa

import android.Manifest
import android.app.Activity
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.pulsa.databinding.ActivityNewReplyBinding
import java.io.IOException
import java.time.LocalDateTime

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

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

        fileName = "${externalCacheDir?.absolutePath}/temp.3gp"

        binding.recordbutton.setOnClickListener {

            if (recording) {
                recording = !recording
                mediaRecorder.stop()
                mediaRecorder.release()

                mediaPlayer = MediaPlayer().apply {
                    try {
                        setDataSource(fileName)
                        prepare()
                    } catch (e: IOException) {
                        // todo
                        System.err.println("oops")
                    }
                }
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

        binding.playbutton.setOnClickListener {
            Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show()
            if (recording) {
                mediaRecorder.stop()
                mediaRecorder.release()

                mediaPlayer = MediaPlayer().apply {
                    try {
                        setDataSource(fileName)
                        prepare()
                    } catch (e: IOException) {
                        // todo
                        System.err.println("oops")
                    }
                }
            } else if (playing)
                mediaPlayer.stop()

            playing = !playing

            mediaPlayer.start()
        }

        binding.postbutton.setOnClickListener {
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
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) onPermissionsGranted()
//        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
//            grantResults[0] == PackageManager.PERMISSION_GRANTED
//        } else {
//            false
//        }
//        if (!permissionToRecordAccepted) finish()
    }

    private fun onPermissionsGranted() {
        Toast.makeText(
            this,
            "success!",
            Toast.LENGTH_SHORT
        ).show()

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
}