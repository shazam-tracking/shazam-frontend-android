package com.example.myapplication.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.myapplication.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay

@Composable
fun EditProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val profileState by viewModel.profileState.collectAsState()
    val editState by viewModel.editState.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    // Load current profile data
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }

    // Initialize with current data when profile loads
    LaunchedEffect(profileState.userData) {
        profileState.userData?.let {
            fullName = it.fullName
            android.util.Log.d("EditProfileScreen", "Loaded profile - Name: ${it.fullName}")
        }
    }

    // Auto-dismiss and navigate back on success
    LaunchedEffect(editState.isSuccess) {
        if (editState.isSuccess) {
            delay(2000)
            navController.popBackStack()
            viewModel.resetEditState()
        }
    }

    Scaffold(
        containerColor = Color(0xFF0A0033)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0A0033),
                                Color(0xFF05001A)
                            )
                        )
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF8B5CF6), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "Edit Profile",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Box(modifier = Modifier.size(48.dp))
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Profile Picture (Read-only - show current picture)
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .border(
                            width = 3.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFE91E63),
                                    Color(0xFF9C27B0),
                                    Color(0xFFF17140),
                                    Color(0xFFFFCC00)
                                )
                            ),
                            shape = CircleShape
                        )
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (profileState.userData?.profilePictureUrl != null) {
                        AsyncImage(
                            model = profileState.userData?.profilePictureUrl,
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = fullName.firstOrNull()?.uppercase() ?: "U",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Full Name
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name", color = Color.White.copy(alpha = 0.7f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Person, null, tint = Color.White.copy(alpha = 0.7f))
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedLabelColor = Color(0xFF8B5CF6),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        cursorColor = Color(0xFF8B5CF6),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Text(
                    text = "Change Password (Optional)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                // Current Password
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Current Password", color = Color.White.copy(alpha = 0.7f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, null, tint = Color.White.copy(alpha = 0.7f))
                    },
                    trailingIcon = {
                        IconButton(onClick = { showCurrentPassword = !showCurrentPassword }) {
                            Icon(
                                if (showCurrentPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                null,
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    },
                    visualTransformation = if (showCurrentPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedLabelColor = Color(0xFF8B5CF6),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        cursorColor = Color(0xFF8B5CF6),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // New Password
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password", color = Color.White.copy(alpha = 0.7f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, null, tint = Color.White.copy(alpha = 0.7f))
                    },
                    trailingIcon = {
                        IconButton(onClick = { showNewPassword = !showNewPassword }) {
                            Icon(
                                if (showNewPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                null,
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    },
                    visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedLabelColor = Color(0xFF8B5CF6),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        cursorColor = Color(0xFF8B5CF6),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Confirm Password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password", color = Color.White.copy(alpha = 0.7f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, null, tint = Color.White.copy(alpha = 0.7f))
                    },
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                null,
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    },
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedLabelColor = Color(0xFF8B5CF6),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        cursorColor = Color(0xFF8B5CF6),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Save Button
                Button(
                    onClick = {
                        // Validate passwords match
                        if (newPassword.isNotEmpty() && newPassword != confirmPassword) {
                            viewModel.setEditError("Passwords do not match")
                            return@Button
                        }

                        // Update profile (without image)
                        viewModel.updateProfile(
                            context = context,
                            fullName = fullName,
                            currentPassword = currentPassword.ifEmpty { null },
                            newPassword = newPassword.ifEmpty { null },
                            profilePictureUri = null // No image upload
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !editState.isLoading && fullName.isNotBlank()
                ) {
                    if (editState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Check, "Save", tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(Modifier.height(24.dp))
            }

            // Loading overlay while fetching profile
            if (profileState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF8B5CF6))
                }
            }

            // Success Message
            AnimatedVisibility(
                visible = editState.isSuccess,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, "Success", tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Profile updated successfully!", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Error Message
            AnimatedVisibility(
                visible = editState.error != null,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF5252).copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, "Error", tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(editState.error ?: "Error", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}