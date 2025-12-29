package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screen.MusicRecognitionScreen
import com.example.myapplication.ui.screen.RecognitionResult

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Auth.route) {
            // Auth screen
        }

        composable(Screen.Home.route) {
            // Your existing MusicRecognitionScreen or create a Home screen with bottom nav
            MusicRecognitionScreen()
            RecognitionResult()
        }

        composable(Screen.Fingerprint.route) {
            // Fingerprint screen
        }

        composable(Screen.Profile.route) {
            // Your profile screen
        }
    }
}