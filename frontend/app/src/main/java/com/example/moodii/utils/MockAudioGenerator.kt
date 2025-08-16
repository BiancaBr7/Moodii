package com.example.moodii.utils

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import kotlin.math.sin

class MockAudioGenerator {
    
    companion object {
        fun generateTestAudio(context: Context, durationSeconds: Int = 5): File {
            val file = File(context.filesDir, "test_audio_${System.currentTimeMillis()}.m4a")
            
            try {
                // Generate a simple sine wave audio for testing
                val sampleRate = 44100
                val samples = durationSeconds * sampleRate
                val frequency = 440.0 // A4 note
                
                val audioData = ByteArray(samples * 2) // 16-bit audio
                
                for (i in 0 until samples) {
                    val sample = (sin(2.0 * Math.PI * frequency * i / sampleRate) * Short.MAX_VALUE).toInt().toShort()
                    // Little-endian format
                    audioData[i * 2] = (sample.toInt() and 0xFF).toByte()
                    audioData[i * 2 + 1] = ((sample.toInt() shr 8) and 0xFF).toByte()
                }
                
                // Write simple WAV header + data (for testing purposes)
                FileOutputStream(file).use { fos ->
                    // This is a simplified approach - in real implementation you'd want proper audio encoding
                    fos.write(audioData)
                }
                
                Log.d("MockAudioGenerator", "Generated test audio: ${file.absolutePath}")
                return file
                
            } catch (e: Exception) {
                Log.e("MockAudioGenerator", "Error generating test audio", e)
                throw e
            }
        }
    }
}
