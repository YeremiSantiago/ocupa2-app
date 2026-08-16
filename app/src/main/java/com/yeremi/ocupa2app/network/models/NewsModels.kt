package com.yeremi.ocupa2app.network.models

// La API de /news NO trae "id" ni endpoint de detalle — solo lista.
data class NewsItem(
    val title: String,
    val image: String? = null,
    val summary: String? = null,
    val date: String? = null,
    val url: String? = null,
    val source: String? = null
)