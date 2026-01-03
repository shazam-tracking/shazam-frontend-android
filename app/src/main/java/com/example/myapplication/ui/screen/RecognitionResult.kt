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
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.myapplication.ui.viewmodel.MusicRecognitionViewModel

@Composable
fun RecognitionResult(
    navController: NavController,
    recognitionData: RecognitionData? = null
) {
    val context = LocalContext.current
    val viewModel: MusicRecognitionViewModel = hiltViewModel()

    // Use recognitionData if available, otherwise show default/placeholder
    val songTitle = recognitionData?.title ?: "Blinding Lights"
    val artist = recognitionData?.artist ?: "The Weeknd"
    val album = recognitionData?.album ?: "The Highlights (Deluxe)"
    val imageUrl = recognitionData?.imageUrl ?: "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=600&h=600&fit=crop"
    val spotifyUrl = recognitionData?.trackUrl
    val releaseYear = recognitionData?.releaseYear ?: 2024
    val energy = recognitionData?.energy?.times(100)?.toInt() ?: 70
    val tempo = recognitionData?.tempo?.times(100)?.toInt() ?: 85
    val dancability = recognitionData?.dancability?.times(100)?.toInt() ?: 60
    val alternatives = recognitionData?.alternatives ?: emptyList()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF03002E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {
            // Status Bar
            StatusBar()

            // Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    // Reset state and go back to recording screen
                    viewModel.resetState()
                    navController.navigate(com.example.myapplication.ui.navigation.Screen.Home.route) {
                        popUpTo(com.example.myapplication.ui.navigation.Screen.Home.route) { inclusive = true }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
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
                } else {
                    // Show similar songs placeholder if no alternatives
                    SimilarSongsSection()
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

        // Bottom Navigation
        BottomNavigation(
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController,
            currentRoute = "home"
        )
    }
}

@Composable
fun StatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "9:41",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Signal bars
            repeat(3) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(12.dp)
                        .background(Color.White, RoundedCornerShape(2.dp))
                )
            }
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(12.dp)
                    .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
fun HeroImageCard(
    title: String,
    artist: String,
    album: String,
    imageUrl: String,
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
        AsyncImage(
            model = imageUrl,
            contentDescription = "Album Art",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

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
                        containerColor = Color(0xFF8B4D6B)
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

            // Latest Release Card
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
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Album",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Other Possible Matches",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${alternatives.size} found",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        alternatives.forEach { song ->
            AlternativeSongItem(
                title = song.title,
                artist = song.artist,
                imageUrl = song.imageUrl ?: "",
                score = song.score,
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
    imageUrl: String,
    score: Int,
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
        AsyncImage(
            model = imageUrl.ifEmpty { "https://via.placeholder.com/48" },
            contentDescription = title,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

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

        Spacer(modifier = Modifier.width(8.dp))

        // Score badge
        Box(
            modifier = Modifier
                .background(
                    Color(0xFFBB86FC).copy(alpha = 0.3f),
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "Score: $score",
                color = Color(0xFFBB86FC),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SimilarSongsSection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Similar songs",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "More",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        val songs = listOf(
            Triple("Blinding lights", "The Weeknd", "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=100&h=100&fit=crop"),
            Triple("Creepin'", "Metro Boomin - The Weeknd", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=100&h=100&fit=crop"),
            Triple("Take My Breath", "The Weeknd", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=100&h=100&fit=crop")
        )

        songs.forEach { (title, artist, imageUrl) ->
            SongItem(title, artist, imageUrl)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun SongItem(title: String, artist: String, imageUrl: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = artist,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More",
                tint = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun AudioFeaturesSection(
    energy: Int = 70,
    tempo: Int = 85,
    dancability: Int = 60
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF0A0033).copy(alpha = 0.5f),
                RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Audio Features",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "More",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "• Audio analysis",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val features = listOf(
                    "Energy" to energy,
                    "Tempo" to tempo,
                    "Happiness" to 10,
                    "Vocals" to 45,
                    "Danceabil..." to dancability
                )

                features.forEach { (label, value) ->
                    AudioFeatureBar(label, value)
                }
            }
        }
    }
}


@Composable
fun AudioFeatureBar(label: String, value: Int) {
    Column(
        modifier = Modifier.width(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(120.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(value / 100f)
                    .background(Color(0xFFB388FF), RoundedCornerShape(20.dp))
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        Text(
            text = "$value%",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun BottomNavigation(
    modifier: Modifier = Modifier,
    navController: NavController,
    currentRoute: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0033).copy(alpha = 0.95f))
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        BottomNavItem(
            label = "Home",
            icon = Icons.Default.Home,
            isSelected = currentRoute == "home",
            onClick = {
                navController.navigate(com.example.myapplication.ui.navigation.Screen.Home.route) {
                    popUpTo(com.example.myapplication.ui.navigation.Screen.Home.route) { inclusive = true }
                }
            }
        )
        BottomNavItem(
            label = "Fingerprint",
            icon = Icons.Default.Fingerprint,
            isSelected = currentRoute == "fingerprint",
            onClick = {
                navController.navigate(com.example.myapplication.ui.navigation.Screen.Fingerprint.route)
            }
        )
        BottomNavItem(
            label = "Profile",
            icon = Icons.Default.Person,
            isSelected = currentRoute == "profile",
            onClick = {
                navController.navigate(com.example.myapplication.ui.navigation.Screen.Profile.route)
            }
        )
    }
}

@Composable
fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
