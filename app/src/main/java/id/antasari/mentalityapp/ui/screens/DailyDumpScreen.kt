package id.antasari.mentalityapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.antasari.mentalityapp.ui.navigation.Screen
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import kotlinx.coroutines.delay

@Composable
fun DailyDumpScreen(navController: NavController) {
    var text by remember { mutableStateOf("") }

    // State untuk Trigger Animasi
    var isBurning by remember { mutableStateOf(false) }

    // Logic Durasi Burn
    LaunchedEffect(isBurning) {
        if (isBurning) {
            delay(2500) // Waktu untuk menikmati api (2.5 detik)
            text = "" // Teks terhapus
            isBurning = false // Animasi selesai
        }
    }

    // Brush Gradient Tombol Save
    val saveButtonBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFFD6A4FF), Color(0xFFFF85A2))
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // --- KONTEN HALAMAN UTAMA ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MainGradient)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // HEADER (Icon Buku Hitam Transparan - Konsisten)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                    contentDescription = null,
                    tint = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Vent Here",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PoppinsFamily,
                    color = Color.Black.copy(alpha = 0.8f)
                )
            }
            Text(
                text = "Let it all out, no judgment",
                fontSize = 12.sp,
                color = Color.Gray,
                fontFamily = PoppinsFamily,
                modifier = Modifier.padding(start = 32.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // INPUT AREA
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box {
                        if (text.isEmpty()) {
                            Text(
                                text = "What's messy in your head right now?",
                                color = Color.LightGray,
                                fontSize = 16.sp,
                                fontFamily = PoppinsFamily
                            )
                        }
                        BasicTextField(
                            value = text,
                            onValueChange = { text = it },
                            textStyle = TextStyle(
                                fontFamily = PoppinsFamily,
                                fontSize = 16.sp,
                                color = Color.Black
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${text.length} characters", fontSize = 10.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                        Text("Private & safe space", fontSize = 10.sp, color = Color.LightGray, fontFamily = PoppinsFamily)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ACTION BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tombol Save
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(50))
                        .background(saveButtonBrush)
                        .clickable { /* Logic Simpan */ },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Save, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save to Journal", color = Color.White, fontWeight = FontWeight.SemiBold, fontFamily = PoppinsFamily)
                    }
                }

                // Tombol Burn It (Trigger)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(50))
                        .border(1.dp, Color(0xFFFF7043), RoundedCornerShape(50))
                        .clickable {
                            if (text.isNotEmpty()) {
                                isBurning = true // Mulai Animasi
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.LocalFireDepartment, null, tint = Color(0xFFFF7043), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Burn It", color = Color(0xFFFF7043), fontWeight = FontWeight.SemiBold, fontFamily = PoppinsFamily)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Journal List
            Text("Your Journal", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily)
            Text("Previous thoughts & reflections", fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                JournalItemCard(
                    text = "Feeling stressed about work deadlines. Need to remember to take breaks.",
                    date = "Yesterday",
                    bgColor = Color(0xFFFFF0F5),
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.JournalDetail.route) }
                )
                JournalItemCard(
                    text = "Today was overwhelming but I made it through.",
                    date = "5 days ago",
                    bgColor = Color(0xFFE0F7FA),
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.JournalDetail.route) }
                )
            }
            Spacer(modifier = Modifier.height(100.dp))
        }

        // --- OVERLAY ANIMASI: "FADE BG" + "BOOM FIRE" ---

        // 1. Background Gelap (Fade In/Out halus)
        AnimatedVisibility(
            visible = isBurning,
            enter = fadeIn(animationSpec = tween(durationMillis = 800)),
            exit = fadeOut(animationSpec = tween(durationMillis = 800)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f))
            )
        }

        // 2. Api & Teks (Zoom In "BOOM" effect)
        // Kita pisahkan dari background supaya animasinya bisa beda (Spring/Bounce)
        if (isBurning) {
            // Animasi Scale dengan efek 'Spring' (Membal/Boom)
            val scale by animateFloatAsState(
                targetValue = if (isBurning) 1f else 0f, // Target ukuran normal (100%)
                // Spring Spec: Membuat efek membal (Bouncy)
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "BoomEffect"
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    // Terapkan scale 'Boom' di sini
                    modifier = Modifier.scale(scale)
                ) {
                    // Api Besar
                    Icon(
                        imageVector = Icons.Rounded.LocalFireDepartment,
                        contentDescription = "Burning",
                        tint = Color(0xFFFF5722),
                        modifier = Modifier.size(150.dp)
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        text = "Letting go...",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PoppinsFamily
                    )
                }
            }
        }
    }
}

// Komponen Card Jurnal
@Composable
fun JournalItemCard(
    text: String,
    date: String,
    bgColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                color = Color.Black.copy(alpha = 0.8f),
                fontFamily = PoppinsFamily,
                lineHeight = 16.sp,
                maxLines = 4
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(date, fontSize = 10.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                Icon(Icons.Rounded.MoreHoriz, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
            }
        }
    }
}