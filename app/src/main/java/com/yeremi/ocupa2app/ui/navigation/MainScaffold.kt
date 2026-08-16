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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yeremi.ocupa2app.ui.offers.ExploreOffersScreen
import com.yeremi.ocupa2app.ui.offers.ExploreOffersViewModel
import com.yeremi.ocupa2app.ui.offers.MapOffersScreen
import com.yeremi.ocupa2app.ui.offers.MapOffersViewModel
import com.yeremi.ocupa2app.ui.profile.ChangePasswordScreen
import com.yeremi.ocupa2app.ui.profile.ChangePasswordViewModel
import com.yeremi.ocupa2app.ui.profile.CompleteProfileViewModel
import com.yeremi.ocupa2app.ui.profile.ProfileScreen
import com.yeremi.ocupa2app.ui.news.NewsViewModel
import com.yeremi.ocupa2app.ui.publish.PublishOfferViewModel
import com.yeremi.ocupa2app.ui.myoffers.MyOffersViewModel
import com.yeremi.ocupa2app.ui.theme.AcidLime
import com.yeremi.ocupa2app.ui.theme.ElectricTeal
import com.yeremi.ocupa2app.ui.theme.SolarOrange

// Rutas del flujo principal (con navbar)
sealed class MainScreen(val route: String) {
    object Home : MainScreen("main_home")
    object Explore : MainScreen("main_explore")
    object Map : MainScreen("main_map")
    object MyApps : MainScreen("main_myapps")
    object Profile : MainScreen("main_profile")
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
    newsViewModel: NewsViewModel,
    publishViewModel: PublishOfferViewModel,
    myOffersViewModel: MyOffersViewModel,
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
            modifier = Modifier.padding(
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {

            composable(MainScreen.Home.route) {
                com.yeremi.ocupa2app.ui.home.HomeScreen(
                    onNavigateToExplore = {
                        mainNavController.navigate(MainScreen.Explore.route) {
                            popUpTo(MainScreen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToNews = {
                        mainNavController.navigate("news")
                    },
                    onNavigateToPublish = {
                        mainNavController.navigate("publish_offer")
                    },
                    onNavigateToMyOffers = {
                        mainNavController.navigate("my_offers")
                    },
                    onNavigateToAbout = {
                        mainNavController.navigate("about")
                    }
                )
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
                    onNavigateBack = {
                        mainNavController.popBackStack()
                    },
                    onNavigateToList = {
                        mainNavController.navigate(MainScreen.Explore.route) {
                            popUpTo(MainScreen.Home.route) {
                                saveState = true
                            }
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
                    onNavigateBack = {
                        mainNavController.popBackStack()
                    }
                )
            }

            composable("news") {
                com.yeremi.ocupa2app.ui.news.NewsListScreen(
                    viewModel = newsViewModel,
                    onNavigateToDetail = { index ->
                        mainNavController.navigate(
                            "news_detail/$index"
                        )
                    },
                    onNavigateBack = {
                        mainNavController.popBackStack()
                    }
                )
            }

            composable(
                "news_detail/{newsIndex}",
                arguments = listOf(
                    navArgument("newsIndex") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val newsIndex =
                    backStackEntry.arguments?.getInt("newsIndex") ?: 0

                com.yeremi.ocupa2app.ui.news.NewsDetailScreen(
                    newsIndex = newsIndex,
                    viewModel = newsViewModel,
                    onNavigateBack = {
                        mainNavController.popBackStack()
                    }
                )
            }

            composable("publish_offer") {
                com.yeremi.ocupa2app.ui.publish.PublishOfferScreen(
                    viewModel = publishViewModel,
                    onPublishSuccess = {
                        mainNavController.navigate("my_offers") {
                            popUpTo("publish_offer") {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateBack = {
                        mainNavController.popBackStack()
                    }
                )
            }

            composable("my_offers") {
                com.yeremi.ocupa2app.ui.myoffers.MyOffersScreen(
                    viewModel = myOffersViewModel,
                    onNavigateToApplicants = { offerId ->
                        mainNavController.navigate(
                            "my_offer_applicants/$offerId"
                        )
                    },
                    onNavigateToPublish = {
                        mainNavController.navigate("publish_offer")
                    },
                    onNavigateBack = {
                        mainNavController.popBackStack()
                    }
                )
            }

            composable(
                "my_offer_applicants/{offerId}",
                arguments = listOf(
                    navArgument("offerId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->

                val offerId =
                    backStackEntry.arguments?.getString("offerId") ?: ""

                com.yeremi.ocupa2app.ui.myoffers.MyOfferApplicantsScreen(
                    offerId = offerId,
                    viewModel = myOffersViewModel,
                    onNavigateBack = {
                        mainNavController.popBackStack()
                    }
                )
            }

            composable("about") {
                com.yeremi.ocupa2app.ui.about.AboutScreen(
                    onNavigateBack = {
                        mainNavController.popBackStack()
                    }
                )
            }
        }
    }
}