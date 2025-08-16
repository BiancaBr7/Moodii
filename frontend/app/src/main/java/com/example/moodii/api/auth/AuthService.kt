package com.example.moodii.api.auth

import com.example.moodii.data.auth.AuthRequest
import com.example.moodii.data.auth.AuthResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Body

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: AuthRequest): Response<Map<String, String>>
}