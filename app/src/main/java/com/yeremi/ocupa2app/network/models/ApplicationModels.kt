package com.yeremi.ocupa2app.network.models

// ── Oferta propia (GET /me/offers) ──────────────────────────────
// TODO: confirmar campos exactos ejecutando "Try it out" en /me/offers con datos reales.
// Dejé los que ya sabemos que existen por el resto de la API (mismos que en /offers).
data class MyOffer(
    val id: String,
    val jobTypeKey: String? = null,
    val description: String? = null,
    val address: String? = null,
    val photo: String? = null,
    val contractType: String? = null,
    val deadline: String? = null,
    val active: Boolean = true,          // se pone en false tras /deactivate
    val payment: OfferPaymentBody? = null
)

// ── Aplicante de UNA de mis ofertas (GET /offers/{id}/applications) ──
// TODO: confirmar campos exactos — no vino JSON de ejemplo. Ajusté nombres
// razonables; si el campo real se llama distinto, solo cambia aquí.
data class Applicant(
    val id: String,                      // este es el "applicationId" que usa PATCH /applications/{id}
    val applicantName: String? = null,
    val applicantEmail: String? = null,
    val comment: String? = null,
    val status: String = "applied",      // "applied" | "discarded" | "finalist" | "winner"
    val rating: Int? = null,
    val appliedAt: String? = null,
    val answers: List<AdditionalAnswer> = emptyList()
)

data class AdditionalAnswer(
    val questionId: String,
    val value: String
)

// ── PATCH /applications/{id} ─────────────────────────────────────
// Todos los campos son opcionales: manda solo lo que necesites cambiar.
data class ApplicationActionRequest(
    val rating: Int? = null,
    val status: String? = null,          // "applied" | "discarded" | "finalist" | "winner"
    val salary: Double? = null,          // opcional, solo aplica si status = "winner"
    val currency: String? = null,        // ej "DOP"
    val startDate: String? = null,       // "yyyy-MM-dd", opcional si status = "winner"
    val duration: String? = null         // ej "3 meses", opcional si status = "winner"
)

// Mis aplicaciones (GET /me/applications) — no es tu módulo, se deja aquí
// por si otro compañero la reutiliza.
data class MyApplication(
    val id: String,
    val offerId: String? = null,
    val status: String,
    val appliedAt: String? = null
)