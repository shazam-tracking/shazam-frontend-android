package com.example.myapplication.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Auth : Screen("auth")
    object Home : Screen("home")
    object Fingerprint : Screen("fingerprint")
    object Profile : Screen("profile")
}