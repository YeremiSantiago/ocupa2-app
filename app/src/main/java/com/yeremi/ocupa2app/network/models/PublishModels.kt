package com.yeremi.ocupa2app.network.models

// ── Preguntas adicionales dinámicas ─────────────────────────────
data class PublishQuestion(
    val label: String,
    val type: String,                    // "text" | "date" | "select" | "check"
    val required: Boolean = false,
    val options: List<String>? = null    // solo si type == "select"
)

// ── Campo dinámico según tipo de empleo ──────────────────────────
// TODO: confirmar en Swagger si GET /job-types ya trae "fields" anidado o hace falta otro endpoint
data class JobTypeField(
    val key: String,
    val label: String,
    val type: String,
    val options: List<String>? = null,
    val required: Boolean = false
)

data class JobTypeDetail(
    val id: String? = null,
    val key: String,                     // usado para crear la oferta (jobTypeKey)
    val name: String,
    val fields: List<JobTypeField> = emptyList()
)

// ── Subir imagen (POST /uploads, base64) ─────────────────────────
data class UploadImageRequest(
    val image: String   // base64 del archivo. TODO: confirmar el nombre exacto del campo en Swagger (puede ser "file" o "base64")
)

// TODO: confirmar forma exacta de la respuesta — asumo que devuelve una URL usable en "photo"
data class UploadImageResponse(
    val url: String
)

// ── Crear oferta (POST /offers) ───────────────────────────────────
data class OfferLocationBody(val lat: Double, val lng: Double)
data class OfferPaymentBody(val amount: Double, val currency: String = "DOP")

data class CreateOfferRequest(
    val jobTypeKey: String,
    val contractType: String,            // "temporal" | "fijo" | "horas"
    val description: String,
    val address: String,
    val photo: String,                   // URL devuelta por /uploads
    val paymentId: String,               // id devuelto por POST /payments
    val location: OfferLocationBody,
    val payment: OfferPaymentBody,
    val deadline: String,                // "yyyy-MM-dd"
    val customAnswers: Map<String, String> = emptyMap(),
    val questions: List<PublishQuestion> = emptyList()
)

data class CreateOfferResponse(
    val id: String,
    val status: String? = null
)

// ── Pago (POST /payments) ─────────────────────────────────────────
data class ChargeCardRequest(
    val cardNumber: String,
    val cvv: String,
    val expMonth: Int,
    val expYear: Int,
    val cardholder: String
)
// Tarjeta de prueba aprobada: 4242424242424242 · rechazada: 4000000000000002

data class Payment(
    val id: String,
    val amount: Double? = null,
    val currency: String? = null,
    val status: String? = null,
    val createdAt: String? = null
)