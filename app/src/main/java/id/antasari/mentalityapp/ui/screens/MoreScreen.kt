package id.antasari.mentalityapp.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.antasari.mentalityapp.ui.navigation.Screen
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import id.antasari.mentalityapp.ui.theme.SkyBlue
import id.antasari.mentalityapp.ui.theme.SoftNeonPink

@Composable
fun MoreScreen(navController: NavController) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // State untuk Dialog "Jar of Care"
    var showJarDialog by remember { mutableStateOf(false) }
    var currentJarTask by remember { mutableStateOf("") }

    // Brush Spesial untuk AI Chat Card
    val aiBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFA020F0), Color(0xFFFF85A2)) // Ungu ke Pink
    )

    // Data Grounding
    val groundingItems = listOf(
        GroundingData("5-4-3-2-1", "Senses Check", Icons.Rounded.Looks5, Color(0xFFE8F5E9), Color(0xFF43A047)),
        GroundingData("Body Check", "Feet on floor", Icons.Rounded.AccessibilityNew, Color(0xFFE3F2FD), Color(0xFF1E88E5)),
        GroundingData("Temp Check", "Hot or Cold?", Icons.Rounded.Thermostat, Color(0xFFFBE9E7), Color(0xFFF4511E)),
        GroundingData("Name 3", "Look around", Icons.Rounded.Visibility, Color(0xFFF3E5F5), Color(0xFF8E24AA))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGradient)
            .verticalScroll(scrollState)
    ) {
        // --- HEADER (Tanpa Icon Settings) ---
        Column(modifier = Modifier.padding(top = 40.dp, start = 24.dp, end = 24.dp)) {
            Text(
                text = "Explore & Heal",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = SoftNeonPink,
                fontFamily = PoppinsFamily
            )
            Text(
                text = "Discover tools for your soul",
                fontSize = 14.sp,
                color = Color.Gray,
                fontFamily = PoppinsFamily
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ==========================================
        // 1️⃣ QUICK GROUNDING (FIRST AID)
        // ==========================================
        // Menggunakan Padding SectionTitle yang sama dengan konten
        SectionTitle("Quick Grounding 🧘‍♂️", "1-minute reset for anxiety")
        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(groundingItems) { item ->
                GroundingCard(item) {
                    val routeId = when (item.title) {
                        "5-4-3-2-1" -> "54321"
                        "Body Check" -> "body"
                        "Temp Check" -> "temp"
                        "Name 3" -> "name3"
                        else -> "54321"
                    }
                    navController.navigate("grounding/$routeId")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ==========================================
        // 2️⃣ CALM & RESET (CLEAN WHITE CARDS)
        // ==========================================
        SectionTitle("Calm & Reset ✨", "Regulate your nervous system")
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            // Baris 1: Breathe & Listen
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MoreMenuCardModern(
                    title = "Breathe", desc = "4-7-8 Technique", icon = Icons.Rounded.Air,
                    iconBgColor = SkyBlue.copy(alpha = 0.1f), iconTint = SkyBlue, modifier = Modifier.weight(1f)
                ) { navController.navigate(Screen.Breathing.route) }

                MoreMenuCardModern(
                    title = "Listen", desc = "Calming sounds", icon = Icons.Rounded.Headphones,
                    iconBgColor = Color(0xFFE3F2FD), iconTint = Color(0xFF42A5F5), modifier = Modifier.weight(1f)
                ) { navController.navigate("sound_library")
                }}

            Spacer(modifier = Modifier.height(16.dp))

            // Baris 2: Color & Pop It
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MoreMenuCardModern(
                    title = "Color Therapy", desc = "Visual relax", icon = Icons.Rounded.Palette,
                    iconBgColor = Color(0xFFFBE9E7), iconTint = Color(0xFFFF7043), modifier = Modifier.weight(1f)
                ) { navController.navigate("color_therapy")
                }

                MoreMenuCardModern(
                    title = "Pop It", desc = "Release tension", icon = Icons.Rounded.TouchApp,
                    iconBgColor = Color(0xFFF3E5F5), iconTint = Color(0xFFAB47BC), modifier = Modifier.weight(1f)
                ) { navController.navigate("pop_it") }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ==========================================
        // 3️⃣ GENTLE CARE (JAR - SPECIAL CARD)
        // ==========================================
        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable {
                        // 🔥 HAPUS LOGIKA LAMA, GANTI JADI INI:
                        navController.navigate("jar_screen")
                    },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE0B2))
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("The Self-Care Jar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5D4037), fontFamily = PoppinsFamily)
                        Text("Tap for a gentle idea ✨", fontSize = 12.sp, color = Color(0xFF5D4037).copy(0.7f), fontFamily = PoppinsFamily)
                    }
                    // Icon Jar
                    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFFFE0B2)), contentAlignment = Alignment.Center) {
                        Text("🍯", fontSize = 24.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ==========================================
        // 4️⃣ LEARN GENTLY (CLEAN WHITE CARDS)
        // ==========================================
        SectionTitle("Learn Gently 📖", "Understand yourself better")
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MoreMenuCardModern(
                    title = "Articles", desc = "Short reads", icon = Icons.Rounded.MenuBook,
                    iconBgColor = Color(0xFFF1F8E9), iconTint = Color(0xFF689F38), modifier = Modifier.weight(1f)
                ) { navController.navigate("article_list/Articles")
                }
                MoreMenuCardModern(
                    title = "Emotions 101", desc = "Why we feel", icon = Icons.Rounded.Psychology,
                    iconBgColor = Color(0xFFFFF3E0), iconTint = Color(0xFFFFB74D), modifier = Modifier.weight(1f)
                ) { navController.navigate("article_list/Psychology")
                }}
        }

        Spacer(modifier = Modifier.height(40.dp))

        // ==========================================
        // 5️⃣ AI SUPPORT (BOTTOM - FEATURED DESIGN)
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text("Need more support?", fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clickable { navController.navigate("ai_chat")},
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp) // Shadow lebih dalam
            ) {
                Box(modifier = Modifier.fillMaxSize().background(aiBrush)) {
                    // Hiasan
                    Box(modifier = Modifier.offset(x = 200.dp, y = (-50).dp).size(200.dp).background(Color.White.copy(alpha = 0.1f), CircleShape))

                    Row(modifier = Modifier.fillMaxSize().padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Chat with Geny AI", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = PoppinsFamily)
                            Text("Your 24/7 mental health buddy.", fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f), fontFamily = PoppinsFamily)
                            Spacer(modifier = Modifier.height(16.dp))
                            // Tombol Kecil
                            Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(Color.White).padding(horizontal = 16.dp, vertical = 8.dp)) {
                                Text("Start Chat 💬", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA020F0), fontFamily = PoppinsFamily)
                            }
                        }
                        Icon(Icons.Rounded.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(60.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    // --- DIALOG: JAR OF CARE ---
    if (showJarDialog) {
        AlertDialog(
            onDismissRequest = { showJarDialog = false },
            containerColor = Color.White,
            icon = { Text("🍯", fontSize = 40.sp) },
            title = { Text("A Gentle Idea", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = currentJarTask,
                    fontFamily = PoppinsFamily,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = { showJarDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)) {
                    Text("I'll try it", fontFamily = PoppinsFamily)
                }
            }
        )
    }
}

// ==========================================
// 🔥 KOMPONEN PENDUKUNG (DESAIN DIPERBAIKI) 🔥
// ==========================================

@Composable
fun SectionTitle(title: String, subtitle: String) {
    // Padding kiri-kanan disamakan dengan konten (24.dp) agar SEJAJAR
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = title,
            fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily, color = Color(0xFF37474F)
        )
        Text(
            text = subtitle,
            fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily
        )
    }
}

