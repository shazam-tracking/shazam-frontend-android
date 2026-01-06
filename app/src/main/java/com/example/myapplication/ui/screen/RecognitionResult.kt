package com.example.myapplication.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.model.AlternativeSong
import com.example.myapplication.data.model.RecognitionData
import com.example.myapplication.ui.components.SharedBottomNavBar
import com.example.myapplication.ui.viewmodel.MusicRecognitionViewModel

@Composable
fun RecognitionResult(
    navController: NavController,
    recognitionData: RecognitionData? = null
) {
    val context = LocalContext.current
    val viewModel: MusicRecognitionViewModel = hiltViewModel()

    val songTitle = recognitionData?.title ?: "Unknown"
    val artist = recognitionData?.artist ?: "Unknown Artist"
    val album = recognitionData?.album ?: "Unknown Album"
    val imageUrl = recognitionData?.imageUrl
    val spotifyUrl = recognitionData?.trackUrl
    val releaseYear = recognitionData?.releaseYear ?: 2024
    val energy = recognitionData?.energy?.times(100)?.toInt() ?: 0
    val tempo = recognitionData?.tempo?.times(100)?.toInt() ?: 0
    val dancability = recognitionData?.dancability?.times(100)?.toInt() ?: 0
    val alternatives = recognitionData?.alternatives ?: emptyList()

    Scaffold(
        bottomBar = {
            SharedBottomNavBar(
                navController = navController as androidx.navigation.NavHostController,
                currentRoute = com.example.myapplication.ui.navigation.Screen.Home.route
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
            ) {
                // Back Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            viewModel.resetState()
                            navController.navigate(com.example.myapplication.ui.navigation.Screen.Home.route) {
                                popUpTo(com.example.myapplication.ui.navigation.Screen.Home.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF8B5CF6), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Recognition Result",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Main Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Hero Image Card
                    HeroImageCard(
                        title = songTitle,
                        artist = artist,
                        album = album,
                        imageUrl = imageUrl,
                        releaseYear = releaseYear,
                        spotifyUrl = spotifyUrl,
                        onSpotifyClick = {
                            spotifyUrl?.let { url ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Alternative Songs Section (if available)
                    if (alternatives.isNotEmpty()) {
                        AlternativeSongsSection(alternatives, context)
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Audio Features Section
                    AudioFeaturesSection(
                        energy = energy,
                        tempo = tempo,
                        dancability = dancability
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun HeroImageCard(
    title: String,
    artist: String,
    album: String,
    imageUrl: String?,
    releaseYear: Int,
    spotifyUrl: String?,
    onSpotifyClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(480.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        // Background Image
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Album Art",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1A1A2E))
            )
        }

        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.2f),
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )

        // Content Overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = artist,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Open in Spotify Button
            if (spotifyUrl != null) {
                Button(
                    onClick = onSpotifyClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B5CF6)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "Open in Spotify",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF1DB954), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Spotify",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Album Info Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Album",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFF1A1A2E), RoundedCornerShape(8.dp))
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "ALBUM",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 10.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = album,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "Album • $releaseYear",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AlternativeSongsSection(
    alternatives: List<AlternativeSong>,
    context: android.content.Context
) {
    Column {
        Text(
            text = "Other Possible Matches",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        alternatives.forEach { song ->
            AlternativeSongItem(
                title = song.title,
                artist = song.artist,
                imageUrl = song.imageUrl,
                trackUrl = song.trackUrl,
                context = context
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun AlternativeSongItem(
    title: String,
    artist: String,
    imageUrl: String?,
    trackUrl: String?,
    context: android.content.Context
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White.copy(alpha = 0.05f),
                RoundedCornerShape(12.dp)
            )
            .clickable {
                trackUrl?.let { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF1A1A2E), RoundedCornerShape(8.dp))
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Text(
                text = artist,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp),
                maxLines = 1
            )
        }
    }
}

@Composable
fun AudioFeaturesSection(
    energy: Int,
    tempo: Int,
    dancability: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF1A1A2E).copy(alpha = 0.5f),
                RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "Audio Features",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                AudioFeatureBar("Energy", energy)
                AudioFeatureBar("Tempo", tempo)
                AudioFeatureBar("Danceability", dancability)
            }
        }
    }
}

@Composable
fun AudioFeatureBar(label: String, value: Int) {
    Column(
        modifier = Modifier.width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(120.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight((value.coerceIn(0, 100)) / 100f)
                    .background(
                        color = Color(0xFF8B5CF6),  // Solid color
                        shape = RoundedCornerShape(25.dp)
                    )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "$value%",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}