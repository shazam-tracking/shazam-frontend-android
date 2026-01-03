package com.example.myapplication.ui.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Home : Screen("home")
    object Fingerprint : Screen("fingerprint")
    object Profile : Screen("profile")
}