data class GroundingData(val title: String, val subtitle: String, val icon: ImageVector, val bg: Color, val tint: Color)

// GROUNDING: Tetap Pastel tapi pakai Border & Shadow biar jelas
@Composable
fun GroundingCard(item: GroundingData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(150.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = item.bg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Shadow
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icon, null, tint = item.tint, modifier = Modifier.size(24.dp))
            }

            Column {
                Text(item.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily, color = Color(0xFF37474F))
                Text(item.subtitle, fontSize = 11.sp, color = Color.Gray, fontFamily = PoppinsFamily, lineHeight = 14.sp)
            }
        }
    }
}

// MENU CARD MODERN: Background Putih, Icon yang berwarna
@Composable
fun MoreMenuCardModern(
    title: String, desc: String, icon: ImageVector, iconBgColor: Color, iconTint: Color, modifier: Modifier = Modifier, onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(110.dp) // Ukuran proporsional
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), // 🔥 Background Putih Bersih
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp) // 🔥 Efek Melayang (Pop-out)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon dengan background warna khusus
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
            }

            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = PoppinsFamily, color = Color(0xFF37474F))
                Text(desc, fontSize = 10.sp, color = Color.Gray, fontFamily = PoppinsFamily, lineHeight = 12.sp)
            }
        }
    }
}