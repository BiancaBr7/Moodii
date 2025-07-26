package com.example.moodii.ui.day

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodii.api.moodlog.MoodLogClient
import com.example.moodii.data.moodlog.MoodLog
import com.example.moodii.data.moodlog.UpdateMoodRequest
import com.example.moodii.utils.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DayViewState(
    val isLoading: Boolean = false,
    val selectedDate: LocalDate = LocalDate.now(),
    val moodLogsForDay: List<MoodLog> = emptyList(),
    val error: String? = null,
    val isDeleting: Boolean = false,
    val isUpdatingMood: Boolean = false
)

class DayViewViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(DayViewState())
    val state: StateFlow<DayViewState> = _state.asStateFlow()

    private val moodLogService = MoodLogClient.moodLogService
    private val authManager = AuthManager(application)

    fun loadMoodLogsForDate(date: LocalDate) {
        _state.value = _state.value.copy(selectedDate = date)
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, error = null)
                
                val userId = getUserId()
                val dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                
                val response = moodLogService.getMoodLogsByDate(userId, dateString)
                
                if (response.isSuccessful) {
                    val sortedMoodLogs = response.body()?.sortedBy { it.createdAt } ?: emptyList()
                    _state.value = _state.value.copy(
                        moodLogsForDay = sortedMoodLogs,
                        isLoading = false
                    )
                } else {
                    _state.value = _state.value.copy(
                        error = "Failed to load mood logs: ${response.code()}",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Network error: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun updateMoodLogMood(moodLogId: String, newMoodType: Int) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isUpdatingMood = true, error = null)
                
                val request = UpdateMoodRequest(newMoodType)
                val response = moodLogService.updateMoodLogMood(moodLogId, request)
                
                if (response.isSuccessful) {
                    // Update the local list with the updated mood log
                    val updatedMoodLog = response.body()
                    if (updatedMoodLog != null) {
                        val updatedList = _state.value.moodLogsForDay.map { moodLog ->
                            if (moodLog.id == moodLogId) updatedMoodLog else moodLog
                        }
                        _state.value = _state.value.copy(
                            moodLogsForDay = updatedList,
                            isUpdatingMood = false
                        )
                    }
                } else {
                    _state.value = _state.value.copy(
                        error = "Failed to update mood: ${response.code()}",
                        isUpdatingMood = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Network error: ${e.message}",
                    isUpdatingMood = false
                )
            }
        }
    }

    fun deleteMoodLog(moodLogId: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isDeleting = true, error = null)
                
                val response = moodLogService.deleteMoodLog(moodLogId)
                
                if (response.isSuccessful) {
                    // Remove the deleted mood log from the local list
                    val updatedList = _state.value.moodLogsForDay.filter { it.id != moodLogId }
                    _state.value = _state.value.copy(
                        moodLogsForDay = updatedList,
                        isDeleting = false
                    )
                } else {
                    _state.value = _state.value.copy(
                        error = "Failed to delete mood log: ${response.code()}",
                        isDeleting = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Network error: ${e.message}",
                    isDeleting = false
                )
            }
        }
    }

    // Get userId from AuthManager
    private fun getUserId(): String {
        return authManager.getUserId() ?: "unknown_user"
    }
}
