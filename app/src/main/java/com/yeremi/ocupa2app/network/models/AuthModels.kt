package com.yeremi.ocupa2app.network.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val referralMatricula: String
)

data class ForgotPasswordRequest(
    val email: String,
    val referralMatricula: String
)

data class AuthResponse<T>(
    val ok: Boolean,
    val data: T?,
    val message: String?
)

data class AuthData(
    val token: String?,
    val tokenType: String?,
    val user: UserData?
)

data class UserData(
    val id: String?,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val nombre: String? = null,
    val cedula: String? = null,
    val gender: String? = null,
    val birthDate: String? = null,
    val profileCompleted: Boolean = false,
    val referralMatricula: String? = null
)

data class ForgotPasswordResponse(
    val message: String?
)
