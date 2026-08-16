package com.yeremi.ocupa2app.data.repository

import com.yeremi.ocupa2app.network.NewsApiService
import com.yeremi.ocupa2app.network.models.NewsItem

sealed class NewsResult<out T> {
    data class Success<T>(val data: T) : NewsResult<T>()
    data class Error(val message: String) : NewsResult<Nothing>()
}

class NewsRepository(private val api: NewsApiService) {

    suspend fun getNews(limit: Int = 12): NewsResult<List<NewsItem>> {
        return try {
            val response = api.getNews(limit = limit)
            if (response.isSuccessful && response.body()?.ok == true) {
                NewsResult.Success(response.body()?.data ?: emptyList())
            } else {
                NewsResult.Error(response.body()?.message ?: "No se pudieron cargar las noticias")
            }
        } catch (e: Exception) {
            NewsResult.Error(e.message ?: "Error de conexión")
        }
    }
}