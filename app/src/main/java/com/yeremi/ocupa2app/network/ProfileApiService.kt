package com.yeremi.ocupa2app.network

import com.yeremi.ocupa2app.network.models.*
import retrofit2.Response
import retrofit2.http.*

interface ProfileApiService {

    @GET("me")
    suspend fun getMe(): Response<AuthResponse<UserData>>

    @PUT("me/profile")
    suspend fun updateProfile(@Body request: ProfileUpdateRequest): Response<AuthResponse<UserData>>

    @GET("job-types")
    suspend fun getJobTypes(): Response<AuthResponse<List<JobType>>>

    @GET("offers")
    suspend fun getOffers(
        @Query("search") search: String? = null,
        @Query("jobTypeId") jobTypeId: Int? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<AuthResponse<OfferResponse>>

    @POST("offers/{id}/like")
    suspend fun likeOffer(@Path("id") id: Int): Response<AuthResponse<Unit>>

    @DELETE("offers/{id}/like")
    suspend fun unlikeOffer(@Path("id") id: Int): Response<AuthResponse<Unit>>

    @PUT("me/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<AuthResponse<Unit>>
}
