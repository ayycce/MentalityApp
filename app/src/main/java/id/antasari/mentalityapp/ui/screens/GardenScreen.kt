package id.antasari.mentalityapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.antasari.mentalityapp.R
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import id.antasari.mentalityapp.ui.theme.SkyBlue
import id.antasari.mentalityapp.ui.theme.SoftNeonPink
import kotlinx.coroutines.delay

@Composable
fun GardenScreen() {
    // --- STATE GAME ---
    var currentXp by remember { mutableIntStateOf(20) }
    val maxXp = 100

    // State Animasi Air
    var isWatering by remember { mutableStateOf(false) }

    // Logic Animasi Air (Muncul sebentar lalu hilang)
    LaunchedEffect(isWatering) {
        if (isWatering) {
            delay(1500) // Air muncul selama 1.5 detik
            isWatering = false
        }
    }

    // Hitung Level
    val level = (currentXp / maxXp) + 1
    val progress = (currentXp % maxXp).toFloat() / maxXp.toFloat()

    // Animasi Progress Bar
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000),
        label = "Progress"
    )

    // LOGIKA GAMBAR
    val (plantImage, plantName) = when (level) {
        1 -> Pair(R.drawable.plant_stage_1, "Baby Seed 🌱")
        2 -> Pair(R.drawable.plant_stage_2, "Little Sprout 🌿")
        3 -> Pair(R.drawable.plant_stage_3, "Growing Bud 🌷")
        else -> Pair(R.drawable.plant_stage_4, "Majestic Flower 🌻")
    }

    // Brush untuk Card Streak
    val fireBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFAB91), Color(0xFFFF5722))
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // --- KONTEN SCROLLABLE (Agar tidak tertimpa) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MainGradient)
                .verticalScroll(rememberScrollState()) // <--- FIX LAYOUT: Scrollable
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // HEADER
            Text(
                text = "My Healing Garden",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PoppinsFamily,
                color = Color.Black.copy(alpha = 0.8f)
            )
            Text(
                text = "Grow yourself, grow your plant.",
                fontSize = 12.sp,
                color = Color.Gray,
                fontFamily = PoppinsFamily
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --- TANAMAN UTAMA ---
            Box(
                modifier = Modifier.size(300.dp),
                contentAlignment = Alignment.Center
            ) {
                // Glow Effect
                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.6f), SkyBlue.copy(alpha = 0.1f), Color.Transparent)
                            )
                        )
                )

                // ANIMASI EVOLUSI
                AnimatedContent(
                    targetState = level,
                    transitionSpec = {
                        (scaleIn(animationSpec = tween(1000)) + fadeIn(animationSpec = tween(1000)))
                            .togetherWith(scaleOut(animationSpec = tween(1000)) + fadeOut(animationSpec = tween(1000)))
                    },
                    label = "PlantEvolution"
                ) { targetLevel ->
                    val currentImageRes = when (targetLevel) {
                        1 -> R.drawable.plant_stage_1
                        2 -> R.drawable.plant_stage_2
                        3 -> R.drawable.plant_stage_3
                        else -> R.drawable.plant_stage_4
                    }
                    Image(
                        painter = painterResource(id = currentImageRes),
                        contentDescription = "Plant",
                        modifier = Modifier.size(220.dp)
                    )
                }

                // --- EFEK AIR (OVERLAY DI ATAS TANAMAN) ---
                // Muncul saat disiram
                androidx.compose.animation.AnimatedVisibility(
                    visible = isWatering,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { -50 }), // Jatuh dari atas
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { 50 }),
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    Text(
                        text = "💦   💦\n   💦", // Emoji air jatuh
                        fontSize = 40.sp,
                        modifier = Modifier.padding(top = 40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // INFO LEVEL
            AnimatedContent(targetState = plantName, label = "TextAnim") { name ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Level $level", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily)
                    Text(name, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black.copy(alpha = 0.8f), fontFamily = PoppinsFamily)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // XP PROGRESS BAR
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Growth Progress", fontSize = 12.sp, fontFamily = PoppinsFamily, color = Color.Gray)
                    Text("${currentXp % maxXp} / $maxXp XP", fontSize = 12.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(50)),
                    color = SkyBlue,
                    trackColor = Color.White.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- TOMBOL SIRAM ---
            Button(
                onClick = {
                    currentXp += 20
                    isWatering = true // Trigger Animasi Air
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(8.dp, RoundedCornerShape(50.dp), spotColor = SkyBlue.copy(alpha = 0.5f)),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                shape = RoundedCornerShape(50.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.WaterDrop, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Water Plant (+20 XP)", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- STREAK & REWARD DASHBOARD (Baru) ---
            Text(
                text = "Streak & Rewards",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PoppinsFamily,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Kartu Streak (Watering/Garden Theme)
            // Definisi Brush warna "Adem" (Teal/Tosca)
            val gardenStreakBrush = Brush.verticalGradient(
                colors = listOf(Color(0xFF4DB6AC), Color(0xFF009688)) // Hijau Tosca Segar
            )

            Card(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Watering Streak", fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                        // Emoji ganti jadi Shower/Watering
                        Text("5 Days", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF009688), fontFamily = PoppinsFamily)
                    }

                    // Visual Icon (Warna Tosca Adem)
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(brush = gardenStreakBrush, alpha = 0.15f), // Alpha transparan
                        contentAlignment = Alignment.Center
                    ) {
                        // Ganti icon api jadi Tetesan Air (WaterDrop)
                        Icon(
                            imageVector = Icons.Rounded.WaterDrop,
                            contentDescription = null,
                            tint = Color(0xFF009688), // Warna Icon Tosca
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Kartu Reward (Ice Freeze)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.AcUnit, null, tint = SkyBlue)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Next Reward: Streak Freeze ❄️", fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily)
                            Text("Reach 7 days streak to unlock", fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Progress Bar Reward
                    LinearProgressIndicator(
                        progress = { 0.7f }, // 5 dari 7 hari
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)),
                        color = SoftNeonPink,
                        trackColor = Color(0xFFEEEEEE),
                    )
                }
            }

            // Spacer Extra agar bisa scroll sampai mentok bawah
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}