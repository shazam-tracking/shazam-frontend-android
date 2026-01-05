package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.local.OnboardingPreferences
import com.example.myapplication.data.local.TokenManager
import com.example.myapplication.ui.navigation.AppNavigation
import com.example.myapplication.ui.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check onboarding status
        val onboardingPreferences = OnboardingPreferences(this)
        val isOnboardingCompleted = onboardingPreferences.isOnboardingCompleted()

        // Check if user is logged in
        val isLoggedIn = runBlocking {
            tokenManager.getToken().first() != null
        }

        // Determine start destination
        val startDestination = when {
            !isOnboardingCompleted -> Screen.Onboarding.route
            !isLoggedIn -> Screen.Auth.route
            else -> Screen.Home.route
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()

                    AppNavigation(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}