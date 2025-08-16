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
    
    @GET("users/{userId}/moodlogs")
    suspend fun getMoodLogsByUser(@Path("userId") userId: Int): Response<List<MoodLog>>
    
    @GET("moodlogs")
    suspend fun getMoodLogsByDate(
        @Query("userId") userId: Int,
        @Query("date") date: String // Format: yyyy-MM-dd
    ): Response<List<MoodLog>>
    
    @GET("moodlogs/calendar")
    suspend fun getMoodLogsByMonth(
        @Query("userId") userId: Int,
        @Query("month") month: String // Format: yyyy-MM
    ): Response<List<MoodLog>>
    
    @PUT("moodlogs/{id}/mood")
    suspend fun updateMoodLogMood(
        @Path("id") id: String,
        @Body request: UpdateMoodRequest
    ): Response<MoodLog>
    
    @DELETE("moodlogs/{id}")
    suspend fun deleteMoodLog(
        @Path("id") id: String,
        @Query("userId") userId: Int
    ): Response<ResponseBody>
    
    @Multipart
    @POST("audio/upload")
    suspend fun uploadAudio(
        @Query("logId") logId: String,
        @Part audio: MultipartBody.Part
    ): Response<ResponseBody>
    
    @GET("audio/{logId}")
    suspend fun getAudio(@Path("logId") logId: String): Response<ResponseBody>
}
