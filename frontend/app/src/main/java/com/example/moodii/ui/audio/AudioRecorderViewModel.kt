package com.example.moodii.ui.audio

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodii.api.moodlog.MoodLogClient
import com.example.moodii.data.moodlog.MoodLog
import com.example.moodii.data.moodlog.MoodLogRequest
import com.example.moodii.utils.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

data class AudioRecorderState(
    val isRecording: Boolean = false,
    val isPaused: Boolean = false,
    val predictedMood: String? = null,
    val transcription: String = "",
    val isSaving: Boolean = false,
    val savedMoodLog: MoodLog? = null,
    val error: String? = null,
    val alertMessage: Pair<String, String>? = null // message, type
)

class AudioRecorderViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(AudioRecorderState())
    val state: StateFlow<AudioRecorderState> = _state.asStateFlow()

    private val moodLogService = MoodLogClient.moodLogService
    private val authManager = AuthManager(application)
    private var audioFile: File? = null

    fun startRecording() {
        _state.value = _state.value.copy(
            isRecording = true,
            isPaused = false,
            error = null
        )
        // TODO: Implement actual audio recording logic
        simulateMoodPrediction()
    }

    fun stopRecording() {
        _state.value = _state.value.copy(
            isRecording = false,
            isPaused = false
        )
        // TODO: Stop actual audio recording and save file
        simulateTranscription()
    }

    fun pauseRecording() {
        _state.value = _state.value.copy(isPaused = true)
        // TODO: Implement pause logic
    }

    fun resumeRecording() {
        _state.value = _state.value.copy(isPaused = false)
        // TODO: Implement resume logic
    }

    fun restartRecording() {
        _state.value = _state.value.copy(
            isRecording = false,
            isPaused = false,
            predictedMood = null,
            transcription = "",
            error = null
        )
        audioFile = null
        showAlert("Recording Restarted", "Info")
    }

    fun selectMood(mood: String) {
        _state.value = _state.value.copy(predictedMood = mood)
    }

    fun saveMoodLog() {
        val currentState = _state.value
        
        if (currentState.predictedMood == null) {
            showAlert("Please select a mood before saving", "Error")
            return
        }

        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isSaving = true, error = null)

                // Create mood log request
                val moodTypeInt = getMoodTypeInt(currentState.predictedMood!!)
                val request = MoodLogRequest(
                    title = "Audio Recording", // You can make this customizable
                    transcription = currentState.transcription,
                    moodType = moodTypeInt,
                    userId = getUserId()
                )

                // Create mood log
                val response = moodLogService.createMoodLog(request)

                if (response.isSuccessful) {
                    val moodLog = response.body()
                    if (moodLog != null) {
                        _state.value = _state.value.copy(savedMoodLog = moodLog)
                        
                        // Upload audio if available
                        audioFile?.let { file ->
                            uploadAudio(moodLog.id!!, file)
                        } ?: run {
                            _state.value = _state.value.copy(isSaving = false)
                            showAlert("Mood log saved successfully!", "Success")
                        }
                    }
                } else {
                    _state.value = _state.value.copy(
                        error = "Failed to save mood log: ${response.code()}",
                        isSaving = false
                    )
                    showAlert("Failed to save mood log", "Error")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Network error: ${e.message}",
                    isSaving = false
                )
                showAlert("Network error occurred", "Error")
            }
        }
    }

    private suspend fun uploadAudio(moodLogId: String, audioFile: File) {
        try {
            val requestFile = audioFile.asRequestBody("audio/webm".toMediaTypeOrNull())
            val audioPart = MultipartBody.Part.createFormData("audio", audioFile.name, requestFile)
            
            val response = moodLogService.uploadAudio(moodLogId, audioPart)
            
            if (response.isSuccessful) {
                _state.value = _state.value.copy(isSaving = false)
                showAlert("Audio saved successfully!", "Success")
            } else {
                _state.value = _state.value.copy(
                    error = "Failed to upload audio: ${response.code()}",
                    isSaving = false
                )
                showAlert("Failed to upload audio", "Error")
            }
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                error = "Audio upload error: ${e.message}",
                isSaving = false
            )
            showAlert("Audio upload failed", "Error")
        }
    }

    private fun simulateMoodPrediction() {
        // Simulate mood prediction - replace with actual ML integration
        val moods = listOf("happy", "sad", "mad", "surprised", "neutral")
        val randomMood = moods.random()
        _state.value = _state.value.copy(predictedMood = randomMood)
    }

    private fun simulateTranscription() {
        // Simulate transcription - replace with actual speech-to-text
        _state.value = _state.value.copy(
            transcription = "This is a simulated transcription of the audio recording."
        )
    }

    private fun getMoodTypeInt(mood: String): Int {
        return when (mood.lowercase()) {
            "happy" -> 1
            "sad" -> 2
            "mad" -> 3
            "surprised" -> 4
            "neutral" -> 5
            else -> 5 // Default to neutral
        }
    }

    private fun showAlert(message: String, type: String) {
        _state.value = _state.value.copy(alertMessage = Pair(message, type))
    }

    fun clearAlert() {
        _state.value = _state.value.copy(alertMessage = null)
    }

    // Get userId from AuthManager
    private fun getUserId(): String {
        return authManager.getUserId() ?: "unknown_user"
    }
}
