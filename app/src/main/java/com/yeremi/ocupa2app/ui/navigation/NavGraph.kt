package com.yeremi.ocupa2app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.yeremi.ocupa2app.ui.auth.AuthViewModel
import com.yeremi.ocupa2app.ui.auth.ForgotPasswordScreen
import com.yeremi.ocupa2app.ui.auth.LoginScreen
import com.yeremi.ocupa2app.ui.auth.RegisterScreen
import com.yeremi.ocupa2app.ui.offers.ExploreOffersViewModel
import com.yeremi.ocupa2app.ui.offers.MapOffersViewModel
import com.yeremi.ocupa2app.ui.profile.ChangePasswordViewModel
import com.yeremi.ocupa2app.ui.profile.CompleteProfileScreen
import com.yeremi.ocupa2app.ui.profile.CompleteProfileViewModel
import com.yeremi.ocupa2app.ui.news.NewsViewModel
import com.yeremi.ocupa2app.ui.publish.PublishOfferViewModel
import com.yeremi.ocupa2app.ui.myoffers.MyOffersViewModel

// Rutas del flujo de autenticación (sin navbar)
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object CompleteProfile : Screen("complete_profile")
    object Main : Screen("main")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: CompleteProfileViewModel,
    changePasswordViewModel: ChangePasswordViewModel,
    offersViewModel: ExploreOffersViewModel,
    mapOffersViewModel: MapOffersViewModel,
    newsViewModel: NewsViewModel,
    publishViewModel: PublishOfferViewModel,
    myOffersViewModel: MyOffersViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // ──────────────────────────────────────────────
        // FLUJO DE AUTENTICACIÓN (sin Bottom NavBar)
        // ──────────────────────────────────────────────

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onLoginSuccess = { profileCompleted ->
                    if (profileCompleted) {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }
                        }
                    } else {
                        navController.navigate(Screen.CompleteProfile.route) {
                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = { profileCompleted ->
                    if (profileCompleted) {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    } else {
                        navController.navigate(Screen.CompleteProfile.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                viewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.CompleteProfile.route) {
            CompleteProfileScreen(
                viewModel = profileViewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // ──────────────────────────────────────────────
        // FLUJO PRINCIPAL (con Bottom NavBar)
        // ──────────────────────────────────────────────

        composable(Screen.Main.route) {
            MainScaffold(
                offersViewModel = offersViewModel,
                mapOffersViewModel = mapOffersViewModel,
                changePasswordViewModel = changePasswordViewModel,
                profileViewModel = profileViewModel,
                newsViewModel = newsViewModel,
                publishViewModel = publishViewModel,
                myOffersViewModel = myOffersViewModel,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}