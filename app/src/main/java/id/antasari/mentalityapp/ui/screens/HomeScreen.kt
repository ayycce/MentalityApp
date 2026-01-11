package id.antasari.mentalityapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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

// Data class untuk Quote Slider
data class QuoteCardItem(
    val text: String,
    val author: String,
    val brush: Brush
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String,
    navController: NavController,
    viewModel: MoodViewModel
) {
    // --- STATE MANAGEMENT ---
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var showFeedback by remember { mutableStateOf(false) }
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

    // Data List Quotes (Untuk Slider)
    val quoteList = listOf(
        QuoteCardItem(
            "It’s okay to take a break. You are doing enough.", "Reminder",
            Brush.linearGradient(listOf(Color(0xFFFFD180), Color(0xFFFFAB40))) // Orange
        ),
        QuoteCardItem(
            "Growth is a process, not a race. Breathe.", "Note to self",
            Brush.linearGradient(listOf(Color(0xFF80DEEA), Color(0xFF4DD0E1))) // Cyan
        ),
        QuoteCardItem(
            "Your feelings are valid. Let them flow.", "Gentle thought",
            Brush.linearGradient(listOf(Color(0xFFF48FB1), Color(0xFFF06292))) // Pink
        ),
        QuoteCardItem(
            "One step at a time is still moving forward.", "Keep going",
            Brush.linearGradient(listOf(Color(0xFFCE93D8), Color(0xFFBA68C8))) // Purple
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MainGradient)
                .verticalScroll(rememberScrollState())
        ) {
            // Padding khusus konten atas (Header & Cards)
            Column(modifier = Modifier.padding(top = 40.dp, start = 24.dp, end = 24.dp)) {

                // --- A. HEADER ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Hi, $userName", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = SoftNeonPink, fontFamily = PoppinsFamily)
                        Text("How's your day", fontSize = 14.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color(0xFFF3F4F6), CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).size(8.dp).background(Color(0xFFEF5350), CircleShape))
                        Icon(Icons.Rounded.Notifications, "Notifications", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- B. MOOD TRACKER (TETAP) ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.align(Alignment.TopEnd).size(100.dp).background(Brush.radialGradient(listOf(Color(0xFFFBCFE8).copy(alpha = 0.4f), Color.Transparent))))
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("how u feeling rn?", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F), fontFamily = PoppinsFamily)
                                Text("💭", fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                moodItems.forEach { item ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier.size(50.dp).clip(RoundedCornerShape(16.dp)).background(Brush.verticalGradient(listOf(Color(0xFFF9FAFB), Color(0xFFF3F4F6)))).clickable { selectedMoodItem = item; showBottomSheet = true },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Image(painter = painterResource(id = item.iconRes), contentDescription = item.label, modifier = Modifier.size(30.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- C. BREATHE CARD (TETAP) ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SkyBlue),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
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
            } // End Column Padding

            Spacer(modifier = Modifier.height(32.dp))

            // --- D. DAILY WHISPERS (2 CARDS GRID) 🔥 MODIFIKASI ---
            // Ambil 2 quote acak setiap recomposition (bisa diubah logic daily nanti)
            val dailyQuotes = remember { quoteList.shuffled().take(2) }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Daily Whispers",
                    modifier = Modifier.padding(horizontal = 24.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF37474F),
                    fontFamily = PoppinsFamily
                )

                Spacer(modifier = Modifier.height(16.dp))

                // GRID 2 KOLOM (Row Horizontal)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp), // Padding kiri-kanan layar
                    horizontalArrangement = Arrangement.spacedBy(16.dp) // Jarak antar card
                ) {
                    // CARD 1
                    QuoteGridCard(
                        quote = dailyQuotes[0],
                        modifier = Modifier.weight(1f) // Ambil setengah layar
                    )

                    // CARD 2
                    if (dailyQuotes.size > 1) {
                        QuoteGridCard(
                            quote = dailyQuotes[1],
                            modifier = Modifier.weight(1f) // Ambil setengah layar
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(120.dp))
        }

        // --- BOTTOM SHEET & FEEDBACK (TETAP SAMA) ---
        if (showBottomSheet && selectedMoodItem != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                MoodBottomSheetContent(
                    moodIndex = selectedMoodItem!!.id,
                    moodLabel = selectedMoodItem!!.label,
                    onDismiss = { showBottomSheet = false },
                    onSave = { intensity: Float, contextData: String? ->
                        viewModel.addMood(index = selectedMoodItem!!.id, intensity = intensity, context = contextData)
                        scope.launch { sheetState.hide() }.invokeOnCompletion { showBottomSheet = false }
                        showFeedback = true
                        scope.launch { delay(3000); showFeedback = false }
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = showFeedback,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp)
        ) {
            Card(
                modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("✅", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Perasaan tercatat.", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = PoppinsFamily)
                        Text(if ((selectedMoodItem?.id ?: 0) < 2) "Kedengerannya berat. Take care ya." else "Mantap, jaga ritmenya!", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontFamily = PoppinsFamily)
                    }
                }
            }
        }
    }
}

// 🔥 KOMPONEN CARD GRID (KOTAK MEMANJANG KE BAWAH) 🔥
@Composable
fun QuoteGridCard(quote: QuoteCardItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(160.dp), // Tinggi card agak memanjang
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(quote.brush)
        ) {
            // Hiasan Circle Transparan (Pojok Kanan Bawah)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 20.dp, y = 20.dp)
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(0.2f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Teks Quote
                Text(
                    text = "\"${quote.text}\"",
                    fontSize = 14.sp, // Font size pas untuk 2 kolom
                    color = Color.White,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )

                // Penulis / Label
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).background(Color.White, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = quote.author,
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontFamily = PoppinsFamily
                    )
                }
            }
        }
    }
}