package com.example.myapplication.ui.screen

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.ui.component.auth.AuthTitle
import com.example.myapplication.ui.components.auth.*
import com.example.myapplication.ui.viewmodel.AuthViewModel
import com.example.myapplication.util.GoogleSignInHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.delay

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()

    var isSignUp by remember { mutableStateOf(false) }
    var showForgotPassword by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val googleSignInHelper = remember { GoogleSignInHelper(context) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val idToken = googleSignInHelper.handleSignInResult(task)
                if (idToken != null) {
                    viewModel.googleSignIn(idToken)
                }
            } catch (e: ApiException) {
                android.util.Log.e("AuthScreen", "Google sign-in failed", e)
            }
        }
    }

    // Handle sign in success - navigate to home
    LaunchedEffect(authState.isSuccess, showForgotPassword) {
        if (authState.isSuccess && !showForgotPassword) {
            onAuthSuccess()
            viewModel.resetState()
        }
    }

    // Handle sign up success - switch to sign in mode
    LaunchedEffect(authState.isSignUpSuccess) {
        if (authState.isSignUpSuccess) {
            delay(2000)  // Show success message for 2 seconds
            isSignUp = false  // Switch to sign in mode
            email = ""
            password = ""
            confirmPassword = ""
            fullName = ""
            viewModel.resetState()
        }
    }

    // Handle forgot password success - switch back to sign in
    LaunchedEffect(authState.message, showForgotPassword) {
        if (authState.message != null && showForgotPassword) {
            delay(3000)
            showForgotPassword = false
            email = ""
            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0033))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (showForgotPassword) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AuthBackButton(
                        onClick = {
                            showForgotPassword = false
                            email = ""
                            viewModel.resetState()
                        }
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            AuthTitle(
                isSignUp = isSignUp,
                showForgotPassword = showForgotPassword
            )

            Spacer(modifier = Modifier.height(if (showForgotPassword) 24.dp else 32.dp))

            if (showForgotPassword) {
                ForgotPasswordSection(
                    email = email,
                    onEmailChange = { email = it },
                    isLoading = authState.isLoading,
                    onSendReset = {
                        if (email.isNotBlank()) {
                            viewModel.forgotPassword(email)
                        }
                    }
                )
            } else {
                AuthFormSection(
                    isSignUp = isSignUp,
                    email = email,
                    onEmailChange = { email = it },
                    password = password,
                    onPasswordChange = { password = it },
                    passwordVisible = passwordVisible,
                    onPasswordVisibleChange = { passwordVisible = it },
                    confirmPassword = confirmPassword,
                    onConfirmPasswordChange = { confirmPassword = it },
                    confirmPasswordVisible = confirmPasswordVisible,
                    onConfirmPasswordVisibleChange = { confirmPasswordVisible = it },
                    fullName = fullName,
                    onFullNameChange = { fullName = it },
                    isLoading = authState.isLoading,
                    onForgotPasswordClick = {
                        showForgotPassword = true
                        viewModel.resetState()
                    },
                    onSubmit = submit@{
                        if (isSignUp) {
                            if (password != confirmPassword) {
                                viewModel.setError("Passwords do not match")
                                return@submit
                            }
                            if (email.isBlank() || password.isBlank() || fullName.isBlank()) {
                                viewModel.setError("Please fill in all fields")
                                return@submit
                            }
                            viewModel.signUp(email, password, fullName)
                        } else {
                            if (email.isBlank() || password.isBlank()) {
                                viewModel.setError("Please fill in all fields")
                                return@submit
                            }
                            viewModel.signIn(email, password)
                        }
                    },
                    onGoogleClick = {
                        val intent = googleSignInHelper.getSignInIntent()
                        googleSignInLauncher.launch(intent)
                    },
                    onToggleMode = {
                        isSignUp = !isSignUp
                        email = ""
                        password = ""
                        confirmPassword = ""
                        fullName = ""
                        viewModel.resetState()
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (authState.error != null) {
                ErrorMessageCard(
                    message = authState.error.orEmpty(),
                    icon = Icons.Default.Warning,
                    backgroundColor = Color(0xFFFF5252).copy(alpha = 0.2f),
                    contentColor = Color(0xFFFF5252)
                )
            }

            if (authState.message != null) {
                ErrorMessageCard(
                    message = authState.message.orEmpty(),
                    icon = Icons.Default.CheckCircle,
                    backgroundColor = Color(0xFF4CAF50).copy(alpha = 0.2f),
                    contentColor = Color(0xFF4CAF50)
                )
            }
        }
    }
}