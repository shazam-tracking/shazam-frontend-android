package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.ui.navigation.Screen

@Composable
fun SharedBottomNavBar(
    navController: NavHostController,
    currentRoute: String
) {
    NavigationBar(
        containerColor = Color(0xFF1A1A2E),
        contentColor = Color.White
    ) {
        // Home
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = { Text("Home", fontSize = 12.sp) },
            selected = currentRoute == Screen.Home.route,
            onClick = {
                if (currentRoute != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        // Clear back stack to avoid flickering
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies
                        launchSingleTop = true
                        // Restore state when reselecting
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF8B5CF6),
                selectedTextColor = Color(0xFF8B5CF6),
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = Color.Transparent
            )
        )

        // Fingerprint
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Fingerprint,
                    contentDescription = "Fingerprint",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = { Text("Fingerprint", fontSize = 12.sp) },
            selected = currentRoute == Screen.Fingerprint.route,
            onClick = {
                if (currentRoute != Screen.Fingerprint.route) {
                    navController.navigate(Screen.Fingerprint.route) {
                        // Clear back stack to avoid flickering
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies
                        launchSingleTop = true
                        // Restore state when reselecting
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF8B5CF6),
                selectedTextColor = Color(0xFF8B5CF6),
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = Color.Transparent
            )
        )

        // Profile
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = { Text("Profile", fontSize = 12.sp) },
            selected = currentRoute == Screen.Profile.route,
            onClick = {
                if (currentRoute != Screen.Profile.route) {
                    navController.navigate(Screen.Profile.route) {
                        // Clear back stack to avoid flickering
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies
                        launchSingleTop = true
                        // Restore state when reselecting
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF8B5CF6),
                selectedTextColor = Color(0xFF8B5CF6),
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = Color.Transparent
            )
        )
    }
}