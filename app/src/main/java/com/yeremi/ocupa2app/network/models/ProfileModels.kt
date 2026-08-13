package com.yeremi.ocupa2app.network.models

data class ProfileUpdateRequest(
    val cedula: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val birthDate: String
)

data class JobType(
    val id: Int,
    val name: String,
    val icon: String?
)

data class OfferResponse(
    val offers: List<Offer>,
    val total: Int,
    val page: Int,
    val totalPages: Int
)

data class Offer(
    val id: Int,
    val title: String,
    val description: String,
    val jobType: JobTypeShort,
    val contractType: String,
    val salary: Double,
    val salaryPeriod: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val deadline: String,
    val applicantsCount: Int,
    val isLiked: Boolean,
    val isUrgent: Boolean,
    val isVerified: Boolean,
    val status: String
)

data class JobTypeShort(
    val id: Int,
    val name: String
)

data class ChangePasswordRequest(
    val password: String
)
