package com.example.util

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.Looper
import java.io.File
import java.io.IOException

class AudioRecorderHelper(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentOutputFile: File? = null

    var isRecording = false
        private set

    var isPlaying = false
        private set

    private var durationSeconds = 0
    private var durationHandler: Handler? = null
    private var durationRunnable: Runnable? = null

    fun startRecording(onDurationUpdate: (Int) -> Unit): String? {
        val audioDir = File(context.filesDir, "audio").apply { if (!exists()) mkdirs() }
        val file = File(audioDir, "audio_${System.currentTimeMillis()}.m4a")
        currentOutputFile = file

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(44100)
            setAudioEncodingBitRate(128000)
            setOutputFile(file.absolutePath)
        }

        return try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            isRecording = true
            durationSeconds = 0

            durationHandler = Handler(Looper.getMainLooper())
            durationRunnable = object : Runnable {
                override fun run() {
                    if (isRecording) {
                        durationSeconds++
                        onDurationUpdate(durationSeconds)
                        durationHandler?.postDelayed(this, 1000)
                    }
                }
            }
            durationHandler?.postDelayed(durationRunnable!!, 1000)

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            releaseRecorder()
            null
        }
    }

    fun stopRecording(): String? {
        if (!isRecording) return currentOutputFile?.absolutePath
        try {
            mediaRecorder?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            releaseRecorder()
        }
        return currentOutputFile?.absolutePath
    }

    private fun releaseRecorder() {
        isRecording = false
        durationRunnable?.let { durationHandler?.removeCallbacks(it) }
        durationHandler = null
        durationRunnable = null
        try {
            mediaRecorder?.release()
        } catch (e: Exception) {
            // ignore
        }
        mediaRecorder = null
    }

    fun playAudio(filePath: String, onComplete: () -> Unit) {
        stopPlayback()
        val file = File(filePath)
        if (!file.exists()) return

        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(filePath)
                prepare()
                start()
                this@AudioRecorderHelper.isPlaying = true
                setOnCompletionListener {
                    this@AudioRecorderHelper.isPlaying = false
                    onComplete()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                this@AudioRecorderHelper.isPlaying = false
                onComplete()
            }
        }
    }

    fun stopPlayback() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
        } catch (e: Exception) {
            // ignore
        }
        mediaPlayer = null
        isPlaying = false
    }

    fun release() {
        releaseRecorder()
        stopPlayback()
    }
}
