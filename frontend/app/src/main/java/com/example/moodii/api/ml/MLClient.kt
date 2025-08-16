package com.example.moodii.api.ml

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object MLClient {
    // Base now points to backend proxy (/api/ml/) so mobile only needs backend URL (BuildConfig.API_BASE_URL)
    // Fallback to emulator base if BuildConfig not accessible yet.
    private var ML_BASE_URL: String = try {
        buildString {
            append(BuildConfig.API_BASE_URL.trimEnd('/'))
            append("/api/ml/")
        }
    } catch (e: Exception) { "http://10.0.2.2:8080/api/ml/" }
    private const val TAG = "MLClient"
    
    // Create logging interceptor for debugging
    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d(TAG, message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    // Configure HTTP client with extended timeouts for ML processing
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS) // ML inference can take time
        .writeTimeout(60, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
    
    // Create Retrofit instance
    @Volatile private var retrofit: Retrofit = newRetrofit(ML_BASE_URL)
    @Volatile var service: MLService = retrofit.create(MLService::class.java)

    private fun newRetrofit(base: String) = Retrofit.Builder()
        .baseUrl(base)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    fun updateBaseUrl(backendBase: String): MLService {
        val newUrl = backendBase.trimEnd('/') + "/api/ml/"
        if(newUrl == ML_BASE_URL) return service
        Log.i(TAG, "Updating ML API base URL to: $newUrl")
        ML_BASE_URL = newUrl
        retrofit = newRetrofit(ML_BASE_URL)
        service = retrofit.create(MLService::class.java)
        return service
    }
}
