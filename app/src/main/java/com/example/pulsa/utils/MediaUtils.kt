package com.example.pulsa.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.chibde.visualizer.LineVisualizer
import com.example.pulsa.R
import com.example.pulsa.activities.BaseLayoutActivity
import com.google.android.material.button.MaterialButton
import java.io.File
import java.io.IOException

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
private const val MEDIA_PLAY = R.drawable.icons8_play_96
private const val MEDIA_STOP = R.drawable.icons8_stop_96
private const val MEDIA_PLAYING = "playing"
private const val MEDIA_STOPPED = "stopped"

private val recordingPermissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
private val filePermissions: Array<String> = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

private var permissionGranted = false

class MediaUtils {

    var audioPlayer: MediaPlayer? = null
    var recordingPlayer: MediaPlayer? = null
    var recordingUri: Uri? = null
    lateinit var mediaRecorder: MediaRecorder

    // Media Player used in posts and replies
    var medPlayer: MediaPlayer? = null
    private var mediaUrl: String? = ""
    private var mediaVisualizer: LineVisualizer? = null
    private var mediaContext: Context? = null

    fun initMediaRecorder(recordingPath: String): MediaRecorder {
        val mediaRecorder: MediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(recordingPath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                val log = "MediaRecorder, $recordingPath"
                val message = e.message.toString()
                Log.e(log, message)
            }
            start()
        }
        return mediaRecorder
    }

    fun initMediaPlayer(uri: Uri, playButton: MaterialButton, activity: BaseLayoutActivity): MediaPlayer {
        val mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(activity, uri)
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

    fun verifyStoragePermissions(activity: BaseLayoutActivity) {
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

    fun verifyRecordingPermissions(activity: BaseLayoutActivity) {
        ActivityCompat.requestPermissions(
            activity,
            recordingPermissions,
            REQUEST_RECORD_AUDIO_PERMISSION
        )
    }

    fun playAudio(aPlayToggle: MaterialButton, audioUri: Uri, activity: BaseLayoutActivity) {
        if (aPlayToggle.tag == MEDIA_PLAYING) {
            audioPlayer?.stop()
            audioPlayer?.release()
            aPlayToggle.tag = MEDIA_STOPPED
            aPlayToggle.setIconResource(MEDIA_PLAY)
            audioPlayer = initMediaPlayer(audioUri, aPlayToggle, activity)
        } else {
            aPlayToggle.tag = MEDIA_PLAYING
            aPlayToggle.setIconResource(MEDIA_STOP)
            audioPlayer?.start()
        }
    }

    fun record(recordButton: Button, loadingRecording: ProgressBar, activity: BaseLayoutActivity, recordingPath: String) {
        if (!permissionGranted) {
            verifyRecordingPermissions(activity)
            recordButton.visibility = View.INVISIBLE
            loadingRecording.visibility = View.VISIBLE
            return
        }

        recordButton.visibility = View.INVISIBLE
        loadingRecording.visibility = View.VISIBLE
        mediaRecorder = initMediaRecorder(recordingPath)
    }

    fun playRecording(activity: BaseLayoutActivity, rPlayToggle: MaterialButton, recordButton: Button, recordingPath: String) {
        if (rPlayToggle.tag == MEDIA_PLAYING) {
            if (!recordButton.isVisible) {
                mediaRecorder.stop()
                mediaRecorder.release()
                recordingUri = File(recordingPath).toUri()
                recordButton.visibility = View.VISIBLE
            } else {
                recordingPlayer?.stop()
                recordingPlayer?.release()
            }
            rPlayToggle.tag = MEDIA_STOPPED
            rPlayToggle.setIconResource(MEDIA_PLAY)
            recordingPlayer = recordingUri?.let { initMediaPlayer(it, rPlayToggle, activity) }
        } else {
            rPlayToggle.tag = MEDIA_PLAYING
            rPlayToggle.setIconResource(MEDIA_STOP)
            recordingPlayer?.start()
        }
    }

    fun initMediaPlayerWithUrl(context: Context, visualizer: LineVisualizer, button: MaterialButton?, url: String): MediaPlayer? {
        mediaUrl = url
        mediaVisualizer = visualizer
        mediaContext = context

        visualizer.visibility = View.VISIBLE

        val mediaPlayer = MediaPlayer()

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        mediaPlayer.setAudioAttributes(audioAttributes)

        try {
            mediaPlayer.setDataSource(mediaUrl)
            // below line is use to prepare
            // and start our media player.
            mediaPlayer.prepare()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        button?.tag = MEDIA_STOPPED

        mediaPlayer.setOnCompletionListener { mp ->
            button?.setIconResource(MEDIA_PLAY)
            button?.tag = MEDIA_STOPPED
            medPlayer = initMediaPlayerWithUrl(mediaContext!!, mediaVisualizer!!, button, mediaUrl!!)
        }

        medPlayer = mediaPlayer

        val lineVisualizer: LineVisualizer = visualizer
        lineVisualizer.setColor(ContextCompat.getColor(context, R.color.user))
        lineVisualizer.setStrokeWidth(2)
        lineVisualizer.setPlayer(mediaPlayer.audioSessionId)

        return medPlayer
    }

    fun playMedia(button: MaterialButton?) {
        if (button?.tag == MEDIA_PLAYING) {
            medPlayer?.stop()
            medPlayer?.release()
            button.tag = MEDIA_STOPPED
            button.setIconResource(MEDIA_PLAY)
            medPlayer = initMediaPlayerWithUrl(mediaContext!!, mediaVisualizer!!, button, mediaUrl!!)
        } else {
            button?.tag = MEDIA_PLAYING
            button?.setIconResource(MEDIA_STOP)
            medPlayer?.start()
        }
    }
}