package com.yeremi.ocupa2app.network

private const val OCUPA2_HOST =
    "https://ocupa2.ia3x.com"

fun resolveMediaUrl(
    value: String?
): String? {

    if (value.isNullOrBlank()) {
        return null
    }

    val cleanValue =
        value.trim()

    return when {

        cleanValue.startsWith(
            "http://",
            ignoreCase = true
        ) -> cleanValue

        cleanValue.startsWith(
            "https://",
            ignoreCase = true
        ) -> cleanValue

        cleanValue.startsWith("/") ->
            "$OCUPA2_HOST$cleanValue"

        else ->
            "$OCUPA2_HOST/$cleanValue"
    }
}