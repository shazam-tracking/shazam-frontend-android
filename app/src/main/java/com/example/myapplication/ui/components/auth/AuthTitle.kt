package com.example.myapplication.ui.component.auth

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Color

@Composable
fun AuthTitle(
    isSignUp: Boolean,
    showForgotPassword: Boolean,
    modifier: Modifier = Modifier
) {
    val text = when {
        showForgotPassword -> "Forgot\npassword?"
        isSignUp -> "Sign up"
        else -> "Sign in"
    }

    Text(
        text = text,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        letterSpacing = 0.5.sp,
        textAlign = if (showForgotPassword) TextAlign.Start else TextAlign.Center,
        lineHeight = 40.sp,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 48.dp)
    )
}
