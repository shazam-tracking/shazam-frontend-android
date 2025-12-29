package com.example.myapplication.ui.screen
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.navigation.NavController

@Composable
fun MusicRecognitionScreen(navController: NavController) {

    var isListening by remember { mutableStateOf(false) }

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
        modifier = Modifier
            .fillMaxWidth()
            .height(700.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0033),
                        Color(0xFF05001A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Box(
                modifier = Modifier.size(280.dp),
                contentAlignment = Alignment.Center
            ) {

                // ===== Outer Rings =====
                Ring(
                    size = 440.dp,
                    strokeWidth = 2.dp,
                    scale = ringScale * 1.64f,
                    colors = listOf(Color(0xFFF50BA7), Color(0xFF842DC6), Color(0xFFF17140), Color(0xFFFFCC00)),
                    alpha = 0.85f,
                    offsetY = (-78).dp
                )

                Ring(
                    size = 380.dp,
                    strokeWidth = 2.dp,
                    scale = ringScale * 1.32f,
                    colors = listOf(Color(0xFFF50BA7), Color(0xFF842DC6), Color(0xFFF17140), Color(0xFFFFCC00)),
                    alpha = 0.7f,
                    offsetY = (-48).dp
                )

                Ring(
                    size = 320.dp,
                    strokeWidth = 2.dp,
                    scale = ringScale * 0.96f,
                    colors = listOf(Color(0xFFF50BA7), Color(0xFF842DC6), Color(0xFFF17140), Color(0xFFFFCC00)),
                    alpha = 0.55f,
                    offsetY = (-16).dp
                )


                // ===== Center Button =====
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clickable { isListening = !isListening },
                    contentAlignment = Alignment.Center
                ) {
                    if (isListening) {
                        AnimatedWaveform(isListening = true)
                    } else {
                        Text(
                            text = "Tap to Listen",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

            }

            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = "Listening for music...",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Make sure your device hear the sound clearly",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
        }
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