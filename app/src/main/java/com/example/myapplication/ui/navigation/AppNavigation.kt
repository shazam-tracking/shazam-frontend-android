package com.example.myapplication.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screen.AuthScreen
import com.example.myapplication.ui.screen.MusicRecognitionScreen
import com.example.myapplication.ui.screen.RecognitionResult
import com.example.myapplication.ui.viewmodel.MusicRecognitionViewModel

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
            val recognitionViewModel: MusicRecognitionViewModel = hiltViewModel()
            val recognitionState by recognitionViewModel.recognitionState.collectAsState()

            // CRITICAL FIX: Show EITHER recognition screen OR result, not both!
            if (recognitionState.recognitionResult?.match == true) {
                // Show result screen when match is found
                RecognitionResult(
                    navController = navController,
                    recognitionData = recognitionState.recognitionResult?.data
                )
            } else {
                // Show recording screen by default
                MusicRecognitionScreen(
                    navController = navController,
                    viewModel = recognitionViewModel
                )
            }
        }

        composable(Screen.Fingerprint.route) {
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