package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screen.AuthScreen
import com.example.myapplication.ui.screen.MusicRecognitionScreen
import com.example.myapplication.ui.screen.OnboardingScreen
import com.example.myapplication.ui.screen.RecognitionResult
import com.example.myapplication.ui.viewmodel.OnboardingViewModel
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
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Add Onboarding Screen
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

        composable(Screen.Home.route) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                MusicRecognitionScreen(navController = navController)
                RecognitionResult(navController = navController)
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