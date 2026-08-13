package com.yeremi.ocupa2app.network

import com.yeremi.ocupa2app.data.local.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    private const val BASE_URL = "https://ocupa2.ia3x.com/apix/"

    fun provideAuthApiService(sessionManager: SessionManager): AuthApiService {
        val client = createOkHttpClient(sessionManager)
        return createRetrofit(client).create(AuthApiService::class.java)
    }

    fun provideProfileApiService(sessionManager: SessionManager): ProfileApiService {
        val client = createOkHttpClient(sessionManager)
        return createRetrofit(client).create(ProfileApiService::class.java)
    }

    private fun createOkHttpClient(sessionManager: SessionManager): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val token = runBlocking { sessionManager.tokenFlow.firstOrNull() }
            val request = chain.request().newBuilder().apply {
                if (!token.isNullOrEmpty()) {
                    addHeader("Authorization", token)
                }
            }.build()
            chain.proceed(request)
        }

        val errorInterceptor = Interceptor { chain ->
            val response = chain.proceed(chain.request())
            if (response.code == 401) {
                runBlocking { sessionManager.clearSession() }
            }
            response
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(errorInterceptor)
            .build()
    }

    private fun createRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
