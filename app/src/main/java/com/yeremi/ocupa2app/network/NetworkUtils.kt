package com.yeremi.ocupa2app.network

import org.json.JSONObject
import retrofit2.Response

/**
 * Parsea el cuerpo del error de una respuesta HTTP fallida.
 * Intenta extraer el campo "message" del JSON devuelto por el servidor.
 * Si no puede, devuelve un mensaje genérico con el código HTTP.
 */
fun <T> Response<T>.parseError(): String {
    return try {
        val errorBody = this.errorBody()?.string()
        if (!errorBody.isNullOrBlank()) {
            val json = JSONObject(errorBody)
            when {
                json.has("message") -> json.getString("message")
                json.has("error")   -> json.getString("error")
                else                -> "Error del servidor (${this.code()})"
            }
        } else {
            "Error del servidor (${this.code()})"
        }
    } catch (e: Exception) {
        "Error del servidor (${this.code()})"
    }
}
