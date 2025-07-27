package com.example.moodii.api.auth

import android.content.Context
import android.content.SharedPreferences
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthorizedClient {
    private var context: Context? = null
    
    fun initialize(appContext: Context) {
        context = appContext
    }
    
    private fun getSharedPreferences(): SharedPreferences? {
        return context?.getSharedPreferences("moodii_auth", Context.MODE_PRIVATE)
    }
    
    private fun getStoredToken(): String? {
        return getSharedPreferences()?.getString("jwt_token", null)
    }
    
    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = getStoredToken()
                val request = if (token != null) {
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else {
                    chain.request()
                }
                chain.proceed(request)
            }.build()
    }
    
    fun create(token: String): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }.build()

        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}