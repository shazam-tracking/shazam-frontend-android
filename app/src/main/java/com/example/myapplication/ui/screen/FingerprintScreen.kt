package com.example.myapplication.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.myapplication.ui.components.SharedBottomNavBar
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.viewmodel.FingerprintViewModel
import kotlinx.coroutines.delay

@Composable
fun FingerprintScreen(
    navController: NavHostController,
    viewModel: FingerprintViewModel = hiltViewModel()
) {
    var spotifyUrl by remember { mutableStateOf("") }
    val fingerprintState by viewModel.fingerprintState.collectAsState()

    // Animated fingerprint
    val infiniteTransition = rememberInfiniteTransition(label = "fingerprint")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Auto-hide success message after 3 seconds
    LaunchedEffect(fingerprintState.isSuccess) {
        if (fingerprintState.isSuccess) {
            delay(3000)
            spotifyUrl = ""
            viewModel.resetState()
        }
    }

    Scaffold(
        bottomBar = {
            SharedBottomNavBar(
                navController = navController,
                currentRoute = Screen.Fingerprint.route
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Title
                Text(
                    text = "New song",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Spotify URL Input
                OutlinedTextField(
                    value = spotifyUrl,
                    onValueChange = { spotifyUrl = it },
                    placeholder = {
                        Text(
                            "http://open.spotify.com/...",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = Color(0xFF8B5CF6),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledBorderColor = Color.White.copy(alpha = 0.2f),
                        disabledTextColor = Color.White.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(28.dp),
                    singleLine = true,
                    enabled = !fingerprintState.isIndexing
                )

                Spacer(modifier = Modifier.weight(1f))

                // Gradient Fingerprint Icon (No Rings)
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(if (fingerprintState.isIndexing) scale else 1f)
                        .clickable(
                            enabled = !fingerprintState.isIndexing && spotifyUrl.isNotBlank(),
                            onClick = {
                                viewModel.indexSongFromSpotify(spotifyUrl)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Fingerprint",
                        modifier = Modifier
                            .size(180.dp)
                            .graphicsLayer(alpha = 0.99f)
                            .drawWithCache {
                                val gradient = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFE91E63),
                                        Color(0xFF9C27B0),
                                        Color(0xFFF17140),
                                        Color(0xFFFFCC00)
                                    )
                                )
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(gradient, blendMode = BlendMode.SrcAtop)
                                }
                            },
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Description
                Text(
                    text = when {
                        fingerprintState.isIndexing -> "Indexing song..."
                        else -> "Touch here to generate music\nfingerprint"
                    },
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                // Success Message
                AnimatedVisibility(
                    visible = fingerprintState.isSuccess,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF8B5CF6)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = fingerprintState.successMessage ?: "Song added successfully",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Error Message
                AnimatedVisibility(
                    visible = fingerprintState.error != null,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFF5252).copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = Color(0xFFFF5252),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = fingerprintState.error ?: "An error occurred",
                                color = Color(0xFFFF5252),
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}