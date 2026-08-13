package com.yeremi.ocupa2app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeremi.ocupa2app.data.repository.AuthRepository
import com.yeremi.ocupa2app.network.models.AuthData
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
        // --- Validaciones locales ---
        val errorLocal = when {
            email.isBlank() ->
                "El correo electrónico es obligatorio."
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
                "Ingresa un correo electrónico válido."
            password.isBlank() ->
                "La contraseña es obligatoria."
            else -> null
        }
        if (errorLocal != null) {
            _uiState.value = AuthState.Error(errorLocal)
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            val result = repository.login(email.trim(), password)
            result.onSuccess { data ->
                _uiState.value = AuthState.Success(data)
                if (data.user?.profileCompleted == true) {
                    _events.emit(AuthEvent.NavigateToHome)
                } else {
                    _events.emit(AuthEvent.NavigateToCompleteProfile)
                }
            }.onFailure {
                _uiState.value = AuthState.Error(it.message ?: "Error al iniciar sesión")
            }
        }
    }

    fun register(email: String, firstName: String, lastName: String, password: String, referralMatricula: String) {
        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            val result = repository.register(email, firstName, lastName, password, referralMatricula)
            result.onSuccess { data ->
                _uiState.value = AuthState.Success(data)
                if (data.user?.profileCompleted == true) {
                    _events.emit(AuthEvent.NavigateToHome)
                } else {
                    _events.emit(AuthEvent.NavigateToCompleteProfile)
                }
            }.onFailure {
                _uiState.value = AuthState.Error(it.message ?: "Error al registrarse")
            }
        }
    }

    fun forgotPassword(email: String, referralMatricula: String) {
        // --- Validaciones locales ---
        val errorLocal = when {
            email.isBlank() ->
                "El correo electrónico es obligatorio."
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
                "Ingresa un correo electrónico válido."
            referralMatricula.isBlank() ->
                "La matrícula es obligatoria."
            referralMatricula.trim().length < 5 ->
                "Ingresa una matrícula válida."
            else -> null
        }
        if (errorLocal != null) {
            _uiState.value = AuthState.Error(errorLocal)
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            val result = repository.forgotPassword(email.trim(), referralMatricula.trim())
            result.onSuccess { message ->
                _uiState.value = AuthState.ForgotPasswordSuccess(message)
            }.onFailure {
                _uiState.value = AuthState.Error(it.message ?: "Error al enviar correo")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _events.emit(AuthEvent.NavigateToLogin)
        }
    }

    fun resetState() {
        _uiState.value = AuthState.Idle
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val data: AuthData) : AuthState()
        data class ForgotPasswordSuccess(val message: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    sealed class AuthEvent {
        object NavigateToHome : AuthEvent()
        object NavigateToCompleteProfile : AuthEvent()
        object NavigateToLogin : AuthEvent()
    }
}
