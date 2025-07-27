package com.example.moodii.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.IOException

class AudioRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var isRecording = false

    fun hasRecordPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context, 
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun startRecording(outputFile: File): Boolean {
        if (!hasRecordPermission()) {
            Log.e("AudioRecorder", "Record audio permission not granted")
            return false
        }

        try {
            this.outputFile = outputFile
            
            // Create MediaRecorder
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile.absolutePath)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)

                prepare()
                start()
                isRecording = true
                Log.d("AudioRecorder", "Recording started to: ${outputFile.absolutePath}")
            }
            
            return true
        } catch (e: IOException) {
            Log.e("AudioRecorder", "Failed to start recording", e)
            cleanup()
            return false
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Error starting recording", e)
            cleanup()
            return false
        }
    }

    fun stopRecording(): File? {
        return try {
            if (isRecording && mediaRecorder != null) {
                mediaRecorder?.apply {
                    stop()
                    release()
                }
                isRecording = false
                Log.d("AudioRecorder", "Recording stopped")
                val file = outputFile
                cleanup()
                file
            } else {
                Log.w("AudioRecorder", "Not currently recording")
                null
            }
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Error stopping recording", e)
            cleanup()
            null
        }
    }

    fun pauseRecording() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isRecording) {
                mediaRecorder?.pause()
                Log.d("AudioRecorder", "Recording paused")
            }
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Error pausing recording", e)
        }
    }

    fun resumeRecording() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isRecording) {
                mediaRecorder?.resume()
                Log.d("AudioRecorder", "Recording resumed")
            }
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Error resuming recording", e)
        }
    }

    fun isCurrentlyRecording(): Boolean = isRecording

    private fun cleanup() {
        try {
            mediaRecorder?.release()
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Error during cleanup", e)
        }
        mediaRecorder = null
        outputFile = null
        isRecording = false
    }

    fun getOutputFile(): File? = outputFile
}
