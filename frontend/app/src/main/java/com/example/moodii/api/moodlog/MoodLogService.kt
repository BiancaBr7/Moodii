package com.example.moodii.api.moodlog

import com.example.moodii.data.moodlog.MoodLog
import com.example.moodii.data.moodlog.MoodLogRequest
import com.example.moodii.data.moodlog.UpdateMoodRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MoodLogService {
    
    @POST("moodlogs")
    suspend fun createMoodLog(@Body request: MoodLogRequest): Response<MoodLog>
    
    @GET("moodlogs/user/{userId}")
    suspend fun getMoodLogsByUser(@Path("userId") userId: String): Response<List<MoodLog>>
    
    @GET("moodlogs/user/{userId}/date/{date}")
    suspend fun getMoodLogsByDate(
        @Path("userId") userId: String,
        @Path("date") date: String // Format: yyyy-MM-dd
    ): Response<List<MoodLog>>
    
    @GET("moodlogs/user/{userId}/month/{month}")
    suspend fun getMoodLogsByMonth(
        @Path("userId") userId: String,
        @Path("month") month: String // Format: yyyy-MM
    ): Response<List<MoodLog>>
    
    @PUT("moodlogs/{id}/mood")
    suspend fun updateMoodLogMood(
        @Path("id") id: String,
        @Body request: UpdateMoodRequest
    ): Response<MoodLog>
    
    @DELETE("moodlogs/{id}")
    suspend fun deleteMoodLog(@Path("id") id: String): Response<ResponseBody>
    
    @Multipart
    @POST("audio/upload")
    suspend fun uploadAudio(
        @Query("logId") logId: String,
        @Part audio: MultipartBody.Part
    ): Response<ResponseBody>
    
    @GET("audio/{logId}")
    suspend fun getAudio(@Path("logId") logId: String): Response<ResponseBody>
}
