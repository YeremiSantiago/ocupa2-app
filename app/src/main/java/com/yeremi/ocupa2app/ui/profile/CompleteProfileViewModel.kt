package com.yeremi.ocupa2app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeremi.ocupa2app.data.repository.ProfileRepository
import com.yeremi.ocupa2app.network.models.ProfileUpdateRequest
import com.yeremi.ocupa2app.network.models.UserData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class CompleteProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val uiState: StateFlow<ProfileState> = _uiState

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events = _events.asSharedFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileState.Loading
            repository.getMe().onSuccess { user ->
                _uiState.value = ProfileState.ProfileLoaded(user)
            }.onFailure {
                _uiState.value = ProfileState.Error(it.message ?: "Error al cargar perfil")
            }
        }
    }

    fun updateProfile(cedula: String, firstName: String, lastName: String, gender: String, birthDate: String) {
        // --- Validaciones locales antes de llamar al API ---
        val cedulaLimpia = cedula.replace("-", "").replace(" ", "")
        val errorLocal = when {
            firstName.isBlank() ->
                "El nombre es obligatorio."
            firstName.trim().length < 2 ->
                "El nombre debe tener al menos 2 caracteres."
            lastName.isBlank() ->
                "El apellido es obligatorio."
            lastName.trim().length < 2 ->
                "El apellido debe tener al menos 2 caracteres."
            cedulaLimpia.isBlank() ->
                "La cédula es obligatoria."
            cedulaLimpia.length != 11 || !cedulaLimpia.all { it.isDigit() } ->
                "La cédula debe tener exactamente 11 dígitos."
            gender.isBlank() ->
                "Debes seleccionar un género."
            birthDate.isBlank() ->
                "La fecha de nacimiento es obligatoria."
            else -> null
        }

        if (errorLocal != null) {
            _uiState.value = ProfileState.Error(errorLocal)
            return
        }

        // --- Si pasa todas las validaciones, llama al API ---
        viewModelScope.launch {
            _uiState.value = ProfileState.Loading
            val request = ProfileUpdateRequest(cedulaLimpia, firstName.trim(), lastName.trim(), gender, birthDate)
            repository.updateProfile(request).onSuccess {
                _events.emit(ProfileEvent.NavigateToHome)
            }.onFailure {
                _uiState.value = ProfileState.Error(it.message ?: "Error al actualizar perfil")
            }
        }
    }

    sealed class ProfileState {
        object Idle : ProfileState()
        object Loading : ProfileState()
        data class ProfileLoaded(val user: UserData) : ProfileState()
        data class Error(val message: String) : ProfileState()
    }

    sealed class ProfileEvent {
        object NavigateToHome : ProfileEvent()
    }
}
