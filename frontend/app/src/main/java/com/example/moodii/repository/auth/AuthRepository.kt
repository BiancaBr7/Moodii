package com.example.moodii.repository.auth

import com.example.moodii.api.auth.AuthClient
import com.example.moodii.data.auth.AuthRequest
import com.example.moodii.data.auth.AuthResponse
import retrofit2.Response

class AuthRepository {
    suspend fun login(email: String, password: String): Response<AuthResponse> {
        val request = AuthRequest(email, password)
        return AuthClient.authService.login(request)
    }

    suspend fun register(email: String, password: String): Response<Map<String, String>> {
        val request = AuthRequest(email, password)
        return AuthClient.authService.register(request)
    }
}