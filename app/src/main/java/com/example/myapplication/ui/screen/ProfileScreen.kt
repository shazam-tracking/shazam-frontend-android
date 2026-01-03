package com.example.myapplication.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.myapplication.ui.components.SharedBottomNavBar
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profileState by viewModel.profileState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }

    Scaffold(
        bottomBar = {
            SharedBottomNavBar(
                navController = navController,
                currentRoute = Screen.Profile.route
            )
        },
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
                    // Empty space for symmetry
                    Box(modifier = Modifier.size(48.dp))

                    Text(
                        text = "Profile",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    IconButton(
                        onClick = {
                            navController.navigate(Screen.EditProfile.route)
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Profile Picture
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
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = profileState.userData?.fullName?.firstOrNull()?.uppercase() ?: "U",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Full Name Field
                ProfileInfoCard(
                    label = "Fullname",
                    value = profileState.userData?.fullName ?: "Loading...",
                    icon = Icons.Default.Person
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email Field
                ProfileInfoCard(
                    label = "Email",
                    value = profileState.userData?.email ?: "Loading...",
                    icon = Icons.Default.Email
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Field (masked)
                ProfileInfoCard(
                    label = "Password",
                    value = "••••••••",
                    icon = Icons.Default.Lock
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Sign Out Button
                Button(
                    onClick = {
                        viewModel.logout()
                        onLogout()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B5CF6)
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = "Sign out",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign out",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Loading Indicator
            if (profileState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF8B5CF6),
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading profile...",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Error Message
            profileState.error?.let { error ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFF5252).copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Error",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = error,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfoCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    color = Color(0xFF8B5CF6),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = value,
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}