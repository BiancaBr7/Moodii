package com.example.moodii.api.ml

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MLService {
    
    @GET("health")
    suspend fun getHealth(): Response<MLHealthResponse>
    
    @GET("model-info")
    suspend fun getModelInfo(): Response<MLModelInfoResponse>
    
    @Multipart
    @POST("predict")
    suspend fun predictEmotion(
        @Part audio: MultipartBody.Part
    ): Response<MLEmotionPredictionResponse>
    
    @Multipart
    @POST("predict-batch")
    suspend fun predictEmotionBatch(
        @Part("audio") audioFiles: List<MultipartBody.Part>
    ): Response<MLBatchPredictionResponse>
}

// Response data classes
data class MLHealthResponse(
    val status: String,
    val model_loaded: Boolean,
    val model_load_time: Double,
    val total_requests: Int,
    val timestamp: String,
    val version: String,
    val message: String
)

data class MLModelInfoResponse(
    val model_type: String,
    val model_name: String,
    val input_shape: List<Any>,
    val output_shape: List<Any>,
    val total_parameters: Int,
    val emotion_labels: List<String>,
    val feature_config: MLFeatureConfig,
    val supported_formats: List<String>,
    val max_file_size_mb: Int,
    val description: String,
    val status: String
)

data class MLFeatureConfig(
    val n_mfcc: Int,
    val feature_length: Int,
    val sample_rate: Int
)

data class MLEmotionPredictionResponse(
    val predicted_emotion: String,
    val confidence: Double,
    val all_predictions: Map<String, Double>,
    val uncertainty: Double,
    val prediction_time: Double,
    val status: String,
    val request_id: String? = null
)

data class MLBatchPredictionResponse(
    val predictions: List<MLEmotionPredictionResponse>,
    val batch_size: Int,
    val total_time: Double,
    val status: String
)
