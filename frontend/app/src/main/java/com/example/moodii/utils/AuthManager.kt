package com.example.moodii.utils

import android.content.Context
import android.content.SharedPreferences

class AuthManager(context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("moodii_auth", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
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
    
    fun getUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }
    
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false) && 
               getJwtToken() != null
    }
    
    fun logout() {
        sharedPreferences.edit().clear().apply()
    }
}
