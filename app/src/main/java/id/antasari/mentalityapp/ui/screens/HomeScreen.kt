package id.antasari.mentalityapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.antasari.mentalityapp.R
import id.antasari.mentalityapp.ui.navigation.Screen
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import id.antasari.mentalityapp.ui.theme.SkyBlue
import id.antasari.mentalityapp.ui.theme.SoftNeonPink
import id.antasari.mentalityapp.ui.components.MoodBottomSheetContent
import id.antasari.mentalityapp.ui.viewmodel.MoodViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Data class MoodItem
data class MoodItem(
    val id: Int,
    val iconRes: Int,
    val label: String,
    val gradientColors: List<Color>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: MoodViewModel) {
    // --- 1. DEFINISI WARNA & BRUSH ---
    val checkInBrush = Brush.linearGradient(listOf(Color(0xFFFBCFE8), Color(0xFFF472B6)))
    val streakBrush = Brush.linearGradient(listOf(Color(0xFFBAE6FD), Color(0xFF22D3EE)))
    val playButtonBrush = Brush.linearGradient(listOf(Color(0xFFE9D5FF), Color(0xFFC084FC)))

    // --- 2. STATE MANAGEMENT ---
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var showFeedback by remember { mutableStateOf(false) } // Untuk Toast "Tercatat"

    // State Data Check-in
    var selectedMoodItem by remember { mutableStateOf<MoodItem?>(null) }

    val scope = rememberCoroutineScope()

    // Data List Mood
    val moodItems = listOf(
        MoodItem(0, R.drawable.emoji_sad, "Sad", listOf(Color(0xFFE5E7EB), Color(0xFF9CA3AF))),
        MoodItem(1, R.drawable.emoji_upset, "Upset", listOf(Color(0xFFFECACA), Color(0xFFF87171))),
        MoodItem(2, R.drawable.emoji_neutral, "Neutral", listOf(Color(0xFFFEF3C7), Color(0xFFFBBF24))),
        MoodItem(3, R.drawable.emoji_happy, "Happy", listOf(Color(0xFFBBF7D0), Color(0xFF4ADE80))),
        MoodItem(4, R.drawable.emoji_excited, "Excited", listOf(Color(0xFFBFDBFE), Color(0xFF60A5FA)))
    )

    // --- 3. UI UTAMA ---
    Box(modifier = Modifier.fillMaxSize()) {

        // KONTEN SCROLLABLE
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MainGradient)
                .verticalScroll(rememberScrollState())
                .padding(top = 40.dp, start = 24.dp, end = 24.dp, bottom = 0.dp)
        ) {

            // A. HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hi, Aisyah",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SoftNeonPink,
                        fontFamily = PoppinsFamily
                    )
                    Text(
                        text = "How's your head today?",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = PoppinsFamily
                    )
                }
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { navController.navigate(Screen.Profile.route) },
                    contentAlignment = Alignment.Center
                ) {
                    // Profile Placeholder (Huruf A)
                    Text("A", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = SoftNeonPink, fontFamily = PoppinsFamily)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // B. MOOD TRACKER WIDGET
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Hiasan background tipis
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(100.dp)
                            .background(Brush.radialGradient(listOf(Color(0xFFFBCFE8).copy(alpha = 0.4f), Color.Transparent)))
                    )

                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("how u feeling rn?", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F), fontFamily = PoppinsFamily)
                            Text("💭", fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Grid Emoji 3D
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            moodItems.forEach { item ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(
                                                Brush.verticalGradient(listOf(Color(0xFFF9FAFB), Color(0xFFF3F4F6)))
                                            )
                                            .clickable {
                                                // 1. Set Item yang dipilih
                                                selectedMoodItem = item
                                                // 2. Buka Bottom Sheet (Prompt otomatis diambil di dalam sheet)
                                                showBottomSheet = true
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = item.iconRes),
                                            contentDescription = item.label,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // C. BREATHE CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SkyBlue),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Overwhelmed?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = PoppinsFamily)
                        Text("Take a moment to breathe", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp, fontFamily = PoppinsFamily)
                    }
                    Button(
                        onClick = { navController.navigate(Screen.Breathing.route) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text("Breathe", color = SkyBlue, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // D. PLAYLIST
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("healing playlist", fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("💖", fontSize = 14.sp)
                }
                Text("see all", fontSize = 14.sp, color = SoftNeonPink, fontFamily = PoppinsFamily)
            }

            Spacer(modifier = Modifier.height(16.dp))

            MusicListItem("late night study sesh", "lo-fi beats", playButtonBrush)
            Spacer(modifier = Modifier.height(12.dp))
            MusicListItem("cozy bedroom vibes", "chill ambient", playButtonBrush)
            Spacer(modifier = Modifier.height(12.dp))
            MusicListItem("midnight thoughts", "soft feels", playButtonBrush)

            Spacer(modifier = Modifier.height(32.dp))

            // E. STATS GRID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Check-ins Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                        .clickable { navController.navigate(Screen.CheckinHistory.route) },
                    shape = RoundedCornerShape(32.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(checkInBrush)
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("check-ins", fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f), fontFamily = PoppinsFamily)
                            Text("24", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = PoppinsFamily)
                            Text("this month 📊", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f), fontFamily = PoppinsFamily)
                        }
                    }
                }

                // Streak Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                        .clickable {
                            navController.navigate(Screen.Garden.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    shape = RoundedCornerShape(32.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(streakBrush)
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("streak", fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f), fontFamily = PoppinsFamily)
                            Text("7 days", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = PoppinsFamily)
                            Text("keep it up! 🔥", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f), fontFamily = PoppinsFamily)
                        }
                    }
                }
            }

            // SPACER AKHIR (Biar bisa scroll mentok di atas navbar)
            Spacer(modifier = Modifier.height(120.dp))
        }

        // --- 4. BOTTOM SHEET (MODAL CHECK-IN) ---
        if (showBottomSheet && selectedMoodItem != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                // Memanggil Konten Sheet (Prompt otomatis dihandle di dalam)
                MoodBottomSheetContent(
                    moodIndex = selectedMoodItem!!.id,
                    moodLabel = selectedMoodItem!!.label,
                    onDismiss = { showBottomSheet = false },
                    onSave = { intensity: Float, contextData: String? ->
                        // 2. SIMPAN DATA KE DATABASE VIA VIEWMODEL 💾
                        // contextData berisi "Pertanyaan -> Jawaban"
                        viewModel.addMood(
                            index = selectedMoodItem!!.id,
                            intensity = intensity,
                            context = contextData
                        )

                        // 3. UI Feedback (Tutup & Toast)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showBottomSheet = false
                        }
                        showFeedback = true

                        // Auto hide feedback setelah 3 detik
                        scope.launch {
                            delay(3000)
                            showFeedback = false
                        }
                    }
                )
            }
        }

        // --- 5. FEEDBACK OVERLAY (TOAST KEREN) ---
        AnimatedVisibility(
            visible = showFeedback,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp) // Muncul di atas navbar
        ) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✅", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Perasaan tercatat.",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            fontFamily = PoppinsFamily
                        )
                        // Validasi emosi dinamis
                        Text(
                            text = if ((selectedMoodItem?.id ?: 0) < 2) "Kedengerannya berat. Take care ya." else "Mantap, jaga ritmenya!",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontFamily = PoppinsFamily
                        )
                    }
                }
            }
        }
    }
}

// Komponen Helper untuk Playlist
@Composable
fun MusicListItem(title: String, subtitle: String, brush: Brush) {
    Card(
        modifier = Modifier.fillMaxWidth().height(80.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp).fillMaxSize()
        ) {
            Box(
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFFF3F4F6))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, fontFamily = PoppinsFamily)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily)
            }
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(brush),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White)
            }
        }
    }
}