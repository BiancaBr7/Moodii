package com.example.moodii.repository.ml

import android.util.Log
import com.example.moodii.api.ml.MLClient
import com.example.moodii.api.ml.MLEmotionPredictionResponse
import com.example.moodii.api.ml.MLHealthResponse
import com.example.moodii.api.ml.MLModelInfoResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class MLRepository {
    private val mlService = MLClient.service
    private val tag = "MLRepository"
    
    suspend fun checkMLHealth(): Result<MLHealthResponse> {
        return try {
            Log.d(tag, "Checking ML API health...")
            val response = mlService.getHealth()
            
            if (response.isSuccessful) {
                val healthData = response.body()
                if (healthData != null) {
                    Log.d(tag, "ML API is healthy: ${healthData.status}")
                    Result.success(healthData)
                } else {
                    Log.e(tag, "Health response body is null")
                    Result.failure(Exception("Empty health response"))
                }
            } else {
                Log.e(tag, "Health check failed: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Health check failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error checking ML health", e)
            Result.failure(e)
        }
    }
    
    suspend fun getModelInfo(): Result<MLModelInfoResponse> {
        return try {
            Log.d(tag, "Getting ML model information...")
            val response = mlService.getModelInfo()
            
            if (response.isSuccessful) {
                val modelInfo = response.body()
                if (modelInfo != null) {
                    Log.d(tag, "Model info received: ${modelInfo.model_type}")
                    Result.success(modelInfo)
                } else {
                    Log.e(tag, "Model info response body is null")
                    Result.failure(Exception("Empty model info response"))
                }
            } else {
                Log.e(tag, "Model info request failed: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Model info request failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting model info", e)
            Result.failure(e)
        }
    }
    
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
    
    suspend fun testMLConnection(): Result<String> {
        return try {
            Log.d(tag, "Testing ML API connection...")
            
            // First check health
            val healthResult = checkMLHealth()
            if (healthResult.isFailure) {
                return Result.failure(healthResult.exceptionOrNull() ?: Exception("Health check failed"))
            }
            
            // Then get model info
            val modelInfoResult = getModelInfo()
            if (modelInfoResult.isFailure) {
                return Result.failure(modelInfoResult.exceptionOrNull() ?: Exception("Model info failed"))
            }
            
            val health = healthResult.getOrNull()!!
            val modelInfo = modelInfoResult.getOrNull()!!
            
            val connectionSummary = """
                ML API Connection Test Results:
                ✅ Health Status: ${health.status}
                ✅ Model Loaded: ${health.model_loaded}
                ✅ Model Type: ${modelInfo.model_type}
                ✅ Supported Emotions: ${modelInfo.emotion_labels.joinToString(", ")}
                ✅ Max File Size: ${modelInfo.max_file_size_mb}MB
                ✅ Supported Formats: ${modelInfo.supported_formats.joinToString(", ")}
            """.trimIndent()
            
            Log.i(tag, "ML API connection test successful")
            Result.success(connectionSummary)
            
        } catch (e: Exception) {
            Log.e(tag, "ML connection test failed", e)
            Result.failure(e)
        }
    }
}
