package com.yeremi.ocupa2app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.yeremi.ocupa2app.data.local.SessionManager
import com.yeremi.ocupa2app.data.repository.AuthRepository
import com.yeremi.ocupa2app.data.repository.ProfileRepository
import com.yeremi.ocupa2app.network.NetworkModule
import com.yeremi.ocupa2app.ui.auth.AuthViewModel
import com.yeremi.ocupa2app.ui.profile.CompleteProfileViewModel
import com.yeremi.ocupa2app.ui.profile.ChangePasswordViewModel
import com.yeremi.ocupa2app.ui.offers.ExploreOffersViewModel
import com.yeremi.ocupa2app.ui.offers.MapOffersViewModel
import com.yeremi.ocupa2app.ui.navigation.NavGraph
import com.yeremi.ocupa2app.ui.theme.Ocupa2AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Manual dependency injection for now
        val sessionManager = SessionManager(applicationContext)
        val authApiService = NetworkModule.provideAuthApiService(sessionManager)
        val profileApiService = NetworkModule.provideProfileApiService(sessionManager)
        
        val authRepository = AuthRepository(authApiService, sessionManager)
        val profileRepository = ProfileRepository(profileApiService)
        
        val authViewModel = AuthViewModel(authRepository)
        val profileViewModel = CompleteProfileViewModel(profileRepository)
        val changePasswordViewModel = ChangePasswordViewModel(profileRepository)
        val offersViewModel = ExploreOffersViewModel(profileRepository)
        val mapOffersViewModel = MapOffersViewModel(profileRepository)
        
        enableEdgeToEdge()
        setContent {
            val token by sessionManager.tokenFlow.collectAsState(initial = "LOADING")

            Ocupa2AppTheme {
                if (token == "LOADING") {
                    // Pantalla de carga (Splash) muy breve mientras lee DataStore
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = com.yeremi.ocupa2app.ui.theme.Obsidian
                    ) {
                        // Pantalla negra de carga inicial
                    }
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = com.yeremi.ocupa2app.ui.theme.Obsidian
                    ) {
                        val navController = rememberNavController()
                        val startDest = if (token.isNullOrEmpty()) com.yeremi.ocupa2app.ui.navigation.Screen.Login.route else com.yeremi.ocupa2app.ui.navigation.Screen.Main.route
                        
                        NavGraph(
                            navController = navController,
                            startDestination = startDest,
                            authViewModel = authViewModel,
                            profileViewModel = profileViewModel,
                            changePasswordViewModel = changePasswordViewModel,
                            offersViewModel = offersViewModel,
                            mapOffersViewModel = mapOffersViewModel
                        )
                    }
                }
            }
        }
    }
}
