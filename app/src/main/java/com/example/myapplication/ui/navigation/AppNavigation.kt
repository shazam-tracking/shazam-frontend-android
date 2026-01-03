package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screen.*
import com.example.myapplication.ui.viewmodel.FingerprintViewModel
import com.example.myapplication.ui.viewmodel.MusicRecognitionViewModel
import com.example.myapplication.ui.viewmodel.ProfileViewModel

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

            if (recognitionState.recognitionResult?.match == true) {
                RecognitionResult(
                    navController = navController,
                    recognitionData = recognitionState.recognitionResult?.data
                )
            } else {
                MusicRecognitionScreen(
                    navController = navController,
                    viewModel = recognitionViewModel
                )
            }
        }

        composable(Screen.Fingerprint.route) {
            val fingerprintViewModel: FingerprintViewModel = hiltViewModel()

            FingerprintScreen(
                navController = navController,
                viewModel = fingerprintViewModel
            )
        }

        composable(Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = hiltViewModel()

            ProfileScreen(
                navController = navController,
                onLogout = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                viewModel = profileViewModel
            )
        }

        composable(Screen.EditProfile.route) {
            val profileViewModel: ProfileViewModel = hiltViewModel()

            EditProfileScreen(
                navController = navController,
                viewModel = profileViewModel
            )
        }
    }
}