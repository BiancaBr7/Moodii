package com.example.moodii.api.moodlog

import com.example.moodii.api.auth.AuthorizedClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MoodLogClient {
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    val moodLogService: MoodLogService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(AuthorizedClient.okHttpClient) // Use authorized client for JWT
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MoodLogService::class.java)
    }
}
