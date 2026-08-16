package com.yeremi.ocupa2app

import android.content.Context
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

import com.yeremi.ocupa2app.data.local.SessionManager
import com.yeremi.ocupa2app.data.repository.AuthRepository
import com.yeremi.ocupa2app.data.repository.MyOffersRepository
import com.yeremi.ocupa2app.data.repository.NewsRepository
import com.yeremi.ocupa2app.data.repository.ProfileRepository
import com.yeremi.ocupa2app.data.repository.PublishOfferRepository

import com.yeremi.ocupa2app.network.NetworkModule

import com.yeremi.ocupa2app.ui.auth.AuthViewModel
import com.yeremi.ocupa2app.ui.myoffers.MyOffersViewModel
import com.yeremi.ocupa2app.ui.navigation.NavGraph
import com.yeremi.ocupa2app.ui.news.NewsViewModel
import com.yeremi.ocupa2app.ui.offers.ExploreOffersViewModel
import com.yeremi.ocupa2app.ui.offers.MapOffersViewModel
import com.yeremi.ocupa2app.ui.profile.ChangePasswordViewModel
import com.yeremi.ocupa2app.ui.profile.CompleteProfileViewModel
import com.yeremi.ocupa2app.ui.publish.PublishOfferViewModel
import com.yeremi.ocupa2app.ui.theme.Obsidian
import com.yeremi.ocupa2app.ui.theme.Ocupa2AppTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Crear todas las dependencias de la aplicación
        val dependencies = AppDependencies(applicationContext)

        setContent {
            Ocupa2AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Obsidian
                ) {
                    Ocupa2Navigation(dependencies)
                }
            }
        }
    }
}


/**
 * Contenedor de las dependencias de la aplicación.
 *
 * Aquí se crean:
 * - SessionManager
 * - API Services
 * - Repositories
 * - ViewModels
 */
private class AppDependencies(
    applicationContext: Context
) {

    // ============================================================
    // SESSION
    // ============================================================

    private val sessionManager =
        SessionManager(applicationContext)


    // ============================================================
    // API SERVICES
    // ============================================================

    private val authApiService =
        NetworkModule.provideAuthApiService(sessionManager)

    private val profileApiService =
        NetworkModule.provideProfileApiService(sessionManager)

    private val newsApiService =
        NetworkModule.provideNewsApiService(sessionManager)

    private val publishApiService =
        NetworkModule.providePublishApiService(sessionManager)

    private val myOffersApiService =
        NetworkModule.provideMyOffersApiService(sessionManager)


    // ============================================================
    // REPOSITORIES
    // ============================================================

    private val authRepository =
        AuthRepository(
            authApiService,
            sessionManager
        )

    private val profileRepository =
        ProfileRepository(
            profileApiService
        )

    private val newsRepository =
        NewsRepository(
            newsApiService
        )

    private val publishRepository =
        PublishOfferRepository(
            publishApiService
        )

    private val myOffersRepository =
        MyOffersRepository(
            myOffersApiService
        )


    // ============================================================
    // VIEWMODELS
    // ============================================================

    val authViewModel =
        AuthViewModel(
            authRepository
        )

    val profileViewModel =
        CompleteProfileViewModel(
            profileRepository
        )

    val changePasswordViewModel =
        ChangePasswordViewModel(
            profileRepository
        )

    val offersViewModel =
        ExploreOffersViewModel(
            profileRepository
        )

    val mapOffersViewModel =
        MapOffersViewModel(
            profileRepository
        )

    val newsViewModel =
        NewsViewModel(
            newsRepository
        )

    val publishViewModel =
        PublishOfferViewModel(
            publishRepository
        )

    val myOffersViewModel =
        MyOffersViewModel(
            myOffersRepository
        )
}


/**
 * Maneja la navegación principal de la aplicación.
 */
@Composable
private fun Ocupa2Navigation(
    dependencies: AppDependencies
) {

    // Controlador de navegación
    val navController = rememberNavController()

    // NavGraph recibe todos los ViewModels
    NavGraph(
        navController = navController,

        // Auth
        authViewModel = dependencies.authViewModel,

        // Profile
        profileViewModel = dependencies.profileViewModel,
        changePasswordViewModel = dependencies.changePasswordViewModel,

        // Offers
        offersViewModel = dependencies.offersViewModel,
        mapOffersViewModel = dependencies.mapOffersViewModel,

        // News
        newsViewModel = dependencies.newsViewModel,

        // Publish Offer
        publishViewModel = dependencies.publishViewModel,

        // My Offers
        myOffersViewModel = dependencies.myOffersViewModel
    )
}