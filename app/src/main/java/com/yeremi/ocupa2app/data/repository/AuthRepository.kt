package com.yeremi.ocupa2app.data.repository

import com.yeremi.ocupa2app.data.local.SessionManager
import com.yeremi.ocupa2app.network.AuthApiService
import com.yeremi.ocupa2app.network.models.*
import kotlinx.coroutines.flow.Flow

class AuthRepository(
    private val apiService: AuthApiService,
    private val sessionManager: SessionManager
) {

    val token: Flow<String?> = sessionManager.tokenFlow

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                if (authResponse.ok && authResponse.data?.token != null) {
                    sessionManager.saveToken(authResponse.data.token)
                    Result.success(authResponse)
                } else {
                    Result.failure(Exception("Error en la respuesta del servidor"))
                }
            } else {
                Result.failure(Exception("Error de red: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        referralMatricula: String
    ): Result<AuthResponse> {
        return try {
            val response = apiService.register(
                RegisterRequest(email, firstName, lastName, password, referralMatricula)
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error de red: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPassword(email: String, referralMatricula: String): Result<AuthResponse> {
        return try {
            val response = apiService.forgotPassword(ForgotPasswordRequest(email, referralMatricula))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error de red: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }
}
