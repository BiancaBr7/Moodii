package com.example.moodii.utils

import android.content.Context
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit { putString("JWT_TOKEN", token) }
    }
    fun getToken(): String? {
        return prefs.getString("JWT_TOKEN", null)
    }
    fun clearToken() {
        prefs.edit { remove("JWT_TOKEN") }
    }
}