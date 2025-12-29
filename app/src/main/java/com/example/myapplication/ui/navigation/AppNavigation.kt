package com.example.myapplication.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screen.AuthScreen
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
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            // DON'T use Box with fillMaxSize and verticalScroll together
            // Just stack them directly
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Music Recognition at top
                MusicRecognitionScreen(navController = navController)

                // Recognition Result below (it has its own scrolling)
                RecognitionResult(navController = navController)
            }
        }

        composable(Screen.Fingerprint.route) {
            // Placeholder for Fingerprint screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0A0033)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Fingerprint Screen\n(Add New Song)",
                    color = Color.White,
                    fontSize = 24.sp
                )
            }
        }

        composable(Screen.Profile.route) {
            // Placeholder for Profile screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0A0033)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Profile Screen",
                    color = Color.White,
                    fontSize = 24.sp
                )
            }
        }
    }
}