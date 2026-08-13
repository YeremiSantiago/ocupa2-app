package com.yeremi.ocupa2app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yeremi.ocupa2app.ui.auth.AuthViewModel
import com.yeremi.ocupa2app.ui.auth.ForgotPasswordScreen
import com.yeremi.ocupa2app.ui.auth.LoginScreen
import com.yeremi.ocupa2app.ui.auth.RegisterScreen
import com.yeremi.ocupa2app.ui.profile.CompleteProfileScreen
import com.yeremi.ocupa2app.ui.profile.CompleteProfileViewModel
import com.yeremi.ocupa2app.ui.profile.ChangePasswordScreen
import com.yeremi.ocupa2app.ui.profile.ChangePasswordViewModel
import com.yeremi.ocupa2app.ui.offers.ExploreOffersScreen
import com.yeremi.ocupa2app.ui.offers.ExploreOffersViewModel
import com.yeremi.ocupa2app.ui.offers.MapOffersScreen
import com.yeremi.ocupa2app.ui.offers.MapOffersViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object CompleteProfile : Screen("complete_profile")
    object Offers : Screen("offers")
    object OffersMap : Screen("offers_map")
    object ChangePassword : Screen("change_password")
    object OfferDetail : Screen("offer_detail/{offerId}") {
        fun createRoute(offerId: Int) = "offer_detail/$offerId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: CompleteProfileViewModel,
    changePasswordViewModel: ChangePasswordViewModel,
    offersViewModel: ExploreOffersViewModel,
    mapOffersViewModel: MapOffersViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                onLoginSuccess = { profileCompleted ->
                    if (profileCompleted) {
                        navController.navigate(Screen.Offers.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.CompleteProfile.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = { profileCompleted ->
                    if (profileCompleted) {
                        navController.navigate(Screen.Offers.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.CompleteProfile.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CompleteProfile.route) {
            CompleteProfileScreen(
                viewModel = profileViewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Offers.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(
                viewModel = changePasswordViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Offers.route) {
            ExploreOffersScreen(
                viewModel = offersViewModel,
                onNavigateToMap = { navController.navigate(Screen.OffersMap.route) },
                onNavigateToDetail = { id -> navController.navigate(Screen.OfferDetail.createRoute(id)) }
            )
        }

        composable(Screen.OffersMap.route) {
            MapOffersScreen(
                viewModel = mapOffersViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToList = {
                    navController.navigate(Screen.Offers.route) {
                        popUpTo(Screen.OffersMap.route) { inclusive = true }
                    }
                },
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.OfferDetail.createRoute(id))
                }
            )
        }

        composable(
            route = Screen.OfferDetail.route,
            arguments = listOf(navArgument("offerId") { type = NavType.IntType })
        ) {
            // Placeholder
        }

        composable(Screen.Home.route) {
            // Redirect
        }
    }
}
