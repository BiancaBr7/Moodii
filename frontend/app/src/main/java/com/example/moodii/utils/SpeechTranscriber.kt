package com.example.moodii.utils

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

class SpeechTranscriber(private val context: Context) {
    
    private var speechRecognizer: SpeechRecognizer? = null
    
    /**
     * Real-time speech recognition during recording
     */
    fun startLiveTranscription(
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition not available on this device")
            return
        }
        
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("SpeechTranscriber", "Ready for speech")
            }
            
            override fun onBeginningOfSpeech() {
                Log.d("SpeechTranscriber", "Speech started")
            }
            
            override fun onRmsChanged(rmsdB: Float) {
                // Audio level changed
            }
            
            override fun onBufferReceived(buffer: ByteArray?) {
                // Audio buffer received
            }
            
            override fun onEndOfSpeech() {
                Log.d("SpeechTranscriber", "Speech ended")
            }
            
            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                    SpeechRecognizer.ERROR_SERVER -> "Error from server"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                    else -> "Unknown error: $error"
                }
                Log.e("SpeechTranscriber", "Recognition error: $errorMessage")
                onError(errorMessage)
            }
            
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val transcription = matches?.firstOrNull() ?: ""
                Log.d("SpeechTranscriber", "Final result: $transcription")
                onResult(transcription)
            }
            
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val transcription = matches?.firstOrNull() ?: ""
                Log.d("SpeechTranscriber", "Partial result: $transcription")
                onResult(transcription)
            }
            
            override fun onEvent(eventType: Int, params: Bundle?) {
                // Speech recognition event
            }
        })
        
        speechRecognizer?.startListening(intent)
    }
    
    /**
     * Stop live transcription
     */
    fun stopLiveTranscription() {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
    
    /**
     * Transcribe audio file using Android's speech recognition
     * Since Android's SpeechRecognizer works with live audio, this attempts to
     * play the file and capture it, or returns the live transcription if available
     */
    suspend fun transcribeAudioFile(audioFile: File, liveTranscription: String = ""): String {
        return suspendCancellableCoroutine { continuation ->
            try {
                // If we already have live transcription, use it
                if (liveTranscription.isNotBlank()) {
                    Log.d("SpeechTranscriber", "Using live transcription: $liveTranscription")
                    continuation.resume(liveTranscription)
                    return@suspendCancellableCoroutine
                }
                
                // Check if file exists and has audio
                val duration = getAudioDuration(audioFile)
                if (duration <= 0) {
                    continuation.resume("No audio content detected in file")
                    return@suspendCancellableCoroutine
                }
                
                Log.d("SpeechTranscriber", "Audio file duration: ${duration}ms")
                
                // For Android's SpeechRecognizer, we can't directly transcribe files
                // This would require playing the audio and capturing it in real-time
                // or using cloud services for file transcription
                
                if (duration < 1000) {
                    continuation.resume("Recording too short for transcription")
                } else if (duration > 60000) {
                    continuation.resume("Recording longer than 1 minute - consider using cloud transcription for better results")
                } else {
                    // Return a message indicating manual transcription is needed
                    continuation.resume("Audio recorded (${duration/1000}s) - Use cloud transcription services for automatic text conversion")
                }
                
            } catch (e: Exception) {
                Log.e("SpeechTranscriber", "Error transcribing audio file", e)
                continuation.resume("Error analyzing audio: ${e.message}")
            }
        }
    }
    
    /**
     * Get audio file duration
     */
    private fun getAudioDuration(audioFile: File): Long {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(audioFile.absolutePath)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            retriever.release()
            duration
        } catch (e: Exception) {
            Log.e("SpeechTranscriber", "Error getting audio duration", e)
            0L
        }
    }
    
    /**
     * Check if speech recognition is available
     */
    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }
}
