package com.yeremi.ocupa2app.data.repository

import com.yeremi.ocupa2app.network.MyOffersApiService
import com.yeremi.ocupa2app.network.models.*

sealed class MyOffersResult<out T> {
    data class Success<T>(val data: T) : MyOffersResult<T>()
    data class Error(val message: String) : MyOffersResult<Nothing>()
}

class MyOffersRepository(private val api: MyOffersApiService) {

    suspend fun getMyOffers(): MyOffersResult<List<MyOffer>> = try {
        val response = api.getMyOffers()
        if (response.isSuccessful && response.body()?.ok == true) {
            MyOffersResult.Success(response.body()?.data ?: emptyList())
        } else {
            MyOffersResult.Error(response.body()?.message ?: "No se pudieron cargar tus ofertas")
        }
    } catch (e: Exception) {
        MyOffersResult.Error(e.message ?: "Error de conexión")
    }

    suspend fun getApplicants(offerId: String): MyOffersResult<List<Applicant>> = try {
        val response = api.getApplicants(offerId)
        if (response.isSuccessful && response.body()?.ok == true) {
            MyOffersResult.Success(response.body()?.data ?: emptyList())
        } else {
            MyOffersResult.Error(response.body()?.message ?: "No se pudieron cargar los aplicantes")
        }
    } catch (e: Exception) {
        MyOffersResult.Error(e.message ?: "Error de conexión")
    }

    suspend fun deactivateOffer(offerId: String): MyOffersResult<Unit> = try {
        val response = api.deactivateOffer(offerId)
        if (response.isSuccessful) MyOffersResult.Success(Unit)
        else MyOffersResult.Error(response.body()?.message ?: "No se pudo desactivar la oferta")
    } catch (e: Exception) {
        MyOffersResult.Error(e.message ?: "Error de conexión")
    }

    suspend fun rateApplicant(applicationId: String, rating: Int): MyOffersResult<Unit> =
        updateApplication(applicationId, ApplicationActionRequest(rating = rating))

    suspend fun discardApplicant(applicationId: String): MyOffersResult<Unit> =
        updateApplication(applicationId, ApplicationActionRequest(status = "discarded"))

    suspend fun markFinalist(applicationId: String): MyOffersResult<Unit> =
        updateApplication(applicationId, ApplicationActionRequest(status = "finalist"))

    suspend fun chooseWinner(
        applicationId: String,
        salary: Double? = null,
        currency: String? = null,
        startDate: String? = null,
        duration: String? = null
    ): MyOffersResult<Unit> = updateApplication(
        applicationId,
        ApplicationActionRequest(status = "winner", salary = salary, currency = currency, startDate = startDate, duration = duration)
    )

    private suspend fun updateApplication(applicationId: String, request: ApplicationActionRequest): MyOffersResult<Unit> = try {
        val response = api.updateApplication(applicationId, request)
        if (response.isSuccessful) MyOffersResult.Success(Unit)
        else MyOffersResult.Error(response.body()?.message ?: "No se pudo actualizar la aplicación")
    } catch (e: Exception) {
        MyOffersResult.Error(e.message ?: "Error de conexión")
    }
}