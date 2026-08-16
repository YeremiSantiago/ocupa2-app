package com.yeremi.ocupa2app.network

import com.yeremi.ocupa2app.network.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ZoibeApiService {

    // ─────────────────────────────────────────────
    // DETALLE DE OFERTA
    // ─────────────────────────────────────────────

    @GET("offers/{id}")
    suspend fun getOfferDetail(
        @Path("id") offerId: String
    ): Response<AuthResponse<OfferDetail>>

    // ─────────────────────────────────────────────
    // APLICAR A UNA OFERTA
    // ─────────────────────────────────────────────

    @POST("offers/{id}/apply")
    suspend fun applyToOffer(
        @Path("id") offerId: String,
        @Body request: ApplyOfferRequest
    ): Response<AuthResponse<ApplyOfferResponse>>

    // ─────────────────────────────────────────────
    // MIS APLICACIONES
    // ─────────────────────────────────────────────

    @GET("me/applications")
    suspend fun getMyApplications():
            Response<AuthResponse<List<MyApplicationDetail>>>

    // ─────────────────────────────────────────────
    // EXPERIENCIAS
    // ─────────────────────────────────────────────

    @GET("me/experiences")
    suspend fun getMyExperiences():
            Response<AuthResponse<List<Experience>>>

    @POST("me/experiences")
    suspend fun createExperience(
        @Body request: CreateExperienceRequest
    ): Response<AuthResponse<Experience>>

    // ─────────────────────────────────────────────
    // VIDEOS
    // Swagger confirmado:
    // GET https://ocupa2.ia3x.com/apix/videos
    // ─────────────────────────────────────────────

    @GET("videos")
    suspend fun getVideos():
            Response<AuthResponse<List<VideoItem>>>
}