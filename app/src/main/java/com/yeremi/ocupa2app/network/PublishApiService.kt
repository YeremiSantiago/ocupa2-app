package com.yeremi.ocupa2app.network

import com.yeremi.ocupa2app.network.models.*
import retrofit2.Response
import retrofit2.http.*

interface PublishApiService {

    @GET("job-types")
    suspend fun getJobTypesDetailed(): Response<AuthResponse<List<JobTypeDetail>>>

    // Sube la foto obligatoria como base64, ANTES de crear la oferta
    @POST("uploads")
    suspend fun uploadImage(@Body request: UploadImageRequest): Response<AuthResponse<UploadImageResponse>>

    // Cobro de 1 USD — se hace ANTES de crear la oferta. Devuelve un paymentId.
    @POST("com/yeremi/ocupa2app/ui/payments")
    suspend fun chargeCard(@Body request: ChargeCardRequest): Response<AuthResponse<Payment>>

    @GET("me/payments")
    suspend fun getMyPayments(): Response<AuthResponse<List<Payment>>>

    // Crea la oferta — ya con photo (url) y paymentId incluidos en el body
    @POST("offers")
    suspend fun createOffer(@Body request: CreateOfferRequest): Response<AuthResponse<CreateOfferResponse>>
}