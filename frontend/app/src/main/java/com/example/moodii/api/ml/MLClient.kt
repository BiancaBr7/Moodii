package com.example.moodii.api.ml

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object MLClient {
    private const val ML_BASE_URL = "http://10.0.2.2:5000/" // Android emulator localhost
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
    private val retrofit = Retrofit.Builder()
        .baseUrl(ML_BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    // Create service instance
    val service: MLService = retrofit.create(MLService::class.java)
    
    fun updateBaseUrl(newUrl: String): MLService {
        Log.i(TAG, "Updating ML API base URL to: $newUrl")
        
        val newRetrofit = Retrofit.Builder()
            .baseUrl(newUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            
        return newRetrofit.create(MLService::class.java)
    }
}
