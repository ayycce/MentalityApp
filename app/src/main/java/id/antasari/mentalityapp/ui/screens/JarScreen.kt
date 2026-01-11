package id.antasari.mentalityapp.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import id.antasari.mentalityapp.ui.theme.SoftNeonPink

// Data Self-Care Tasks
val selfCareTasks = listOf(
    "Drink a full glass of water. 💧",
    "Unclench your jaw and drop your shoulders. 🧘",
    "Listen to your favorite song right now. 🎵",
    "Step outside and look at the sky for 1 min. ☁️",
    "Write down 3 things you are grateful for. ✍️",
    "Text a friend just to say hi. 👋",
    "Put your phone down for 10 minutes. 📵",
    "Wash your face with cold water. 🌊",
    "Fix your posture. Sit up straight! 🪑",
    "Give yourself a warm hug. 🫂",
    "Eat a fruit or a snack you enjoy. 🍎",
    "Take 5 deep breaths. Inhale... Exhale... 🌬️"
)

@Composable
fun JarScreen(navController: NavController) {
    val view = LocalView.current

    // State
    var isOpened by remember { mutableStateOf(false) }
    var currentTask by remember { mutableStateOf("") }

    // Animasi Lid (Tutup Toples)
    val lidOffsetY by animateDpAsState(
        targetValue = if (isOpened) (-120).dp else 0.dp, // Melayang lebih tinggi biar gak nutupin kertas
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "lid"
    )
    val lidRotation by animateFloatAsState(
        targetValue = if (isOpened) -20f else 0f,
        label = "lidRot"
    )

    // Animasi Kertas (Note)
    val paperOffsetY by animateDpAsState(
        targetValue = if (isOpened) (-140).dp else 0.dp, // Naik ke atas
        animationSpec = spring(dampingRatio = 0.7f),
        label = "paperY"
    )
    val paperScale by animateFloatAsState(
        targetValue = if (isOpened) 1f else 0f,
        label = "paperScale"
    )
    val paperAlpha by animateFloatAsState(
        targetValue = if (isOpened) 1f else 0f,
        animationSpec = tween(300),
        label = "paperAlpha"
    )

    // Background Gradient (Warm Honey)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFFF8E1), Color(0xFFFFECB3))))
    ) {

        // --- LAYOUT UTAMA ---
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // 1. HEADER (Rapi dengan statusBarsPadding)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding() // 🔥 Biar aman dari poni HP
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.background(Color.White.copy(0.6f), CircleShape).size(48.dp)
                ) {
                    Icon(Icons.Rounded.ArrowBack, null, tint = Color(0xFF5D4037))
                }

                // Placeholder biar tombol Back tidak geser kalau Reset muncul/hilang
                Box(modifier = Modifier.size(48.dp)) {
                    if (isOpened) {
                        IconButton(
                            onClick = {
                                isOpened = false
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            },
                            modifier = Modifier.fillMaxSize().background(Color.White.copy(0.6f), CircleShape)
                        ) {
                            Icon(Icons.Rounded.Refresh, null, tint = Color(0xFF5D4037))
                        }
                    }
                }
            }

            // 2. JUDUL & TEKS (Di atas Toples)
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isOpened) "Here is a thought for you" else "The Self-Care Jar",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color(0xFF5D4037)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isOpened) "Take it one step at a time." else "Tap the jar to pick a gentle task.",
                    fontFamily = PoppinsFamily,
                    fontSize = 14.sp,
                    color = Color(0xFF8D6E63)
                )
            }

            // 3. AREA TOPLES (Mengambil sisa ruang tengah)
            Box(
                modifier = Modifier
                    .weight(1f) // Isi sisa ruang
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // CONTAINER UTAMA ELEMEN TOPLES
                // Urutan coding: Paling atas = Paling belakang layer-nya
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier.height(400.dp) // Area kerja animasi
                ) {

                    // A. KERTAS TUGAS (Layer Paling Belakang - Muncul dari dalam)
                    Card(
                        modifier = Modifier
                            .offset(y = paperOffsetY) // Animasi naik
                            .width(260.dp)
                            .height(180.dp)
                            .scale(paperScale)
                            .alpha(paperAlpha),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = currentTask,
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF5D4037),
                                lineHeight = 28.sp
                            )
                            Box(modifier = Modifier.align(Alignment.TopCenter).size(12.dp).background(SoftNeonPink, CircleShape))
                        }
                    }

                    // B. BADAN TOPLES (Layer Tengah)
                    Box(
                        modifier = Modifier
                            .size(width = 220.dp, height = 280.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (!isOpened) {
                                    isOpened = true
                                    currentTask = selfCareTasks.random()
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                }
                            }
                    ) {
                        // Menggambar Toples
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Kaca Semi-Transparan
                            drawRoundRect(
                                color = Color.White.copy(alpha = 0.2f),
                                size = Size(w, h),
                                cornerRadius = CornerRadius(40f, 40f)
                            )
                            // Outline Kaca
                            drawRoundRect(
                                color = Color.White.copy(alpha = 0.5f),
                                size = Size(w, h),
                                cornerRadius = CornerRadius(40f, 40f),
                                style = Stroke(width = 6f)
                            )

                            // Isi Madu/Gulungan Kertas (Hanya gambar kalau tertutup)
                            if (!isOpened) {
                                drawCircle(color = Color(0xFFFFD54F), radius = 35f, center = Offset(w * 0.3f, h * 0.8f))
                                drawCircle(color = Color(0xFFFFCA28), radius = 40f, center = Offset(w * 0.7f, h * 0.75f))
                                drawCircle(color = Color(0xFFFFB300), radius = 30f, center = Offset(w * 0.5f, h * 0.85f))
                            }

                            // Highlight Kilau Kaca
                            drawRoundRect(
                                color = Color.White.copy(alpha = 0.3f),
                                topLeft = Offset(20f, 40f),
                                size = Size(25f, h - 80f),
                                cornerRadius = CornerRadius(10f, 10f)
                            )
                        }

                        // Label "Gentle Tasks"
                        if (!isOpened) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFF8D6E63), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("Gentle Tasks", fontSize = 14.sp, fontFamily = PoppinsFamily, color = Color(0xFF5D4037))
                            }
                        }
                    }

                    // C. TUTUP TOPLES / LID (Layer Paling Depan/Atas)
                    // Posisikan relatif terhadap container (BottomCenter), lalu geser ke atas badan toples
                    Box(
                        modifier = Modifier
                            .offset(y = lidOffsetY - 260.dp) // Posisi awal di bibir toples (280 tinggi toples - 20 overlap)
                            .rotate(lidRotation)
                            .size(width = 180.dp, height = 45.dp)
                            .background(Color(0xFF8D6E63), RoundedCornerShape(8.dp))
                            .border(2.dp, Color(0xFF5D4037), RoundedCornerShape(8.dp))
                            .shadow(4.dp, RoundedCornerShape(8.dp))
                    )
                }
            }

            // Spacer bawah agar toples tidak terlalu mepet bawah
            Spacer(modifier = Modifier.height(40.dp))
        }

        // Partikel Magic (Overlay paling atas)
        if (isOpened) {
            FloatingParticles()
        }
    }
}

// Efek Partikel Melayang (Simple)
@Composable
fun FloatingParticles() {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -150f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Restart), label = "y"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Text("✨", modifier = Modifier.align(Alignment.Center).offset(x = (-100).dp, y = offsetY.dp + 60.dp).alpha(0.6f))
        Text("✨", modifier = Modifier.align(Alignment.Center).offset(x = 100.dp, y = offsetY.dp).alpha(0.4f))
        Text("🍯", modifier = Modifier.align(Alignment.Center).offset(x = 0.dp, y = offsetY.dp - 80.dp).alpha(0.3f).scale(0.8f))
    }
}