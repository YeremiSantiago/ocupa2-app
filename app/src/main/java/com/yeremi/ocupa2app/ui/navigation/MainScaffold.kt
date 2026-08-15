package com.yeremi.ocupa2app.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yeremi.ocupa2app.ui.offers.ExploreOffersScreen
import com.yeremi.ocupa2app.ui.offers.ExploreOffersViewModel
import com.yeremi.ocupa2app.ui.offers.MapOffersScreen
import com.yeremi.ocupa2app.ui.offers.MapOffersViewModel
import com.yeremi.ocupa2app.ui.profile.ChangePasswordScreen
import com.yeremi.ocupa2app.ui.profile.ChangePasswordViewModel
import com.yeremi.ocupa2app.ui.profile.CompleteProfileViewModel
import com.yeremi.ocupa2app.ui.profile.ProfileScreen
import com.yeremi.ocupa2app.ui.theme.AcidLime
import com.yeremi.ocupa2app.ui.theme.ElectricTeal
import com.yeremi.ocupa2app.ui.theme.SolarOrange

// Rutas del flujo principal (con navbar)
sealed class MainScreen(val route: String) {
    object Home     : MainScreen("main_home")
    object Explore  : MainScreen("main_explore")
    object Map      : MainScreen("main_map")
    object MyApps   : MainScreen("main_myapps")
    object Profile  : MainScreen("main_profile")
}

// Definición de los ítems de la barra
val bottomNavItems = listOf(
    BottomNavItem(
        route = MainScreen.Home.route,
        label = "Inicio",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home,
        accentColor = AcidLime
    ),
    BottomNavItem(
        route = MainScreen.Explore.route,
        label = "Explorar",
        icon = Icons.Outlined.Search,
        selectedIcon = Icons.Filled.Search,
        accentColor = AcidLime
    ),
    BottomNavItem(
        route = MainScreen.Map.route,
        label = "Mapa",
        icon = Icons.Outlined.Map,
        selectedIcon = Icons.Filled.Map,
        accentColor = ElectricTeal
    ),
    BottomNavItem(
        route = MainScreen.MyApps.route,
        label = "Mis Apps",
        icon = Icons.Outlined.GridView,
        selectedIcon = Icons.Filled.GridView,
        accentColor = AcidLime
    ),
    BottomNavItem(
        route = MainScreen.Profile.route,
        label = "Perfil",
        icon = Icons.Outlined.Person,
        selectedIcon = Icons.Filled.Person,
        accentColor = AcidLime
    )
)

@Composable
fun MainScaffold(
    offersViewModel: ExploreOffersViewModel,
    mapOffersViewModel: MapOffersViewModel,
    changePasswordViewModel: ChangePasswordViewModel,
    profileViewModel: CompleteProfileViewModel,
    onLogout: () -> Unit
) {
    val mainNavController = rememberNavController()

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            Ocupa2BottomBar(
                navController = mainNavController,
                items = bottomNavItems
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = mainNavController,
            startDestination = MainScreen.Home.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable(MainScreen.Home.route) {
                // TODO: HomeScreen - pendiente implementación por otro compañero
                PlaceholderScreen(title = "Inicio")
            }

            composable(MainScreen.Explore.route) {
                ExploreOffersScreen(
                    viewModel = offersViewModel,
                    onNavigateToMap = {
                        mainNavController.navigate(MainScreen.Map.route)
                    },
                    onNavigateToDetail = { id ->
                        mainNavController.navigate("offer_detail/$id")
                    }
                )
            }

            composable(MainScreen.Map.route) {
                MapOffersScreen(
                    viewModel = mapOffersViewModel,
                    onNavigateBack = { mainNavController.popBackStack() },
                    onNavigateToList = {
                        mainNavController.navigate(MainScreen.Explore.route) {
                            popUpTo(MainScreen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToDetail = { id ->
                        mainNavController.navigate("offer_detail/$id")
                    }
                )
            }

            composable(MainScreen.MyApps.route) {
                // TODO: MyApplicationsScreen - Mis solicitudes de empleo
                PlaceholderScreen(title = "Mis Apps")
            }

            composable(MainScreen.Profile.route) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onNavigateToChangePassword = {
                        mainNavController.navigate("change_password")
                    },
                    onLogout = onLogout
                )
            }

            composable("change_password") {
                ChangePasswordScreen(
                    viewModel = changePasswordViewModel,
                    onNavigateBack = { mainNavController.popBackStack() }
                )
            }

            composable(
                route = "offer_detail/{id}",
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                PlaceholderScreen(title = "Detalle de Oferta\nID: $id")
            }
        }
    }
}
