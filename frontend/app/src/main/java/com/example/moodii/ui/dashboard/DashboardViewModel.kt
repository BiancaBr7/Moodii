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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DashboardState(
    val isLoading: Boolean = false,
    val currentMonth: LocalDate = LocalDate.now(),
    val moodLogsForMonth: List<MoodLog> = emptyList(),
    val error: String? = null,
    val selectedDate: LocalDate? = null
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

    fun navigateToMonth(month: LocalDate) {
        _state.value = _state.value.copy(currentMonth = month)
        loadMoodLogsForMonth(month)
    }

    fun selectDate(date: LocalDate) {
        _state.value = _state.value.copy(selectedDate = date)
    }

    private fun loadMoodLogsForMonth(month: LocalDate) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, error = null)
                
                // Get userId from shared preferences or auth state
                val userId = getUserId() // You'll need to implement this
                val monthString = month.format(DateTimeFormatter.ofPattern("yyyy-MM"))
                
                val response = moodLogService.getMoodLogsByMonth(userId, monthString)
                
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
        val previousMonth = _state.value.currentMonth.minusMonths(1)
        navigateToMonth(previousMonth)
    }

    fun navigateToNextMonth() {
        val nextMonth = _state.value.currentMonth.plusMonths(1)
        navigateToMonth(nextMonth)
    }

    // Get userId from AuthManager
    private fun getUserId(): String {
        return authManager.getUserId() ?: "unknown_user"
    }
}
