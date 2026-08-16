package com.yeremi.ocupa2app.network

import com.yeremi.ocupa2app.network.models.AuthResponse
import com.yeremi.ocupa2app.network.models.NewsItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NewsApiService {

    @GET("news")
    suspend fun getNews(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<AuthResponse<List<NewsItem>>>

    @GET("news/{id}")
    suspend fun getNewsDetail(@Path("id") id: String): Response<AuthResponse<NewsItem>>
}