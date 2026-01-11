package id.antasari.mentalityapp.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.antasari.mentalityapp.ui.theme.PoppinsFamily

// Data Palette Warna
data class ColorMood(val name: String, val color1: Color, val color2: Color)

@Composable
fun ColorTherapyScreen(navController: NavController) {
    // Pilihan Mood Warna
    val moods = listOf(
        ColorMood("Ocean Calm", Color(0xFFE3F2FD), Color(0xFF2196F3)),
        ColorMood("Sunset Warmth", Color(0xFFFFF3E0), Color(0xFFFF9800)),
        ColorMood("Forest Peace", Color(0xFFE8F5E9), Color(0xFF4CAF50)),
        ColorMood("Lavender Dream", Color(0xFFF3E5F5), Color(0xFF9C27B0)),
        ColorMood("Midnight Quiet", Color(0xFF263238), Color(0xFF000000))
    )

    var currentMoodIndex by remember { mutableIntStateOf(0) }
    val currentMood = moods[currentMoodIndex]

    // Animasi Perubahan Warna (Background)
    val animatedColor1 by animateColorAsState(targetValue = currentMood.color1, animationSpec = tween(2000), label = "c1")
    val animatedColor2 by animateColorAsState(targetValue = currentMood.color2, animationSpec = tween(2000), label = "c2")

    // Animasi "Breathing" (Terang-Gelap Perlahan)
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(animatedColor1, animatedColor2)))
            .clickable {
                // Ganti mood saat layar disentuh
                currentMoodIndex = (currentMoodIndex + 1) % moods.size
            }
    ) {
        // Tombol Close (Pojok Kanan Atas)
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
                .background(Color.White.copy(0.2f), CircleShape)
        ) {
            Icon(Icons.Rounded.Close, null, tint = Color.White)
        }

        // Konten Tengah
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Lingkaran "Fokus" yang bernapas
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .alpha(alphaAnim) // Efek kedip pelan
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .blur(50.dp) // Efek Glow Blur
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Teks Nama Mood
            Text(
                text = currentMood.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PoppinsFamily,
                color = if (currentMoodIndex == 4) Color.White.copy(0.9f) else Color(0xFF37474F).copy(0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Instruksi Kecil
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.TouchApp,
                    null,
                    tint = if (currentMoodIndex == 4) Color.Gray else Color.Gray.copy(0.6f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Tap gently to change the atmosphere",
                    fontSize = 12.sp,
                    color = if (currentMoodIndex == 4) Color.Gray else Color.Gray.copy(0.6f),
                    fontFamily = PoppinsFamily
                )
            }
        }
    }
}