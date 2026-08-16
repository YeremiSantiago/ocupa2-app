package com.yeremi.ocupa2app.network.models

import com.google.gson.annotations.SerializedName

// ─────────────────────────────────────────────────────────────────────────────
// DETALLE DE OFERTA + APLICAR
// ─────────────────────────────────────────────────────────────────────────────

data class OfferDetail(
    val id: String,
    val jobTypeKey: String? = null,
    val jobTypeName: String? = null,
    val contractType: String? = null,
    val description: String? = null,
    val address: String? = null,
    val location: OfferLocation? = null,
    val payment: OfferPayment? = null,
    val photo: String? = null,
    val deadline: String? = null,
    val status: String? = null,
    val applicantsCount: Int = 0,
    val likesCount: Int = 0,
    val likedByMe: Boolean = false,
    val appliedByMe: Boolean = false,

    @SerializedName(
        value = "questions",
        alternate = ["additionalQuestions"]
    )
    val questions: List<OfferQuestion> = emptyList()
)

data class OfferQuestion(
    val id: String? = null,

    @SerializedName(
        value = "label",
        alternate = [
            "question",
            "text",
            "title"
        ]
    )
    val label: String = "",

    val type: String = "text",

    val required: Boolean = false,

    val options: List<String>? = null
)

data class ApplyAnswer(
    val questionId: String,
    val value: String
)

data class ApplyOfferRequest(
    val comment: String,
    val answers: List<ApplyAnswer> = emptyList()
)

data class ApplyOfferResponse(
    val id: String? = null,
    val status: String? = null
)

// ─────────────────────────────────────────────────────────────────────────────
// MIS APLICACIONES
// ─────────────────────────────────────────────────────────────────────────────

data class MyApplicationDetail(
    val id: String,
    val offerId: String? = null,
    val status: String = "applied",
    val appliedAt: String? = null,
    val comment: String? = null,
    val offer: ApplicationOfferSummary? = null
)

data class ApplicationOfferSummary(
    val id: String? = null,
    val jobTypeKey: String? = null,
    val jobTypeName: String? = null,
    val description: String? = null,
    val address: String? = null,
    val photo: String? = null,
    val contractType: String? = null,
    val payment: OfferPayment? = null
)

// ─────────────────────────────────────────────────────────────────────────────
// EXPERIENCIAS
// ─────────────────────────────────────────────────────────────────────────────

data class Experience(
    val id: String,

    val title: String? = null,

    val description: String? = null,

    @SerializedName(
        value = "certificate",
        alternate = [
            "certificateUrl",
            "certificateImage",
            "certificate_url"
        ]
    )
    val certificate: String? = null,

    val createdAt: String? = null
)

data class CreateExperienceRequest(
    val title: String,
    val description: String,
    val certificate: String? = null
)

// ─────────────────────────────────────────────────────────────────────────────
// VIDEOS
//
// Swagger confirmado:
// GET /apix/videos
//
// Cada video devuelve:
// youtubeId
// url
// title
// description
// ─────────────────────────────────────────────────────────────────────────────

data class VideoItem(
    val youtubeId: String,
    val url: String,
    val title: String,
    val description: String? = null
)