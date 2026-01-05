package com.example.myapplication.data.local

import android.content.Context
import android.content.SharedPreferences

class OnboardingPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val ONBOARDING_COMPLETED = "onboarding_completed"
    }

    fun saveOnboardingCompleted() {
        sharedPreferences.edit().putBoolean(ONBOARDING_COMPLETED, true).apply()
    }

    fun isOnboardingCompleted(): Boolean {
        return sharedPreferences.getBoolean(ONBOARDING_COMPLETED, false)
    }

    // Optional: For testing purposes
    fun resetOnboarding() {
        sharedPreferences.edit().putBoolean(ONBOARDING_COMPLETED, false).apply()
    }
}