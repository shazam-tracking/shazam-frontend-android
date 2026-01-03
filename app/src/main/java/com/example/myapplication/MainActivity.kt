package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
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
        enableEdgeToEdge()

        // Check if user is logged in
        val isLoggedIn = runBlocking {
            tokenManager.getToken().first() != null
        }

        setContent {
            MaterialTheme {
                Surface {
                    val navController = rememberNavController()

                    AppNavigation(
                        navController = navController,
                        startDestination = if (isLoggedIn) Screen.Fingerprint.route else Screen.Auth.route
                    )
                }
            }
        }
    }
}