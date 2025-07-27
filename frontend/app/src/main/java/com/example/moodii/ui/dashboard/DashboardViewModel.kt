package com.example.moodii.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodii.api.moodlog.MoodLogClient
import com.example.moodii.data.moodlog.MoodLog
import com.example.moodii.utils.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class DashboardState(
    val isLoading: Boolean = false,
    val currentMonth: String = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date()),
    val moodLogsForMonth: List<MoodLog> = emptyList(),
    val error: String? = null,
    val selectedDate: String? = null
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private val moodLogService = MoodLogClient.moodLogService
    private val authManager = AuthManager(application)

    init {
        loadMoodLogsForCurrentMonth()
    }

    fun loadMoodLogsForCurrentMonth() {
        val currentMonth = _state.value.currentMonth
        loadMoodLogsForMonth(currentMonth)
    }

    fun navigateToMonth(month: String) {
        _state.value = _state.value.copy(currentMonth = month)
        loadMoodLogsForMonth(month)
    }

    fun selectDate(date: String) {
        _state.value = _state.value.copy(selectedDate = date)
    }

    private fun loadMoodLogsForMonth(month: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, error = null)
                
                // Get userId as Int from auth manager
                val userId = getUserId()
                
                val response = moodLogService.getMoodLogsByMonth(userId, month)
                
                if (response.isSuccessful) {
                    _state.value = _state.value.copy(
                        moodLogsForMonth = response.body() ?: emptyList(),
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

    fun navigateToPreviousMonth() {
        try {
            val currentMonth = _state.value.currentMonth
            val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.time = dateFormat.parse(currentMonth) ?: Date()
            calendar.add(Calendar.MONTH, -1)
            val previousMonth = dateFormat.format(calendar.time)
            navigateToMonth(previousMonth)
        } catch (e: Exception) {
            // If parsing fails, just stay on current month
        }
    }

    fun navigateToNextMonth() {
        try {
            val currentMonth = _state.value.currentMonth
            val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.time = dateFormat.parse(currentMonth) ?: Date()
            calendar.add(Calendar.MONTH, 1)
            val nextMonth = dateFormat.format(calendar.time)
            navigateToMonth(nextMonth)
        } catch (e: Exception) {
            // If parsing fails, just stay on current month
        }
    }

    // Get userId from AuthManager
    private fun getUserId(): Int {
        return authManager.getUserIdAsInt()
    }
    
    fun logout() {
        authManager.logout()
    }
}
