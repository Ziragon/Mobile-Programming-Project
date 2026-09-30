package com.example.buggame.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.buggame.ui.menu.MainTabsScreen
import com.example.buggame.ui.registration.RegistrationScreen

object Routes {
    const val REGISTRATION = "registration"
    const val MAIN = "main"
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.REGISTRATION
    ) {
        composable(Routes.REGISTRATION) {
            RegistrationScreen(
                onRegistrationComplete = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.REGISTRATION) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MAIN) {
            MainTabsScreen()
        }
    }
}
