package com.example.moodii.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object NetworkDebugUtil {
    
    suspend fun testConnection(baseUrl: String = "http://10.0.2.2:8080"): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("$baseUrl/api/moodlogs/calendar?month=2024-07&userId=1")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                
                val responseCode = connection.responseCode
                Log.d("NetworkDebug", "Connection test to $baseUrl: Response code = $responseCode")
                
                connection.disconnect()
                responseCode in 200..299 || responseCode == 404 // 404 is fine, means server is running
            } catch (e: IOException) {
                Log.e("NetworkDebug", "Connection test failed: ${e.message}")
                false
            }
        }
    }
    
    suspend fun testMultipleUrls(): String? {
        val urls = listOf(
            "http://10.0.2.2:8080",
            "http://localhost:8080", 
            "http://127.0.0.1:8080"
        )
        
        for (url in urls) {
            if (testConnection(url)) {
                Log.d("NetworkDebug", "Successfully connected to: $url")
                return url
            }
        }
        
        Log.e("NetworkDebug", "Could not connect to any backend URL")
        return null
    }
}
