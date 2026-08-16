package com.yeremi.ocupa2app.network

import android.util.Log
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

    fun provideNewsApiService(sessionManager: SessionManager): NewsApiService {
        val client = createOkHttpClient(sessionManager)
        return createRetrofit(client).create(NewsApiService::class.java)
    }

    fun providePublishApiService(sessionManager: SessionManager): PublishApiService {
        val client = createOkHttpClient(sessionManager)
        return createRetrofit(client).create(PublishApiService::class.java)
    }

    fun provideMyOffersApiService(sessionManager: SessionManager): MyOffersApiService {
        val client = createOkHttpClient(sessionManager)
        return createRetrofit(client).create(MyOffersApiService::class.java)
    }

    private fun createOkHttpClient(sessionManager: SessionManager): OkHttpClient {

        // 1. Auth interceptor — añade Bearer token antes del logging
        val authInterceptor = Interceptor { chain ->

            val token = runBlocking {
                sessionManager.tokenFlow.firstOrNull()
            }

            val requestBuilder = chain.request().newBuilder()

            if (!token.isNullOrEmpty()) {

                // Añade "Bearer " si el token guardado no lo incluye ya
                val bearer =
                    if (token.startsWith("Bearer ")) {
                        token
                    } else {
                        "Bearer $token"
                    }

                requestBuilder.addHeader(
                    "Authorization",
                    bearer
                )

                Log.d(
                    "OCUPA2_NET",
                    "Auth header → $bearer"
                )

            } else {

                Log.w(
                    "OCUPA2_NET",
                    "⚠ Sin token — request sin Authorization"
                )
            }

            chain.proceed(
                requestBuilder.build()
            )
        }

        // 2. Logging interceptor — corre DESPUÉS del auth
        val loggingInterceptor =
            HttpLoggingInterceptor { message ->
                Log.d("OCUPA2_HTTP", message)
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

        // 3. Error interceptor — detecta 401 y limpia sesión
        val errorInterceptor = Interceptor { chain ->

            val response = chain.proceed(
                chain.request()
            )

            if (response.code == 401) {

                Log.e(
                    "OCUPA2_NET",
                    "401 Unauthorized → limpiando sesión"
                )

                runBlocking {
                    sessionManager.clearSession()
                }
            }

            response
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(errorInterceptor)
            .build()
    }

    private fun createRetrofit(
        client: OkHttpClient
    ): Retrofit {

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }
}