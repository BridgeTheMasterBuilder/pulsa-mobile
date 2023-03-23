package com.example.pulsa.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import androidx.core.app.ActivityCompat
import com.blankj.utilcode.util.UriUtils
import com.example.pulsa.databinding.ActivityNewPostBinding
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.*
import com.google.gson.reflect.TypeToken
import java.io.IOException

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
private const val SELECT_PICTURE = 200
private const val SELECT_AUDIO = 2

class NewPostActivity : BaseLayoutActivity() {
    private lateinit var binding: ActivityNewPostBinding
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var audioPlayer: MediaPlayer
    private lateinit var recordingPlayer: MediaPlayer
    private lateinit var post: Post
    private lateinit var audioUri: Uri
    private lateinit var imageUri: Uri
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var recording = false
    private var playing = false
    private var permissionGranted = false
    private var imagePath = ""
    private var audioPath = ""
    private var recordingPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        verifyStoragePermissions(this);

        recordingPath = "${externalCacheDir?.absolutePath}/tempRecording.3gp"
        audioPath = "${externalCacheDir?.absolutePath}/tempAudio.3gp"

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

        /*binding.postbutton.setOnClickListener {
            val sub: Sub = intent.getParcelableExtra("sub")!!
            val slug = sub.slug
            var body: HashMap<String, RequestBody> = hashMapOf(
                "audio" to file.asRequestBody("multipart/form-data".toMediaTypeOrNull()),
                "image" to imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            )
            var map: HashMap<String, Any> = HashMap()
            map["type"] = object: TypeToken<Post>(){}
            map["title"] = title
            map["text"] = text
            map["audio"] = MultipartBody
            map["image"] = MultipartBody
            map["recording"] = ""
            map["session"] = Cookie
            map["url"] = "/p/${slug}/newPost"
            runOnUiThread{ NetworkManager().post(this, map, body) }
        }*/

        /*binding.postbutton.setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            val post = Post(
                100,
                title,
                Content(
                    20,
                    text,
                    "test",
                    "test",
                    "recording",
                    LocalDateTime.now(),
                    LocalDateTime.now()
                ),
                user,
                Sub(-1, "", "", mutableListOf(), "", 0),
                0,
                mutableListOf(),
                mutableListOf(),
                LocalDateTime.now(),
                LocalDateTime.now()
            )

            intent.putExtra("post", post)
            setResult(Activity.RESULT_OK, intent)
            finish()
            startActivity(intent)

        }*/
    }

    private fun verifyStoragePermissions(activity: NewPostActivity) {
        val permission =
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            );
        var permissions: Array<String> = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                permissions,
                1
            );
        }
    }

    private fun recordButtonOnClick() {
        if (recording) {
            recording = !recording
            mediaRecorder.stop()
            mediaRecorder.release()
            binding.playrecordingbutton.visibility = View.VISIBLE

            recordingPlayer = initMediaPlayer(recordingPath)
            return
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

    private fun playAudioButtonOnClick() {
        if (playing) audioPlayer.stop()
        if (!this::audioPlayer.isInitialized) {
            audioPlayer = initMediaPlayer(audioPath)
        }
        playing = !playing
        audioPlayer.start()
    }

    private fun playRecordingButtonOnClick() {
        if (recording) {
            recording = false
            mediaRecorder.stop()
            mediaRecorder.release()
            recordingPlayer = initMediaPlayer(recordingPath)
        } else if (playing)
            recordingPlayer.stop()

        playing = !playing
        recordingPlayer.start()
    }

    private fun initMediaRecorder() {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(recordingPath)
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

    private fun initMediaPlayer(fileName: String): MediaPlayer {
        val mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
            } catch (e: IOException) {
                // todo
                System.err.println("oops")
            }
        }
        return mediaPlayer
    }

    private fun submitButtonOnClick() {
        val map: HashMap<String, Any> = HashMap()

        val sub = intent.getParcelableExtra<Sub>("sub")!!

        map["slug"] = sub.slug
        map["type"] = object : TypeToken<Post>() {}
        map["title"] = binding.newposttitle.text.toString()
        map["text"] = binding.newposttext.text.toString()
        map["url"] = "/p/${sub.slug}/newPost"
        map["image"] = UriUtils.uri2File(imageUri)
        map["audio"] = UriUtils.uri2File(audioUri)
        map["recording"] = UriUtils.uri2File(audioUri)
        map["imageType"] = this.contentResolver.getType(imageUri).toString()
        map["audioType"] = this.contentResolver.getType(audioUri).toString()
        map["recordingType"] = this.contentResolver.getType(audioUri).toString()
        map["token"] = ""

        runOnUiThread { NetworkManager().post(this, map) }
    }

    fun get_filename_by_uri(uri: Uri): String {
        contentResolver.query(uri, null, null, null, null).use { cursor ->
            cursor?.let {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                it.moveToFirst()
                return it.getString(nameIndex)
            }
        }
        return ""
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) return

        when (requestCode) {
            SELECT_PICTURE -> {
                data?.data?.let { image ->
                    imageUri = image
                    imagePath =
                        "https://res.cloudinary.com/dc6h0nrwk/image/upload/v1666386282/xov6nkbsxf3hmhuqb3jn.png"
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
                    audioPath = audio.toString()
                    binding.playaudiobutton.visibility = View.VISIBLE
                    audioPlayer = MediaPlayer().apply {
                        try {
                            setDataSource(this@NewPostActivity, audio)
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
        if (this::audioPlayer.isInitialized) audioPlayer.stop()
        if (this::recordingPlayer.isInitialized) recordingPlayer.stop()
    }

    override fun resolvePost(content: Any) {
        post = content as Post
        println("WOOP WOOP")
        println(post.title)
        val intent = intent
        intent.putExtra("post", post)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}