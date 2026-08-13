package com.yeremi.ocupa2app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeremi.ocupa2app.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChangePasswordViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _currentPassword = MutableStateFlow("")
    val currentPassword: StateFlow<String> = _currentPassword

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    // Computed properties
    val requirementMinLength: Boolean get() = _newPassword.value.length >= 8
    val requirementUppercase: Boolean get() = _newPassword.value.any { it.isUpperCase() }
    val requirementNumber: Boolean get() = _newPassword.value.any { it.isDigit() }
    
    val isFormValid: Boolean get() = _currentPassword.value.isNotBlank() &&
            requirementMinLength && requirementUppercase && requirementNumber &&
            _newPassword.value == _confirmPassword.value

    enum class PasswordStrength { WEAK, MEDIUM, STRONG }

    val passwordStrength: PasswordStrength
        get() {
            val pass = _newPassword.value
            return when {
                requirementMinLength && requirementUppercase && requirementNumber -> PasswordStrength.STRONG
                pass.length >= 6 && pass.any { it.isDigit() } -> PasswordStrength.MEDIUM
                else -> PasswordStrength.WEAK
            }
        }

    fun onCurrentPasswordChanged(value: String) { 
        _currentPassword.value = value 
        _errorMessage.value = null
    }
    
    fun onNewPasswordChanged(value: String) { 
        _newPassword.value = value 
        _errorMessage.value = null
    }
    
    fun onConfirmPasswordChanged(value: String) { 
        _confirmPassword.value = value 
        _errorMessage.value = null
    }

    fun changePassword() {
        if (!isFormValid) return
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.changePassword(_currentPassword.value, _newPassword.value)
                .onSuccess {
                    _isSuccess.value = true
                }
                .onFailure {
                    _errorMessage.value = it.message
                }
            
            _isLoading.value = false
        }
    }
}
