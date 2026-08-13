package com.yeremi.ocupa2app.data.repository

import com.yeremi.ocupa2app.network.ProfileApiService
import com.yeremi.ocupa2app.network.models.*
import com.yeremi.ocupa2app.network.parseError

class ProfileRepository(private val apiService: ProfileApiService) {

    suspend fun getMe(): Result<UserData> {
        return try {
            val response = apiService.getMe()
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.ok && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message ?: "Error al obtener perfil"))
                }
            } else {
                Result.failure(Exception(response.parseError()))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Fallo en la red. Verifica tu conexión a internet."))
        }
    }

    suspend fun updateProfile(request: ProfileUpdateRequest): Result<UserData> {
        return try {
            val response = apiService.updateProfile(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.ok && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message ?: "Error al actualizar perfil"))
                }
            } else {
                Result.failure(Exception(response.parseError()))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Fallo en la red. Verifica tu conexión a internet."))
        }
    }

    suspend fun getJobTypes(): Result<List<JobType>> {
        return try {
            val response = apiService.getJobTypes()
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.ok && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message ?: "Error al obtener tipos de empleo"))
                }
            } else {
                Result.failure(Exception(response.parseError()))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Fallo en la red. Verifica tu conexión a internet."))
        }
    }

    suspend fun getOffers(search: String?, jobTypeId: Int?, page: Int, limit: Int = 10): Result<OfferResponse> {
        return try {
            val response = apiService.getOffers(search, jobTypeId, page, limit)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.ok && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message ?: "Error al obtener ofertas"))
                }
            } else {
                Result.failure(Exception(response.parseError()))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Fallo en la red. Verifica tu conexión a internet."))
        }
    }

    suspend fun toggleLike(offerId: Int, isLiked: Boolean): Result<Unit> {
        return try {
            val response = if (isLiked) apiService.unlikeOffer(offerId) else apiService.likeOffer(offerId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.ok) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(body.message ?: "Error al procesar me gusta"))
                }
            } else {
                Result.failure(Exception(response.parseError()))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Fallo en la red. Verifica tu conexión a internet."))
        }
    }

    suspend fun changePassword(newPassword: String): Result<String> {
        return try {
            val response = apiService.changePassword(ChangePasswordRequest(newPassword))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.ok) {
                    Result.success(body.message ?: "Contraseña actualizada exitosamente")
                } else {
                    Result.failure(Exception(body.message ?: "Error al cambiar contraseña"))
                }
            } else {
                Result.failure(Exception(response.parseError()))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Fallo en la red. Verifica tu conexión a internet."))
        }
    }
}
