package id.antasari.mentalityapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.antasari.mentalityapp.R
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import id.antasari.mentalityapp.ui.theme.SkyBlue
import id.antasari.mentalityapp.ui.theme.SoftNeonPink
import id.antasari.mentalityapp.ui.viewmodel.GardenViewModel
import kotlinx.coroutines.delay

@Composable
fun GardenScreen(
    viewModel: GardenViewModel = viewModel()
) {
    val gardenState by viewModel.gardenState.collectAsState()
    val weeklyData by viewModel.weeklyStats.collectAsState()

    val currentXp = gardenState.currentXp
    val waterTokens = gardenState.waterTokens
    val level = gardenState.level

    val maxXp = 100
    val progress = (currentXp % maxXp).toFloat() / maxXp.toFloat()

    // State Animasi & Dialog
    var isWatering by remember { mutableStateOf(false) }
    var isTouched by remember { mutableStateOf(false) }
    var showStreakDetail by remember { mutableStateOf(false) }
    var showWaterInfo by remember { mutableStateOf(false) }

    // Animasi Denyut saat disentuh
    val scaleAnim by animateFloatAsState(
        targetValue = if (isTouched) 1.1f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "PlantPulse"
    )

    LaunchedEffect(isWatering) {
        if (isWatering) { delay(2000); isWatering = false }
    }
    LaunchedEffect(isTouched) {
        if (isTouched) { delay(300); isTouched = false }
    }

    // Nama Stage (Untuk Teks di bawah)
    val stageName = when {
        level <= 1 -> "Seedling"
        level == 2 -> "Sprout 🌱"
        level == 3 -> "Budding 🌿"
        else -> "Blooming 🌻"
    }

    val sageGreen = Color(0xFFAED581)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MainGradient)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text("Healing Garden", fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily, color = Color(0xFF37474F))
            Text("This grows when you show up. No rush.", fontSize = 14.sp, color = Color.Gray, fontFamily = PoppinsFamily)

            Spacer(modifier = Modifier.height(60.dp))

            // ==========================================
            // 🔥 VISUAL TANAMAN (DENGAN ANIMASI EVOLUSI) 🔥
            // ==========================================
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { isTouched = true },
                contentAlignment = Alignment.Center
            ) {
                // Background Glow
                Box(modifier = Modifier.size(220.dp).background(Brush.radialGradient(colors = listOf(Color.White.copy(alpha = 0.8f), sageGreen.copy(alpha = 0.2f), Color.Transparent))))

                // 🔥 ANIMASI TRANSISI LEVEL 🔥
                AnimatedContent(
                    targetState = level,
                    transitionSpec = {
                        // Masuk: Membesar + Fade In (Pelan 1 detik)
                        (scaleIn(animationSpec = tween(1000)) + fadeIn(animationSpec = tween(1000)))
                            .togetherWith(
                                // Keluar: Mengecil + Fade Out
                                scaleOut(animationSpec = tween(1000)) + fadeOut(animationSpec = tween(1000))
                            )
                    },
                    label = "PlantEvolution"
                ) { targetLevel ->
                    // Tentukan Gambar berdasarkan Level Target
                    val currentImageRes = when {
                        targetLevel <= 1 -> R.drawable.plant_stage_1
                        targetLevel == 2 -> R.drawable.plant_stage_2
                        targetLevel == 3 -> R.drawable.plant_stage_3
                        else -> R.drawable.plant_stage_4
                    }

                    Image(
                        painter = painterResource(id = currentImageRes),
                        contentDescription = "My Plant",
                        // Gabungkan ukuran asli dengan animasi scale saat disentuh
                        modifier = Modifier
                            .size(240.dp)
                            .scale(scaleAnim)
                    )
                }

                // Feedback Sentuh (Pop-up text)
                androidx.compose.animation.AnimatedVisibility(
                    visible = isTouched,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier.align(Alignment.TopCenter).offset(y = (-40).dp)
                ) {
                    Box(modifier = Modifier.background(Color.White, RoundedCornerShape(12.dp)).border(1.dp, SoftNeonPink.copy(0.3f), RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text("Growing with you ❤️", fontSize = 10.sp, color = SoftNeonPink, fontFamily = PoppinsFamily)
                    }
                }

                // Animasi Air
                androidx.compose.animation.AnimatedVisibility(
                    visible = isWatering,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { -150 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { 150 }),
                    modifier = Modifier.align(Alignment.Center).offset(y = (-60).dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💦", fontSize = 40.sp)
                        Row {
                            Text("💦", fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("💦", fontSize = 32.sp)
                        }
                        Text("💦", fontSize = 36.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // PROGRESS BAR
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Animasi Text Nama Stage juga biar smooth (Opsional, tapi bagus)
                AnimatedContent(targetState = stageName, label = "StageNameAnim") { name ->
                    Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F), fontFamily = PoppinsFamily)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.width(200.dp)) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)),
                        color = sageGreen,
                        trackColor = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Growth Progress", fontSize = 10.sp, color = Color.Gray, fontFamily = PoppinsFamily)
            }

            Spacer(modifier = Modifier.height(50.dp))

            // TOMBOL WATER
            Button(
                onClick = {
                    if (waterTokens > 0) {
                        viewModel.waterPlant()
                        isWatering = true
                    }
                },
                enabled = waterTokens > 0,
                modifier = Modifier.height(56.dp).width(180.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue, disabledContainerColor = Color.LightGray),
                shape = RoundedCornerShape(50),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.WaterDrop, null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    if (waterTokens > 0) {
                        Text("Water ($waterTokens)", fontSize = 16.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold)
                    } else {
                        Text("No Water Left", fontSize = 14.sp, fontFamily = PoppinsFamily)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // INFO AIR
            if (waterTokens == 0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Your plant is resting today. 🌱", fontSize = 12.sp, color = Color(0xFF37474F), fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showWaterInfo = true }.padding(4.dp)
                    ) {
                        Text("How to get more water?", fontSize = 10.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Rounded.Info, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showWaterInfo = true }.padding(4.dp)
                ) {
                    Text("How to get water", fontSize = 10.sp, color = Color.Gray.copy(0.6f), fontFamily = PoppinsFamily)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Rounded.Info, null, tint = Color.Gray.copy(0.6f), modifier = Modifier.size(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // STREAK CARD
            Card(
                modifier = Modifier.fillMaxWidth().clickable { showStreakDetail = true },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFF1F8E9)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.CalendarToday, null, tint = sageGreen)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Days you showed up", fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ⓘ", fontSize = 10.sp, color = SkyBlue)
                        }
                        Text("Consistent", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F), fontFamily = PoppinsFamily)
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // DIALOGS
        if (showWaterInfo) {
            AlertDialog(
                onDismissRequest = { showWaterInfo = false },
                containerColor = Color.White,
                icon = { Icon(Icons.Rounded.WaterDrop, null, tint = SkyBlue) },
                title = { Text("Watering your garden", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                text = { Text("Water is given when you check in or write your thoughts.\n\nYou can get a few drops each day. No rush.", fontFamily = PoppinsFamily, fontSize = 13.sp, textAlign = TextAlign.Center, color = Color(0xFF37474F)) },
                confirmButton = { TextButton(onClick = { showWaterInfo = false }) { Text("Got it", color = SkyBlue, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold) } }
            )
        }

        if (showStreakDetail) {
            AlertDialog(
                onDismissRequest = { showStreakDetail = false },
                containerColor = Color.White,
                icon = { Icon(Icons.Rounded.CheckCircle, null, tint = sageGreen, modifier = Modifier.size(32.dp)) },
                title = { Text("Weekly Consistency", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Last 7 days activity.", fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily, textAlign = TextAlign.Center)
                        Text("Based on your check-ins and reflections.", fontSize = 10.sp, color = Color.Gray.copy(0.8f), fontFamily = PoppinsFamily, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            weeklyData.forEach { data ->
                                val dayName = data.first
                                val attended = data.second
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(if (attended) sageGreen else Color.LightGray.copy(0.3f)), contentAlignment = Alignment.Center) {
                                        if (attended) Icon(Icons.Rounded.CheckCircle, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(dayName, fontSize = 10.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                                }
                            }
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { showStreakDetail = false }) { Text("Keep Going", color = SkyBlue, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold) } }
            )
        }
    }
}