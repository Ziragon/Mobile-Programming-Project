package com.example.buggame.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.buggame.ui.authors.AuthorsScreen
import com.example.buggame.ui.registration.RegistrationScreen
import com.example.buggame.ui.rules.RulesScreen
import com.example.buggame.ui.settings.SettingsScreen

object Routes {
    const val REGISTRATION = "registration"
    const val MENU = "menu"
    const val RULES = "rules"
    const val AUTHORS = "authors"
    const val SETTINGS = "settings"
    const val GAME = "game"
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
                    navController.navigate(Routes.MENU) {
                        popUpTo(Routes.REGISTRATION) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MENU) {
            MenuScreen(
                onRulesClick = { navController.navigate(Routes.RULES) },
                onAuthorsClick = { navController.navigate(Routes.AUTHORS) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                onPlayClick = { navController.navigate(Routes.GAME) }
            )
        }

        composable(Routes.RULES) {
            RulesScreen()
        }

        composable(Routes.AUTHORS) {
            AuthorsScreen()
        }

        composable(Routes.SETTINGS) {
            SettingsScreen()
        }

        composable(Routes.GAME) {
            // заглушка, пока нет игрового экрана
            GamePlaceholderScreen()
        }
    }
}