package com.example.myapplication.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.local.OnboardingPreferences

data class OnboardingModel(
    val title: String,
    val description: String,
    val imageUrl: String
)

class OnboardingViewModel(context: Context) : ViewModel() {

    private val onboardingPreferences = OnboardingPreferences(context)

    private val _pages = listOf(
        OnboardingModel(
            title = "Every Song Has a Fingerprint",
            description = "We generate unique audio signatures to identify any track instantly.",
            imageUrl = "https://images.unsplash.com/photo-1619983081563-430f63602796?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        ),
        OnboardingModel(
            title = "Crystal Clear in the Chaos",
            description = "Our algorithm is built to cut through background noise, ensuring high accuracy even in loud environments.",
            imageUrl = "https://images.unsplash.com/photo-1531651008558-ed1740375b39?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        ),
        OnboardingModel(
            title = "Just a Snippet Needed",
            description = "Don't wait for the chorus. Get an exact match from even the shortest audio clips.",
            imageUrl = "https://plus.unsplash.com/premium_photo-1681335986095-5a9585e77246?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        )
    )
    val pages: List<OnboardingModel> = _pages

    fun saveOnboardingCompleted() {
        onboardingPreferences.saveOnboardingCompleted()
    }
}
