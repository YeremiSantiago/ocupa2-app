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

    suspend fun login(email: String, password: String): Result<AuthData> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.ok && body.data?.token != null) {
                    val fullToken = "${body.data.tokenType ?: "Bearer"} ${body.data.token}"
                    sessionManager.saveToken(fullToken)
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message ?: "Error en la respuesta del servidor"))
                }
            } else {
                Result.failure(Exception("Error de conexión. Verifica tu internet e inténtalo de nuevo."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión. Verifica tu internet e inténtalo de nuevo."))
        }
    }

    suspend fun register(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        referralMatricula: String
    ): Result<AuthData> {
        return try {
            val response = apiService.register(
                RegisterRequest(email, firstName, lastName, password, referralMatricula)
            )
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.ok && body.data?.token != null) {
                    val fullToken = "${body.data.tokenType ?: "Bearer"} ${body.data.token}"
                    sessionManager.saveToken(fullToken)
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message ?: "Error en el registro"))
                }
            } else {
                Result.failure(Exception("Error de conexión. Verifica tu internet e inténtalo de nuevo."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión. Verifica tu internet e inténtalo de nuevo."))
        }
    }

    suspend fun forgotPassword(email: String, referralMatricula: String): Result<String> {
        return try {
            val response = apiService.forgotPassword(ForgotPasswordRequest(email, referralMatricula))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.ok) {
                    Result.success(body.message ?: "Correo enviado")
                } else {
                    Result.failure(Exception(body.message ?: "Error al procesar solicitud"))
                }
            } else {
                Result.failure(Exception("Error de conexión. Verifica tu internet e inténtalo de nuevo."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión. Verifica tu internet e inténtalo de nuevo."))
        }
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }
}
