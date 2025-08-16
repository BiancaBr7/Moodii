package com.example.moodii.api.moodlog

import com.example.moodii.api.BackendApiClient

@Deprecated("Use BackendApiClient.moodLogService instead")
object MoodLogClient {
    val moodLogService: MoodLogService by lazy { BackendApiClient.moodLogService }
}
