package com.yeremi.ocupa2app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeremi.ocupa2app.data.repository.AuthRepository
import com.yeremi.ocupa2app.network.models.AuthResponse
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthState>(AuthState.Idle)
    val uiState: StateFlow<AuthState> = _uiState

    private val _events = MutableSharedFlow<AuthEvent>()
    val events = _events.asSharedFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            val result = repository.login(email, password)
            result.onSuccess {
                _uiState.value = AuthState.Success(it)
                _events.emit(AuthEvent.NavigateToHome)
            }.onFailure {
                _uiState.value = AuthState.Error(it.message ?: "Login failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _events.emit(AuthEvent.NavigateToLogin)
        }
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val data: AuthResponse) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    sealed class AuthEvent {
        object NavigateToHome : AuthEvent()
        object NavigateToLogin : AuthEvent()
    }
}
