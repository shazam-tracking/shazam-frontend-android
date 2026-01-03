package com.example.myapplication.ui.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.viewmodel.MusicRecognitionViewModel
import com.example.myapplication.util.AudioRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun MusicRecognitionScreen(
    navController: NavController,
    viewModel: MusicRecognitionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.recognitionState.collectAsState()
    val scope = rememberCoroutineScope()

    var audioRecorder by remember { mutableStateOf<AudioRecorder?>(null) }
    var recordingDuration by remember { mutableStateOf(0) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scope.launch(Dispatchers.IO) {
                try {
                    audioRecorder = AudioRecorder(context)
                    val file = audioRecorder?.startRecording()

                    withContext(Dispatchers.Main) {
                        if (file != null) {
                            viewModel.startListening()
                            android.util.Log.d("MusicRecognition", "Recording started")
                        } else {
                            android.util.Log.e("MusicRecognition", "Failed to start recording")
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("MusicRecognition", "Error starting recording", e)
                }
            }
        }
    }

    // Recording timer
    LaunchedEffect(state.isListening) {
        if (state.isListening) {
            recordingDuration = 0
            while (state.isListening && recordingDuration < 10) {
                delay(1000)
                recordingDuration++
            }

            // Auto-stop after 10 seconds
            if (recordingDuration >= 10 && state.isListening) {
                scope.launch(Dispatchers.IO) {
                    try {
                        val file = audioRecorder?.stopRecording()


                        withContext(Dispatchers.Main) {
                            if (file != null && file.exists() && file.length() > 0) {
                                android.util.Log.d("MusicRecognition", "Auto-stop: ${file.length()} bytes")
                                viewModel.recognizeSong(file)
                            } else {
                                android.util.Log.e("MusicRecognition", "Invalid audio file")
                                viewModel.stopListening()
                            }
                        }

                        audioRecorder = null
                    } catch (e: Exception) {
                        android.util.Log.e("MusicRecognition", "Error in auto-stop", e)
                        withContext(Dispatchers.Main) {
                            viewModel.stopListening()
                        }
                    }
                }
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val ringScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Box(
        modifier = Modifier.fillMaxSize()
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
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier.size(280.dp),
                contentAlignment = Alignment.Center
            ) {

                // ===== ALWAYS SHOW Outer Rings (even when idle) =====
                Ring(
                    size = 440.dp,
                    strokeWidth = 2.dp,
                    scale = ringScale * 1.64f,
                    colors = listOf(Color(0xFFF50BA7), Color(0xFF842DC6), Color(0xFFF17140), Color(0xFFFFCC00)),
                    alpha = if (state.isListening || state.isProcessing) 0.85f else 0.3f,
                    offsetY = (-78).dp
                )

                Ring(
                    size = 380.dp,
                    strokeWidth = 2.dp,
                    scale = ringScale * 1.32f,
                    colors = listOf(Color(0xFFF50BA7), Color(0xFF842DC6), Color(0xFFF17140), Color(0xFFFFCC00)),
                    alpha = if (state.isListening || state.isProcessing) 0.7f else 0.25f,
                    offsetY = (-48).dp
                )

                Ring(
                    size = 320.dp,
                    strokeWidth = 2.dp,
                    scale = ringScale * 0.96f,
                    colors = listOf(Color(0xFFF50BA7), Color(0xFF842DC6), Color(0xFFF17140), Color(0xFFFFCC00)),
                    alpha = if (state.isListening || state.isProcessing) 0.55f else 0.2f,
                    offsetY = (-16).dp
                )

                // ===== Center Button =====
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clickable(enabled = !state.isProcessing) {
                            if (state.isListening) {
                                // Stop recording manually
                                scope.launch(Dispatchers.IO) {
                                    try {
                                        val file = audioRecorder?.stopRecording()


                                        withContext(Dispatchers.Main) {
                                            if (file != null && file.exists() && file.length() > 0) {
                                                android.util.Log.d("MusicRecognition", "Manual stop")
                                                viewModel.recognizeSong(file)
                                            } else {
                                                android.util.Log.e("MusicRecognition", "Invalid file")
                                                viewModel.stopListening()
                                            }
                                        }

                                        audioRecorder = null
                                    } catch (e: Exception) {
                                        android.util.Log.e("MusicRecognition", "Error", e)
                                        withContext(Dispatchers.Main) {
                                            viewModel.stopListening()
                                        }
                                    }
                                }
                            } else {
                                // Start recording
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        state.isProcessing -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(60.dp),
                                    color = Color(0xFFBB86FC),
                                    strokeWidth = 4.dp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Recognizing...",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        state.isListening -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AnimatedWaveform(isListening = true)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "${recordingDuration}s / 10s",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        else -> {
                            Text(
                                text = "Tap to Listen",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))

            // Status text
            Text(
                text = when {
                    state.isProcessing -> "Processing audio..."
                    state.isListening -> "Listening for music..."
                    state.recognitionResult?.match == true -> "Song Found! 🎵"
                    state.recognitionResult?.match == false -> "No match found"
                    state.errorMessage != null -> "Error occurred"
                    else -> "Tap to start listening"
                },
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))


            // Additional info
            Text(
                text = when {
                    state.isListening -> "Recording... Tap again to stop"
                    state.isProcessing -> "Analyzing audio fingerprint..."
                    state.recognitionResult != null -> state.recognitionResult?.data?.title ?: "Unknown"
                    else -> "Make sure your device can hear the sound clearly"
                },
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp
            )

            // Error message
            state.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = Color(0xFFCF6679),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }

        // Bottom Navigation - Use the one from RecognitionResult
        BottomNavigation(
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController,
            currentRoute = "home"
        )
    }
}

@Composable
fun Ring(
    size: Dp,
    strokeWidth: Dp,
    scale: Float,
    colors: List<Color>,
    alpha: Float,
    offsetY: Dp
) {
    Canvas(
        modifier = Modifier
            .size(size)
            .offset(y = offsetY)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .alpha(alpha)
    ) {
        drawCircle(
            brush = Brush.linearGradient(colors),
            style = Stroke(width = strokeWidth.toPx())
        )
    }
}

@Composable
fun AnimatedWaveform(
    isListening: Boolean,
    barCount: Int = 7
) {
    if (!isListening) return

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val barAnimations = List(barCount) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 500 + index * 90,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = ""
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        barAnimations.forEach { anim ->
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(26.dp * anim.value)
                    .background(
                        color = Color(0xFFFFD54F),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

// REMOVED: BottomNavigation and BottomNavItem - they're already in RecognitionResult.kt
