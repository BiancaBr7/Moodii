package com.example.moodii.utils

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonObject

object JwtDecoder {
    
    data class JwtPayload(
        val userId: String? = null,
        val username: String? = null,
        val exp: Long? = null
    )
    
    fun decodeToken(token: String): JwtPayload? {
        return try {
            // JWT format: header.payload.signature
            val parts = token.split(".")
            if (parts.size != 3) return null
            
            // Decode the payload (second part)
            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
            val payloadJson = String(decodedBytes)
            
            // Parse JSON
            val gson = Gson()
            val jsonObject = gson.fromJson(payloadJson, JsonObject::class.java)
            
            JwtPayload(
                userId = jsonObject.get("userId")?.asString,
                username = jsonObject.get("username")?.asString,
                exp = jsonObject.get("exp")?.asLong
            )
        } catch (e: Exception) {
            null
        }
    }
    
    fun isTokenExpired(token: String): Boolean {
        val payload = decodeToken(token) ?: return true
        val exp = payload.exp ?: return true
        return System.currentTimeMillis() / 1000 > exp
    }
}
