package id.antasari.mentalityapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.graphicsLayer

// --- 1. DATA MODEL (GLOBAL DI FILE INI) ---
data class SoundTrack(val id: String, val title: String, val category: String, val color: Color, val icon: androidx.compose.ui.graphics.vector.ImageVector)

// List Lagu Statis
val soundLibrary = listOf(
    SoundTrack("1", "Rain on Window", "Nature", Color(0xFF64B5F6), Icons.Rounded.WaterDrop),
    SoundTrack("2", "Forest Creek", "Nature", Color(0xFF81C784), Icons.Rounded.Forest),
    SoundTrack("3", "Night Ambience", "Sleep", Color(0xFF9575CD), Icons.Rounded.Bedtime),
    SoundTrack("4", "Coffee Shop", "Focus", Color(0xFFFFB74D), Icons.Rounded.Coffee),
    SoundTrack("5", "Brown Noise", "Focus", Color(0xFFA1887F), Icons.Rounded.GraphicEq),
    SoundTrack("6", "Ocean Waves", "Relax", Color(0xFF4DD0E1), Icons.Rounded.Waves)
)

// --- 2. SCREEN 1: LIBRARY (DAFTAR PLAYLIST) ---
@Composable
fun SoundLibraryScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A237E), Color(0xFF0D1238))))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Spacer(modifier = Modifier.height(20.dp))

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.background(Color.White.copy(0.1f), CircleShape).size(40.dp)
                ) {
                    Icon(Icons.Rounded.ArrowBack, null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Soundscape", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Choose your vibe", fontSize = 16.sp, color = Color.White.copy(0.7f), fontFamily = PoppinsFamily)
            Spacer(modifier = Modifier.height(16.dp))

            // Grid Playlist
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(soundLibrary) { track ->
                    SoundCard(track = track) {
                        // Navigasi ke Player dengan ID lagu
                        navController.navigate("sound_player/${track.id}")
                    }
                }
            }
        }
    }
}

// Komponen Card di Library
@Composable
fun SoundCard(track: SoundTrack, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = track.color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(track.color.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(track.icon, null, tint = Color.White, modifier = Modifier.size(24.dp))
            }

            Column {
                Text(track.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = PoppinsFamily, color = Color.White)
                Text(track.category, fontSize = 12.sp, fontFamily = PoppinsFamily, color = Color.White.copy(0.7f))
            }
        }
    }
}

// --- 3. SCREEN 2: PLAYER (PEMUTAR MUSIK) ---
@Composable
fun SoundPlayerScreen(navController: NavController, trackId: String) {
    // Cari data lagu berdasarkan ID
    val track = soundLibrary.find { it.id == trackId } ?: soundLibrary[0]

    var isPlaying by remember { mutableStateOf(false) } // Default mati dulu biar user siap
    var playbackSeconds by remember { mutableIntStateOf(0) }

    // Auto Play saat masuk (Opsional, kalau mau langsung bunyi ubah true)
    LaunchedEffect(Unit) {
        isPlaying = true
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying) {
                delay(1000)
                playbackSeconds++
            }
        }
    }

    val formattedTime = remember(playbackSeconds) {
        val min = playbackSeconds / 60
        val sec = playbackSeconds % 60
        String.format("%02d:%02d", min, sec)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A237E), Color(0xFF0D1238))))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Header Player
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.background(Color.White.copy(0.1f), CircleShape)
                ) {
                    Icon(Icons.Rounded.KeyboardArrowDown, null, tint = Color.White) // Icon panah bawah (Minimize)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("Now Playing", color = Color.White.copy(0.6f), fontFamily = PoppinsFamily, fontSize = 12.sp)
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(40.dp)) // Dummy spacer biar tengah
            }

            Spacer(modifier = Modifier.weight(1f))

            // Artwork Besar
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(track.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(track.icon, null, tint = track.color, modifier = Modifier.size(100.dp))

                // Visualizer effect di belakang icon
                if (isPlaying) {
                    CircularVisualizer(color = track.color)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Judul & Info
            Text(track.title, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = PoppinsFamily)
            Text(track.category, fontSize = 16.sp, color = Color.White.copy(0.6f), fontFamily = PoppinsFamily)

            Spacer(modifier = Modifier.height(32.dp))

            // Timer
            Text(formattedTime, fontSize = 16.sp, color = Color.White.copy(0.8f), fontFamily = PoppinsFamily)

            Spacer(modifier = Modifier.height(24.dp))

            // Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Prev (Dummy)
                Icon(Icons.Rounded.SkipPrevious, null, tint = Color.White.copy(0.5f), modifier = Modifier.size(32.dp))

                // Play/Pause Besar
                IconButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier
                        .size(80.dp)
                        .background(track.color, CircleShape)
                ) {
                    Icon(
                        if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        null,
                        tint = Color.Black.copy(0.8f),
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Next (Dummy)
                Icon(Icons.Rounded.SkipNext, null, tint = Color.White.copy(0.5f), modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// Efek Visualizer Melingkar
@Composable
fun CircularVisualizer(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "circle")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "s"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "a"
    )

    Box(
        modifier = Modifier
            .size(200.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .border(2.dp, color, CircleShape)
    )
}