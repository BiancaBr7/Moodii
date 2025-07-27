package com.example.moodii.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.moodii.api.auth.AuthClient
import com.example.moodii.data.auth.AuthRequest
import com.example.moodii.data.auth.AuthResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthManager(context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("moodii_auth", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
    
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = AuthRequest(email, password)
                val response = AuthClient.authService.login(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    val token = authResponse.token
                    
                    // Decode token to extract user info
                    val payload = JwtDecoder.decodeToken(token)
                    if (payload != null && payload.userId != null && payload.username != null) {
                        // Save auth data
                        saveAuthData(token, payload.userId, payload.username)
                        Result.success(authResponse)
                    } else {
                        Result.failure(Exception("Invalid token format"))
                    }
                } else {
                    Result.failure(Exception("Login failed: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    fun saveAuthData(token: String, userId: String, username: String) {
        sharedPreferences.edit().apply {
            putString(KEY_JWT_TOKEN, token)
            putString(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    fun getJwtToken(): String? {
        return sharedPreferences.getString(KEY_JWT_TOKEN, null)
    }
    
    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }
    
    fun getUserIdAsInt(): Int {
        val userIdString = sharedPreferences.getString(KEY_USER_ID, null)
        return try {
            userIdString?.toInt() ?: 1 // Default to 1 for testing
        } catch (e: NumberFormatException) {
            1 // Default to 1 for testing
        }
    }
    
    fun getUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }
    
    fun isLoggedIn(): Boolean {
        val isLoggedInFlag = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        val token = getJwtToken()
        
        if (!isLoggedInFlag || token == null) {
            return false
        }
        
        // Check if token is expired
        if (JwtDecoder.isTokenExpired(token)) {
            // Token expired, clear auth data
            logout()
            return false
        }
        
        return true
    }
    
    fun logout() {
        sharedPreferences.edit().clear().apply()
    }
}
