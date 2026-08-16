package com.yeremi.ocupa2app.data.repository

import com.yeremi.ocupa2app.network.PublishApiService
import com.yeremi.ocupa2app.network.ZoibeApiService
import com.yeremi.ocupa2app.network.models.*
import com.yeremi.ocupa2app.network.parseError
import retrofit2.Response

class ZoibeRepository(
    private val apiService: ZoibeApiService,
    private val publishApiService: PublishApiService
) {

    // ─────────────────────────────────────────────
    // DETALLE DE OFERTA
    // ─────────────────────────────────────────────

    suspend fun getOfferDetail(
        offerId: String
    ): Result<OfferDetail> =
        request("Error al obtener el detalle de la oferta") {
            apiService.getOfferDetail(offerId)
        }

    // ─────────────────────────────────────────────
    // APLICAR
    // ─────────────────────────────────────────────

    suspend fun applyToOffer(
        offerId: String,
        comment: String,
        answers: Map<String, String>
    ): Result<ApplyOfferResponse> {

        val requestBody = ApplyOfferRequest(
            comment = comment.trim(),
            answers = answers.map { (questionId, value) ->
                ApplyAnswer(
                    questionId = questionId,
                    value = value
                )
            }
        )

        return request("No se pudo enviar la aplicación") {
            apiService.applyToOffer(
                offerId = offerId,
                request = requestBody
            )
        }
    }

    // ─────────────────────────────────────────────
    // MIS APLICACIONES
    // ─────────────────────────────────────────────

    suspend fun getMyApplications():
            Result<List<MyApplicationDetail>> =
        request("Error al cargar tus aplicaciones") {
            apiService.getMyApplications()
        }

    // ─────────────────────────────────────────────
    // EXPERIENCIAS
    // ─────────────────────────────────────────────

    suspend fun getExperiences():
            Result<List<Experience>> =
        request("Error al cargar tus experiencias") {
            apiService.getMyExperiences()
        }

    suspend fun createExperience(
        title: String,
        description: String,
        certificateBase64: String?
    ): Result<Experience> {

        var certificateUrl: String? = null

        if (!certificateBase64.isNullOrBlank()) {

            val upload =
                request<UploadImageResponse>(
                    "No se pudo subir el certificado"
                ) {
                    publishApiService.uploadImage(
                        UploadImageRequest(
                            image = certificateBase64
                        )
                    )
                }

            upload.onFailure {
                return Result.failure(it)
            }

            certificateUrl =
                upload.getOrNull()?.url
        }

        return request(
            "No se pudo guardar la experiencia"
        ) {
            apiService.createExperience(
                CreateExperienceRequest(
                    title = title.trim(),
                    description = description.trim(),
                    certificate = certificateUrl
                )
            )
        }
    }

    // ─────────────────────────────────────────────
    // PAGOS
    // ─────────────────────────────────────────────

    suspend fun getPayments():
            Result<List<Payment>> =
        request("Error al cargar tus pagos") {
            publishApiService.getMyPayments()
        }

    // ─────────────────────────────────────────────
    // VIDEOS
    // ─────────────────────────────────────────────

    suspend fun getVideos():
            Result<List<VideoItem>> =
        request("Error al cargar los videos") {
            apiService.getVideos()
        }

    // ─────────────────────────────────────────────
    // MANEJO GENERAL DE RESPUESTAS
    // ─────────────────────────────────────────────

    private suspend fun <T> request(
        fallbackMessage: String,
        call: suspend () -> Response<AuthResponse<T>>
    ): Result<T> {

        return try {

            val response = call()

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                val body = response.body()!!

                if (
                    body.ok &&
                    body.data != null
                ) {
                    Result.success(body.data)
                } else {
                    Result.failure(
                        Exception(
                            body.message
                                ?: fallbackMessage
                        )
                    )
                }

            } else {

                Result.failure(
                    Exception(
                        response.parseError()
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?.takeIf { it.isNotBlank() }
                        ?: "Fallo en la red. Verifica tu conexión a internet."
                )
            )
        }
    }
}