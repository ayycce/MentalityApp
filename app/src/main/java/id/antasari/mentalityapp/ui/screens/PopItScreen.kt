package id.antasari.mentalityapp.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import id.antasari.mentalityapp.ui.theme.SkyBlue
import id.antasari.mentalityapp.ui.theme.SoftNeonPink

@Composable
fun PopItScreen(navController: NavController) {
    // Grid 6x6 (36 Bubble)
    val totalBubbles = 36
    val bubbleStates = remember { mutableStateListOf<Boolean>().apply { repeat(totalBubbles) { add(false) } } }

    // Haptic yang Lebih Kuat (View Based)
    val view = LocalView.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGradient) // 🔥 Pakai MainGradient biar konsisten
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // --- CUSTOM HEADER (Ganti TopAppBar) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Tombol Back (Kecil Bulat)
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(0.5f), CircleShape)
                ) {
                    Icon(Icons.Rounded.ArrowBack, null, tint = Color.Gray)
                }

                Text(
                    text = "Pop the Anxiety",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF37474F)
                )

                // Tombol Reset
                IconButton(
                    onClick = {
                        // Reset Semua
                        for (i in 0 until totalBubbles) { bubbleStates[i] = false }
                        // Efek Getar Reset
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(0.5f), CircleShape)
                ) {
                    Icon(Icons.Rounded.Refresh, null, tint = SoftNeonPink)
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Dorong konten ke tengah vertikal

            // --- WADAH POP IT (MAINANNYA) ---
            Card(
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp), // Shadow lebih lembut
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color.White)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(6), // 6 Kolom
                        modifier = Modifier
                            .width(300.dp), // Ukuran fix biar proporsional
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(totalBubbles) { index ->
                            // Warna Rainbow Pastel
                            val row = index / 6
                            val bubbleColor = when(row) {
                                0 -> Color(0xFFFFCDD2) // Merah Soft
                                1 -> Color(0xFFFFE0B2) // Orange Soft
                                2 -> Color(0xFFFFF9C4) // Kuning Soft
                                3 -> Color(0xFFC8E6C9) // Hijau Soft
                                4 -> Color(0xFFBBDEFB) // Biru Soft
                                else -> Color(0xFFE1BEE7) // Ungu Soft
                            }

                            PopBubble(
                                isPopped = bubbleStates[index],
                                baseColor = bubbleColor,
                                onClick = {
                                    if (!bubbleStates[index]) {
                                        bubbleStates[index] = true
                                        // 🔥 GETAR YANG LEBIH TERASA (Keyboard Tap)
                                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    } else {
                                        // Un-pop (Opsional)
                                        bubbleStates[index] = false
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Tap bubbles to release tension.",
                fontFamily = PoppinsFamily,
                color = Color.Gray,
                fontSize = 14.sp
            )
            Text(
                text = "Reset anytime.",
                fontFamily = PoppinsFamily,
                color = Color.Gray.copy(0.7f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(40.dp)) // Jarak extra buat navbar
        }
    }
}

// KOMPONEN SATU BUBBLE (Visual Dipercantik)
@Composable
fun PopBubble(
    isPopped: Boolean,
    baseColor: Color,
    onClick: () -> Unit
) {
    // Animasi Warna
    val colorAnim by animateColorAsState(
        targetValue = if (isPopped) baseColor.copy(alpha = 0.5f) else baseColor, // Lebih transparan pas pecah
        animationSpec = tween(150),
        label = "color"
    )

    // Animasi Scale (Efek Penyet)
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPopped) 0.90f else 1f,
        animationSpec = tween(150),
        label = "scale"
    )

    // Visual Bubble
    Box(
        modifier = Modifier
            .size(40.dp) // Ukuran sedikit lebih kecil biar rapi
            .scale(scaleAnim)
            .shadow(if (isPopped) 0.dp else 3.dp, CircleShape, spotColor = baseColor) // Shadow berwarna
            .clip(CircleShape)
            .background(colorAnim)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Highlight Kilau (Hanya ada jika BELUM pecah)
        if (!isPopped) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = (8).dp)
                    .background(Brush.radialGradient(listOf(Color.White.copy(0.8f), Color.Transparent)))
            )
        } else {
            // Indikator "Penyok" (Bayangan dalam saat pecah)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.05f)) // Sedikit gelap di tengah
            )
        }
    }
}