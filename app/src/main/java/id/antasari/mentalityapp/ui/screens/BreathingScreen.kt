package id.antasari.mentalityapp.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import kotlinx.coroutines.delay

// Enum Fase Napas
enum class BreathPhase(val text: String, val duration: Int) {
    INHALE("Breathe In", 4000),
    HOLD("Hold", 4000),
    EXHALE("Breathe Out", 4000),
    REST("Rest", 2000),
    IDLE("Ready?", 0)
}

@Composable
fun BreathingScreen(navController: NavController) {
    // --- STATE ---
    var isPlaying by remember { mutableStateOf(false) }
    var soundOn by remember { mutableStateOf(true) }
    var currentPhase by remember { mutableStateOf(BreathPhase.IDLE) }
    var cycles by remember { mutableIntStateOf(0) }

    // Logic Timer
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            if (currentPhase == BreathPhase.IDLE) currentPhase = BreathPhase.INHALE
            while (isPlaying) {
                currentPhase = BreathPhase.INHALE
                delay(BreathPhase.INHALE.duration.toLong())
                if (!isPlaying) break
                currentPhase = BreathPhase.HOLD
                delay(BreathPhase.HOLD.duration.toLong())
                if (!isPlaying) break
                currentPhase = BreathPhase.EXHALE
                delay(BreathPhase.EXHALE.duration.toLong())
                if (!isPlaying) break
                currentPhase = BreathPhase.REST
                delay(BreathPhase.REST.duration.toLong())
                if (!isPlaying) break
                cycles++
            }
        } else {
            currentPhase = BreathPhase.IDLE
        }
    }

    // Animasi Scale
    val targetScale = when (currentPhase) {
        BreathPhase.INHALE, BreathPhase.HOLD -> 1.4f
        BreathPhase.EXHALE, BreathPhase.REST, BreathPhase.IDLE -> 0.8f
    }

    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(
            durationMillis = if (currentPhase == BreathPhase.IDLE) 500 else currentPhase.duration,
            easing = LinearEasing
        ),
        label = "ScaleAnim"
    )

    // --- WARNA DINAMIS (KEMBALI KE VERSI LAMA) ---
    // Warna lingkaran berubah sesuai fase (Biru -> Ungu -> Pink -> Hijau)
    val gradientColors = when (currentPhase) {
        BreathPhase.INHALE -> listOf(Color(0xFF90CAF9), Color(0xFF4DD0E1)) // Biru - Cyan
        BreathPhase.HOLD -> listOf(Color(0xFFCE93D8), Color(0xFFF48FB1))   // Ungu - Pink
        BreathPhase.EXHALE -> listOf(Color(0xFFF48FB1), Color(0xFFEF9A9A)) // Pink - Merah
        BreathPhase.REST -> listOf(Color(0xFFA5D6A7), Color(0xFF80CBC4))   // Hijau - Teal
        BreathPhase.IDLE -> listOf(Color(0xFFB0BEC5), Color(0xFF90A4AE))   // Abu - Abu
    }

    // Warna Gradient Tombol Start (Soft Purple-Pink)
    val buttonGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFCE93D8), Color(0xFFF48FB1))
    )

    // BACKGROUND UTAMA (Soft White-Blue)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFFFFF), Color(0xFFF3F8FF))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- 1. HEADER ---
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "breathing exercise",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PoppinsFamily,
                    color = Color(0xFF37474F)
                )
                Text(
                    text = "take a deep breath. inhale... exhale... let the drama go.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontFamily = PoppinsFamily,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- 2. LINGKARAN NAPAS (VERSI LAMA + PARTIKEL) ---
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(300.dp)
            ) {
                // Lingkaran Gradient Utama (Membesar/Mengecil)
                Box(
                    modifier = Modifier
                        .size(200.dp) // Base size
                        .scale(animatedScale) // Animasi Scale
                        .shadow(elevation = 20.dp, shape = CircleShape, spotColor = gradientColors[0])
                        .background(
                            brush = Brush.linearGradient(gradientColors),
                            shape = CircleShape
                        )
                        .border(4.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Inner Glow
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(listOf(Color.White.copy(alpha = 0.4f), Color.Transparent))
                            )
                    )

                    // Teks Tengah (Fase)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AnimatedContent(
                            targetState = currentPhase,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "TextAnim"
                        ) { phase ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isPlaying) phase.text.lowercase() else "ready?",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 14.sp,
                                    fontFamily = PoppinsFamily
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isPlaying) "••••" else "start",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = PoppinsFamily
                                )
                            }
                        }
                    }
                }

                // Partikel Melayang (Dummy animation)
                if (isPlaying) {
                    val particleY by rememberInfiniteTransition(label = "p").animateFloat(
                        initialValue = 0f, targetValue = -100f,
                        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart), label = "py"
                    )
                    Box(
                        modifier = Modifier
                            .offset(y = particleY.dp)
                            .size(6.dp)
                            .background(Color.White.copy(alpha = 0.6f), CircleShape)
                    )
                    // Tambahan partikel kedua biar lebih rame dikit
                    val particleY2 by rememberInfiniteTransition(label = "p2").animateFloat(
                        initialValue = 20f, targetValue = -80f,
                        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart), label = "py2"
                    )
                    Box(
                        modifier = Modifier
                            .offset(x = 40.dp, y = particleY2.dp)
                            .size(4.dp)
                            .background(Color.White.copy(alpha = 0.4f), CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // --- 3. STATS CARDS (Dua Kotak Kecil - Layout Baru) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatCardSmall(label = "cycles", value = "$cycles")
                Spacer(modifier = Modifier.width(16.dp))
                // Hitung durasi
                val totalSeconds = cycles * 14
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                StatCardSmall(label = "duration", value = String.format("%d:%02d", minutes, seconds))
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- 4. TOMBOL KONTROL (Soft Style) ---
            Button(
                onClick = { isPlaying = !isPlaying },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .shadow(10.dp, RoundedCornerShape(50), spotColor = Color(0xFFF48FB1).copy(alpha = 0.4f)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(50)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(buttonGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isPlaying) "pause" else "start breathing",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PoppinsFamily,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Sound (Putih Bersih)
            Button(
                onClick = { soundOn = !soundOn },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .shadow(4.dp, RoundedCornerShape(50), spotColor = Color.Black.copy(alpha = 0.05f)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(50)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (soundOn) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (soundOn) "sound on" else "sound off",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = PoppinsFamily
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// Komponen Card Kecil untuk Stats
@Composable
fun StatCardSmall(label: String, value: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.size(width = 100.dp, height = 70.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 10.sp, color = Color.Gray, fontFamily = PoppinsFamily)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF37474F), fontFamily = PoppinsFamily)
        }
    }
}