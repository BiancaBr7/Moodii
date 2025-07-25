package com.example.moodii.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodii.repository.auth.AuthRepository
import com.example.moodii.ui.auth.register.RegisterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val response = repository.register(email, password)
                if (response.isSuccessful) {
                    val message = response.body()?.get("message") ?: "Registered successfully"
                    _registerState.value = RegisterState.Success(message)
                } else {
                    _registerState.value = RegisterState.Error("Registration failed: ${response.code()}")
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(e.message ?: "Unexpected error")
            }
        }
    }

    fun clearError() {
        if (_registerState.value is RegisterState.Error) {
            _registerState.value = RegisterState.Idle
        }
    }
}