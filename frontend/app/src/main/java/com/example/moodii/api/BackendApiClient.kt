package com.example.moodii.api

import com.example.moodii.BuildConfig
import com.example.moodii.api.auth.AuthorizedClient
import com.example.moodii.api.ml.MLClient
import com.example.moodii.api.moodlog.MoodLogService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Central Retrofit provider. Uses BuildConfig.API_BASE_URL (set per flavor) and a single OkHttp client
 * with JWT interceptor from AuthorizedClient. ML service now proxied via backend (/api/ml/predict) so
 * we only expose the mood log service here; MLClient already builds its own /api/ml base on demand.
 */
object BackendApiClient {
    private val baseApi: String by lazy { BuildConfig.API_BASE_URL.trimEnd('/') + "/api/" }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseApi)
            .client(AuthorizedClient.okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val moodLogService: MoodLogService by lazy { retrofit.create(MoodLogService::class.java) }

    /** Force refresh base URL (e.g., after remote config) */
    fun refresh(newBase: String) {
        MLClient.updateBaseUrl(newBase)
    }
}
