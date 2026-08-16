package com.yeremi.ocupa2app.network

import com.yeremi.ocupa2app.network.models.*
import retrofit2.Response
import retrofit2.http.*

interface MyOffersApiService {

    @GET("me/offers")
    suspend fun getMyOffers(): Response<AuthResponse<List<MyOffer>>>

    @GET("offers/{id}/applications")
    suspend fun getApplicants(@Path("id") offerId: String): Response<AuthResponse<List<Applicant>>>

    @POST("offers/{id}/deactivate")
    suspend fun deactivateOffer(@Path("id") offerId: String): Response<AuthResponse<Unit>>

    // Una sola acción para calificar / descartar / finalista / ganador
    @PATCH("applications/{id}")
    suspend fun updateApplication(
        @Path("id") applicationId: String,
        @Body request: ApplicationActionRequest
    ): Response<AuthResponse<Unit>>
}