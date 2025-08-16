package com.example.moodii.repository.ml

import android.util.Log
import com.example.moodii.api.ml.MLClient
import com.example.moodii.api.ml.MLEmotionPredictionResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class MLRepository {
    private val mlService = MLClient.service
    private val tag = "MLRepository"
    
    
    suspend fun predictEmotion(audioFile: File): Result<MLEmotionPredictionResponse> {
        return try {
            Log.d(tag, "Starting emotion prediction for file: ${audioFile.name}")
            Log.d(tag, "File size: ${audioFile.length()} bytes")
            Log.d(tag, "File exists: ${audioFile.exists()}")
            
            if (!audioFile.exists()) {
                return Result.failure(Exception("Audio file does not exist"))
            }
            
            if (audioFile.length() == 0L) {
                return Result.failure(Exception("Audio file is empty"))
            }
            
            // Determine content type based on file extension
            val contentType = when (audioFile.extension.lowercase()) {
                "mp3" -> "audio/mpeg"
                "wav" -> "audio/wav"
                "m4a" -> "audio/mp4"
                "aac" -> "audio/aac"
                "ogg" -> "audio/ogg"
                "flac" -> "audio/flac"
                else -> "audio/mp4" // Default for Android MediaRecorder AAC output
            }
            
            Log.d(tag, "Using content type: $contentType")
            
            // Create multipart body part
            val requestBody = audioFile.asRequestBody(contentType.toMediaTypeOrNull())
            val audioPart = MultipartBody.Part.createFormData("audio", audioFile.name, requestBody)
            
            Log.d(tag, "Making emotion prediction request...")
            val response = mlService.predictEmotion(audioPart)
            
            Log.d(tag, "Prediction response code: ${response.code()}")
            
            if (response.isSuccessful) {
                val prediction = response.body()
                if (prediction != null) {
                    Log.d(tag, "Emotion predicted: ${prediction.predicted_emotion} (confidence: ${prediction.confidence})")
                    Log.d(tag, "All predictions: ${prediction.all_predictions}")
                    Result.success(prediction)
                } else {
                    Log.e(tag, "Prediction response body is null")
                    Result.failure(Exception("Empty prediction response"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Prediction request failed: ${response.code()} - ${response.message()}")
                Log.e(tag, "Error body: $errorBody")
                Result.failure(Exception("Prediction failed: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error during emotion prediction", e)
            Result.failure(e)
        }
    }
    
    // (Removed health/model info/test methods – backend proxy only exposes predict)
}
