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
    object Home       : MainScreen("main_home")
    object Explore    : MainScreen("main_explore")
    object Publish    : MainScreen("main_publish")
    object MySpace    : MainScreen("main_myspace")
    object Profile    : MainScreen("main_profile")
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
        route = MainScreen.Publish.route,
        label = "Publicar",
        icon = Icons.Outlined.AddCircle,
        selectedIcon = Icons.Filled.AddCircle,
        accentColor = SolarOrange
    ),
    BottomNavItem(
        route = MainScreen.MySpace.route,
        label = "Mi Espacio",
        icon = Icons.Outlined.Folder,
        selectedIcon = Icons.Filled.Folder,
        accentColor = ElectricTeal
    ),
    BottomNavItem(
        route = MainScreen.Profile.route,
        label = "Perfil",
        icon = Icons.Outlined.AccountCircle,
        selectedIcon = Icons.Filled.AccountCircle,
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
                        mainNavController.navigate("explore_map")
                    },
                    onNavigateToDetail = { id ->
                        mainNavController.navigate("offer_detail/$id")
                    }
                )
            }

            composable("explore_map") {
                MapOffersScreen(
                    viewModel = mapOffersViewModel,
                    onNavigateBack = { mainNavController.popBackStack() },
                    onNavigateToList = { mainNavController.popBackStack() },
                    onNavigateToDetail = { id ->
                        mainNavController.navigate("offer_detail/$id")
                    }
                )
            }

            composable(MainScreen.Publish.route) {
                // TODO: PublishOfferScreen - pendiente implementación
                PlaceholderScreen(title = "Publicar Oferta")
            }

            composable(MainScreen.MySpace.route) {
                // TODO: MySpaceScreen (Mis Ofertas / Mis Aplicaciones / Mis Pagos)
                PlaceholderScreen(title = "Mi Espacio")
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
        }
    }
}
