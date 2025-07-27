package com.example.moodii.data.moodlog

import com.google.gson.annotations.SerializedName

data class MoodLog(
    @SerializedName("id")
    val id: String? = null,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("transcription")
    val transcription: String,
    
    @SerializedName("moodType")
    val moodType: Int,
    
    @SerializedName("userId")
    val userId: String,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

data class MoodLogRequest(
    @SerializedName("title")
    val title: String,
    
    @SerializedName("transcription")
    val transcription: String,
    
    @SerializedName("moodType")
    val moodType: Int,
    
    @SerializedName("userId")
    val userId: Int
)

data class UpdateMoodRequest(
    @SerializedName("moodType")
    val moodType: Int
)
