package com.example.moodii.api.auth

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.moodii.BuildConfig

object AuthClient {
    private val baseUrl: String by lazy { BuildConfig.API_BASE_URL.trimEnd('/') + "/api/" }

    val authService: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }
}