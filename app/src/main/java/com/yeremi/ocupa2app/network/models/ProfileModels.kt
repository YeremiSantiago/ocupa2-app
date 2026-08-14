package com.yeremi.ocupa2app.network.models

data class ProfileUpdateRequest(
    val cedula: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val birthDate: String
)

data class JobType(
    val id: String,
    val key: String,
    val name: String
)

// ── Modelos de Oferta (según JSON real de la API) ──────────────────────

data class OfferLocation(
    val lat: Double?,
    val lng: Double?
)

data class OfferPayment(
    val amount: Double?,
    val currency: String?,
    val period: String?
)

data class Offer(
    val id: String,                          // String en la API
    val jobTypeKey: String?,
    val jobTypeName: String?,                // Título visible de la oferta
    val contractType: String?,               // "fijo", "temporal", "por horas"
    val description: String?,
    val address: String?,
    val location: OfferLocation?,            // { lat, lng }
    val payment: OfferPayment?,              // { amount, currency, period }
    val photo: String?,
    val deadline: String?,
    val status: String = "published",
    val applicantsCount: Int = 0,
    val likesCount: Int = 0,
    val likedByMe: Boolean = false,          // isLiked en la UI
    val isIdentityRevealed: Boolean = false
)

// La API devuelve data: List<Offer> directamente (sin wrapper de paginación)
// Usamos una clase auxiliar para manejar la paginación localmente
data class OfferPageResult(
    val offers: List<Offer>,
    val hasMore: Boolean
)

data class JobTypeShort(
    val id: Int,
    val name: String
)

data class ChangePasswordRequest(
    val password: String
)
