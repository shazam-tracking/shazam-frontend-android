package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.example.myapplication.ui.screen.*
import com.example.myapplication.ui.viewmodel.FingerprintViewModel
import com.example.myapplication.ui.viewmodel.MusicRecognitionViewModel
import com.example.myapplication.ui.viewmodel.OnboardingViewModel
import com.example.myapplication.ui.viewmodel.ProfileViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                viewModel = OnboardingViewModel(context),
                onFinished = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Home.route
        ) { backStackEntry ->
            // Get ViewModel scoped to navigation graph to prevent recreation
            val recognitionViewModel: MusicRecognitionViewModel = hiltViewModel(
                viewModelStoreOwner = backStackEntry
            )
            val recognitionState by recognitionViewModel.recognitionState.collectAsState()

            // Logic to switch between Recognition Screen and Result Screen
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

        composable(
            route = Screen.Fingerprint.route
        ) { backStackEntry ->
            val fingerprintViewModel: FingerprintViewModel = hiltViewModel(
                viewModelStoreOwner = backStackEntry
            )

            FingerprintScreen(
                navController = navController,
                viewModel = fingerprintViewModel
            )
        }

        composable(
            route = Screen.Profile.route
        ) { backStackEntry ->
            val profileViewModel: ProfileViewModel = hiltViewModel(
                viewModelStoreOwner = backStackEntry
            )

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

        composable(
            route = Screen.EditProfile.route
        ) { backStackEntry ->
            // Get parent Profile screen's ViewModel
            val parentEntry = navController.getBackStackEntry(Screen.Profile.route)
            val profileViewModel: ProfileViewModel = hiltViewModel(
                viewModelStoreOwner = parentEntry
            )

            EditProfileScreen(
                navController = navController,
                viewModel = profileViewModel
            )
        }
    }
}