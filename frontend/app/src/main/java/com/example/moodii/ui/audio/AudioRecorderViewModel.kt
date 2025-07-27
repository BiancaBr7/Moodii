package com.example.moodii.ui.audio

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodii.api.moodlog.MoodLogClient
import com.example.moodii.data.moodlog.MoodLog
import com.example.moodii.data.moodlog.MoodLogRequest
import com.example.moodii.utils.AuthManager
import com.example.moodii.utils.AudioRecorder
import com.example.moodii.utils.SpeechTranscriber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class AudioRecorderState(
    val isRecording: Boolean = false,
    val isPaused: Boolean = false,
    val predictedMood: String? = null,
    val transcription: String = "",
    val isSaving: Boolean = false,
    val savedMoodLog: MoodLog? = null,
    val error: String? = null,
    val alertMessage: Pair<String, String>? = null, // message, type
    val needsPermission: Boolean = false,
    val isTranscribing: Boolean = false,
    val transcriptionError: String? = null
)

class AudioRecorderViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(AudioRecorderState())
    val state: StateFlow<AudioRecorderState> = _state.asStateFlow()

    private val moodLogService = MoodLogClient.moodLogService
    private val authManager = AuthManager(application)
    private val audioRecorder = AudioRecorder(application)
    private val speechTranscriber = SpeechTranscriber(application)
    private var audioFile: File? = null

    fun checkPermissions() {
        if (!audioRecorder.hasRecordPermission()) {
            _state.value = _state.value.copy(needsPermission = true)
        }
    }

    fun onPermissionResult(granted: Boolean) {
        _state.value = _state.value.copy(needsPermission = false)
        if (!granted) {
            showAlert("Microphone permission is required for recording", "Error")
        }
    }

    fun startRecording() {
        if (!audioRecorder.hasRecordPermission()) {
            _state.value = _state.value.copy(needsPermission = true)
            return
        }

        try {
            // Create output file
            val fileName = "recording_${System.currentTimeMillis()}.m4a"
            audioFile = File(getApplication<Application>().filesDir, fileName)
            
            val success = audioRecorder.startRecording(audioFile!!)
            if (success) {
                _state.value = _state.value.copy(
                    isRecording = true,
                    isPaused = false,
                    error = null,
                    transcription = "",
                    isTranscribing = true
                )
                Log.d("AudioRecorderViewModel", "Recording started successfully")
                
                // Start real-time transcription
                startTranscription()
                
                // Simulate mood prediction for now - replace with actual ML integration
                simulateMoodPrediction()
            } else {
                showAlert("Failed to start recording", "Error")
            }
        } catch (e: Exception) {
            Log.e("AudioRecorderViewModel", "Error starting recording", e)
            showAlert("Error starting recording: ${e.message}", "Error")
        }
    }

    fun stopRecording() {
        try {
            // Stop transcription first
            stopTranscription()
            
            val recordedFile = audioRecorder.stopRecording()
            _state.value = _state.value.copy(
                isRecording = false,
                isPaused = false,
                isTranscribing = false
            )
            
            if (recordedFile != null && recordedFile.exists()) {
                audioFile = recordedFile
                Log.d("AudioRecorderViewModel", "Recording stopped, file saved: ${recordedFile.absolutePath}")
                
                // If real-time transcription didn't capture much, try file transcription
                if (_state.value.transcription.isBlank() || _state.value.transcription.length < 10) {
                    Log.d("AudioRecorderViewModel", "Live transcription was empty or too short, attempting file analysis")
                    transcribeAudioFile(recordedFile)
                } else {
                    Log.d("AudioRecorderViewModel", "Using live transcription: ${_state.value.transcription}")
                }
            } else {
                showAlert("Recording failed to save", "Error")
            }
        } catch (e: Exception) {
            Log.e("AudioRecorderViewModel", "Error stopping recording", e)
            showAlert("Error stopping recording: ${e.message}", "Error")
        }
    }

    fun pauseRecording() {
        if (audioRecorder.isCurrentlyRecording()) {
            audioRecorder.pauseRecording()
            _state.value = _state.value.copy(isPaused = true)
        }
    }

    fun resumeRecording() {
        if (audioRecorder.isCurrentlyRecording()) {
            audioRecorder.resumeRecording()
            _state.value = _state.value.copy(isPaused = false)
        }
    }

    fun restartRecording() {
        // Stop current recording if active
        if (audioRecorder.isCurrentlyRecording()) {
            audioRecorder.stopRecording()
        }
        
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
                
                Log.d("AudioRecorder", "Starting mood log save process...")

                // Create mood log request
                val moodTypeInt = getMoodTypeInt(currentState.predictedMood!!)
                val userId = getUserId()
                
                Log.d("AudioRecorder", "Creating mood log with: userId=$userId, moodType=$moodTypeInt")
                
                val request = MoodLogRequest(
                    title = "Audio Recording - ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())}",
                    transcription = currentState.transcription,
                    moodType = moodTypeInt,
                    userId = userId
                )

                // Create mood log
                Log.d("AudioRecorder", "Sending request to backend...")
                val response = moodLogService.createMoodLog(request)

                if (response.isSuccessful) {
                    val moodLog = response.body()
                    Log.d("AudioRecorder", "Mood log created successfully: ${moodLog?.id}")
                    if (moodLog != null) {
                        _state.value = _state.value.copy(savedMoodLog = moodLog)
                        
                        // Upload audio if available
                        audioFile?.let { file ->
                            Log.d("AudioRecorder", "Uploading audio file...")
                            uploadAudio(moodLog.id!!, file)
                        } ?: run {
                            _state.value = _state.value.copy(isSaving = false)
                            showAlert("Mood log saved successfully!", "Success")
                            Log.d("AudioRecorder", "Mood log saved without audio")
                        }
                    }
                } else {
                    val errorMsg = "Failed to save mood log: ${response.code()} - ${response.message()}"
                    Log.e("AudioRecorder", errorMsg)
                    _state.value = _state.value.copy(
                        error = errorMsg,
                        isSaving = false
                    )
                    showAlert("Failed to save mood log", "Error")
                }
            } catch (e: Exception) {
                val errorMsg = "Network error: ${e.message}"
                Log.e("AudioRecorder", errorMsg, e)
                _state.value = _state.value.copy(
                    error = errorMsg,
                    isSaving = false
                )
                showAlert("Network error occurred", "Error")
            }
        }
    }

    private suspend fun uploadAudio(moodLogId: String, audioFile: File) {
        try {
            Log.d("AudioRecorder", "Preparing to upload audio file: ${audioFile.name}")
            Log.d("AudioRecorder", "File exists: ${audioFile.exists()}")
            Log.d("AudioRecorder", "File size: ${audioFile.length()} bytes")
            Log.d("AudioRecorder", "File path: ${audioFile.absolutePath}")
            
            val requestFile = audioFile.asRequestBody("audio/mp4".toMediaTypeOrNull())
            val audioPart = MultipartBody.Part.createFormData("audio", audioFile.name, requestFile)
            
            Log.d("AudioRecorder", "Uploading audio for mood log: $moodLogId")
            val response = moodLogService.uploadAudio(moodLogId, audioPart)
            
            Log.d("AudioRecorder", "Upload response code: ${response.code()}")
            Log.d("AudioRecorder", "Upload response message: ${response.message()}")
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("AudioRecorder", "Upload response body: $responseBody")
                _state.value = _state.value.copy(isSaving = false)
                showAlert("Audio saved successfully!", "Success")
                Log.d("AudioRecorder", "Audio uploaded successfully")
            } else {
                val errorMsg = "Failed to upload audio: ${response.code()} - ${response.message()}"
                Log.e("AudioRecorder", errorMsg)
                // Try to read error body
                try {
                    val errorBody = response.errorBody()?.string()
                    Log.e("AudioRecorder", "Upload error body: $errorBody")
                } catch (e: Exception) {
                    Log.e("AudioRecorder", "Could not read upload error body", e)
                }
                _state.value = _state.value.copy(
                    error = errorMsg,
                    isSaving = false
                )
                showAlert("Failed to upload audio", "Error")
            }
        } catch (e: Exception) {
            val errorMsg = "Audio upload error: ${e.message}"
            Log.e("AudioRecorder", errorMsg, e)
            _state.value = _state.value.copy(
                error = errorMsg,
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

    fun testSaveMoodLog() {
        // Test function to save a mood log without recording
        _state.value = _state.value.copy(
            predictedMood = "happy",
            transcription = "This is a test mood log entry created without audio recording.",
            isRecording = false
        )
        Log.d("AudioRecorder", "Test mood log created, calling saveMoodLog()")
        saveMoodLog()
    }

    fun clearAlert() {
        _state.value = _state.value.copy(alertMessage = null)
    }

    // Manual transcription for testing when microphone doesn't work
    fun setManualTranscription(text: String) {
        _state.value = _state.value.copy(
            transcription = text,
            transcriptionError = null
        )
        Log.d("AudioRecorderViewModel", "Manual transcription set: $text")
    }

    // Transcription methods
    private fun startTranscription() {
        if (!speechTranscriber.isAvailable()) {
            _state.value = _state.value.copy(
                transcriptionError = "Speech recognition not available",
                isTranscribing = false
            )
            return
        }

        speechTranscriber.startLiveTranscription(
            onResult = { transcription ->
                _state.value = _state.value.copy(
                    transcription = transcription,
                    transcriptionError = null
                )
                Log.d("AudioRecorderViewModel", "Transcription update: $transcription")
            },
            onError = { error ->
                _state.value = _state.value.copy(
                    transcriptionError = error,
                    isTranscribing = false
                )
                Log.e("AudioRecorderViewModel", "Transcription error: $error")
            }
        )
    }

    private fun stopTranscription() {
        speechTranscriber.stopLiveTranscription()
    }

    private fun transcribeAudioFile(audioFile: File) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isTranscribing = true)
                // Pass the current live transcription to avoid re-processing
                val currentTranscription = _state.value.transcription
                val transcription = speechTranscriber.transcribeAudioFile(audioFile, currentTranscription)
                _state.value = _state.value.copy(
                    transcription = transcription,
                    isTranscribing = false,
                    transcriptionError = null
                )
                Log.d("AudioRecorderViewModel", "File transcription: $transcription")
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    transcriptionError = "Failed to transcribe audio: ${e.message}",
                    isTranscribing = false
                )
                Log.e("AudioRecorderViewModel", "File transcription error", e)
            }
        }
    }

    // Get userId from AuthManager
    private fun getUserId(): Int {
        return authManager.getUserIdAsInt()
    }
}
