package com.yeremi.ocupa2app.data.repository

import com.yeremi.ocupa2app.network.PublishApiService
import com.yeremi.ocupa2app.network.models.*
import android.util.Base64
import java.io.File

sealed class PublishResult<out T> {
    data class Success<T>(val data: T) : PublishResult<T>()
    data class Error(val message: String) : PublishResult<Nothing>()
}

class PublishOfferRepository(private val api: PublishApiService) {

    suspend fun getJobTypes(): PublishResult<List<JobTypeDetail>> = try {
        val response = api.getJobTypesDetailed()
        if (response.isSuccessful && response.body()?.ok == true) {
            PublishResult.Success(response.body()?.data ?: emptyList())
        } else {
            PublishResult.Error(response.body()?.message ?: "No se pudieron cargar los tipos de empleo")
        }
    } catch (e: Exception) {
        PublishResult.Error(e.message ?: "Error de conexión")
    }

    suspend fun uploadPhoto(photoFile: File): PublishResult<String> = try {
        val bytes = photoFile.readBytes()
        val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
        val response = api.uploadImage(UploadImageRequest(image = base64))
        if (response.isSuccessful && response.body()?.ok == true && response.body()?.data != null) {
            PublishResult.Success(response.body()!!.data!!.url)
        } else {
            PublishResult.Error(response.body()?.message ?: "No se pudo subir la foto")
        }
    } catch (e: Exception) {
        PublishResult.Error(e.message ?: "Error de conexión")
    }

    suspend fun chargeCard(request: ChargeCardRequest): PublishResult<Payment> = try {
        val response = api.chargeCard(request)
        if (response.isSuccessful && response.body()?.ok == true && response.body()?.data != null) {
            PublishResult.Success(response.body()!!.data!!)
        } else {
            PublishResult.Error(response.body()?.message ?: "Pago rechazado")
        }
    } catch (e: Exception) {
        PublishResult.Error(e.message ?: "Error de conexión")
    }

    suspend fun createOffer(request: CreateOfferRequest): PublishResult<CreateOfferResponse> = try {
        val response = api.createOffer(request)
        if (response.isSuccessful && response.body()?.ok == true && response.body()?.data != null) {
            PublishResult.Success(response.body()!!.data!!)
        } else {
            PublishResult.Error(response.body()?.message ?: "No se pudo crear la oferta")
        }
    } catch (e: Exception) {
        PublishResult.Error(e.message ?: "Error de conexión")
    }

    suspend fun getMyPayments(): PublishResult<List<Payment>> = try {
        val response = api.getMyPayments()
        if (response.isSuccessful && response.body()?.ok == true) {
            PublishResult.Success(response.body()?.data ?: emptyList())
        } else {
            PublishResult.Error(response.body()?.message ?: "No se pudieron cargar los pagos")
        }
    } catch (e: Exception) {
        PublishResult.Error(e.message ?: "Error de conexión")
    }
}