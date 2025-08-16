package com.example.moodii.api.ml

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MLService {
    // Backend proxy only exposes /api/ml/predict; health/model-info direct ML endpoints removed for mobile
    @Multipart
    @POST("predict")
    suspend fun predictEmotion(
        @Part audio: MultipartBody.Part
    ): Response<MLEmotionPredictionResponse>
}

// Response data classes
// (Removed MLHealthResponse, MLModelInfoResponse, MLFeatureConfig) – not used via backend proxy

data class MLEmotionPredictionResponse(
    val predicted_emotion: String,
    val confidence: Double,
    val all_predictions: Map<String, Double>,
    val uncertainty: Double,
    val prediction_time: Double,
    val status: String,
    val request_id: String? = null
)

// (Removed batch prediction response) – backend proxy currently only supports single prediction
