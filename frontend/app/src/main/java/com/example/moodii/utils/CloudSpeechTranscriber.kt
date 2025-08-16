package com.example.moodii.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Cloud Speech Transcription using Google Cloud Speech-to-Text API
 * This provides higher accuracy than Android's built-in speech recognition
 * 
 * Setup Requirements:
 * 1. Enable Google Cloud Speech-to-Text API
 * 2. Create a service account and download credentials
 * 3. Add the API key to your app
 */
class CloudSpeechTranscriber(private val context: Context) {
    
    private val client = OkHttpClient()
    
    // Replace with your actual Google Cloud Speech API key
    private val apiKey = "YOUR_GOOGLE_CLOUD_API_KEY"
    private val apiUrl = "https://speech.googleapis.com/v1/speech:recognize"
    
    /**
     * Transcribe audio file using Google Cloud Speech-to-Text
     */
    suspend fun transcribeAudioFile(audioFile: File): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (apiKey == "YOUR_GOOGLE_CLOUD_API_KEY") {
                Log.w("CloudSpeechTranscriber", "API key not configured, using fallback")
                Result.success("Cloud transcription not configured. Please add your Google Cloud API key.")
            } else {
                performCloudTranscription(audioFile)
            }
        } catch (e: Exception) {
            Log.e("CloudSpeechTranscriber", "Error in cloud transcription", e)
            Result.failure(e)
        }
    }
    
    private suspend fun performCloudTranscription(audioFile: File): Result<String> = suspendCoroutine { continuation ->
        try {
            // Convert audio file to base64
            val audioBytes = audioFile.readBytes()
            val audioBase64 = android.util.Base64.encodeToString(audioBytes, android.util.Base64.NO_WRAP)
            
            // Create request JSON
            val requestJson = JSONObject().apply {
                put("config", JSONObject().apply {
                    put("encoding", "MP4") // For .m4a files
                    put("sampleRateHertz", 44100)
                    put("languageCode", "en-US")
                    put("enableAutomaticPunctuation", true)
                    put("enableWordTimeOffsets", false)
                    put("model", "latest_long") // Better for longer audio
                })
                put("audio", JSONObject().apply {
                    put("content", audioBase64)
                })
            }
            
            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaTypeOrNull())
            
            val request = Request.Builder()
                .url("$apiUrl?key=$apiKey")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build()
            
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("CloudSpeechTranscriber", "Network request failed", e)
                    continuation.resume(Result.failure(e))
                }
                
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val responseBody = response.body?.string()
                        if (response.isSuccessful && responseBody != null) {
                            val jsonResponse = JSONObject(responseBody)
                            val results = jsonResponse.optJSONArray("results")
                            
                            if (results != null && results.length() > 0) {
                                val firstResult = results.getJSONObject(0)
                                val alternatives = firstResult.getJSONArray("alternatives")
                                if (alternatives.length() > 0) {
                                    val transcript = alternatives.getJSONObject(0).getString("transcript")
                                    Log.d("CloudSpeechTranscriber", "Transcription successful: $transcript")
                                    continuation.resume(Result.success(transcript))
                                } else {
                                    continuation.resume(Result.success("No speech detected"))
                                }
                            } else {
                                continuation.resume(Result.success("No speech detected"))
                            }
                        } else {
                            val error = "API request failed: ${response.code} - $responseBody"
                            Log.e("CloudSpeechTranscriber", error)
                            continuation.resume(Result.failure(Exception(error)))
                        }
                    } catch (e: Exception) {
                        Log.e("CloudSpeechTranscriber", "Error parsing response", e)
                        continuation.resume(Result.failure(e))
                    }
                }
            })
            
        } catch (e: Exception) {
            Log.e("CloudSpeechTranscriber", "Error preparing request", e)
            continuation.resume(Result.failure(e))
        }
    }
    
    /**
     * Alternative: Use OpenAI Whisper API for transcription
     * Often more accurate and cost-effective than Google Cloud
     */
    suspend fun transcribeWithWhisper(audioFile: File): Result<String> = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine { continuation ->
            try {
                val whisperApiKey = "YOUR_OPENAI_API_KEY"
                if (whisperApiKey == "YOUR_OPENAI_API_KEY") {
                    continuation.resume(Result.success("OpenAI Whisper API key not configured"))
                    return@suspendCoroutine
                }
                
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", audioFile.name, audioFile.asRequestBody("audio/mp4".toMediaTypeOrNull()))
                    .addFormDataPart("model", "whisper-1")
                    .addFormDataPart("language", "en")
                    .build()
                
                val request = Request.Builder()
                    .url("https://api.openai.com/v1/audio/transcriptions")
                    .post(requestBody)
                    .addHeader("Authorization", "Bearer $whisperApiKey")
                    .build()
                
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        continuation.resume(Result.failure(e))
                    }
                    
                    override fun onResponse(call: Call, response: Response) {
                        try {
                            val responseBody = response.body?.string()
                            if (response.isSuccessful && responseBody != null) {
                                val jsonResponse = JSONObject(responseBody)
                                val transcript = jsonResponse.getString("text")
                                continuation.resume(Result.success(transcript))
                            } else {
                                continuation.resume(Result.failure(Exception("Whisper API failed: ${response.code}")))
                            }
                        } catch (e: Exception) {
                            continuation.resume(Result.failure(e))
                        }
                    }
                })
                
            } catch (e: Exception) {
                continuation.resume(Result.failure(e))
            }
        }
    }
}
