package id.antasari.mentalityapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

    // Brush Spesial untuk AI Chat Card
    val aiBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFA020F0), Color(0xFFFF85A2)) // Ungu ke Pink
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGradient)
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // --- HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
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
            // Icon Settings Kecil
            IconButton(onClick = { Toast.makeText(context, "Settings Clicked", Toast.LENGTH_SHORT).show() }) {
                Icon(Icons.Rounded.Settings, contentDescription = null, tint = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 1. AI COMPANION (FEATURED CARD) ---
        Text("AI Support", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clickable {
                    // Nanti arahkan ke halaman Chat AI
                    Toast.makeText(context, "Opening Geny AI...", Toast.LENGTH_SHORT).show()
                },
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(aiBrush) // Gradient Mewah
            ) {
                // Hiasan Circle Transparan
                Box(modifier = Modifier.offset(x = 200.dp, y = (-50).dp).size(200.dp).background(Color.White.copy(alpha = 0.1f), CircleShape))

                Row(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Chat with Geny AI", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = PoppinsFamily)
                        Text("Your 24/7 mental health buddy.", fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f), fontFamily = PoppinsFamily)
                        Spacer(modifier = Modifier.height(16.dp))
                        // Tombol Kecil "Start Chat"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.White)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("Start Chat 💬", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA020F0), fontFamily = PoppinsFamily)
                        }
                    }
                    // Icon Robot/Sparkle
                    Icon(Icons.Rounded.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(60.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 2. SELF DISCOVERY (GRID) ---
        Text("Self Discovery", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily)
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // MBTI TEST CARD
            MoreMenuCard(
                title = "MBTI Test",
                desc = "Know your personality",
                icon = Icons.Rounded.Face,
                bgColor = Color(0xFFFFF3E0), // Orange Pastel
                iconColor = Color(0xFFFFB74D),
                modifier = Modifier.weight(1f),
                onClick = { Toast.makeText(context, "MBTI Test Coming Soon!", Toast.LENGTH_SHORT).show() }
            )

            // PSYCHOLOGIST CARD
            MoreMenuCard(
                title = "Psychologist",
                desc = "Find professional help",
                icon = Icons.Rounded.MedicalServices,
                bgColor = Color(0xFFE0F2F1), // Teal Pastel
                iconColor = Color(0xFF4DB6AC),
                modifier = Modifier.weight(1f),
                onClick = { Toast.makeText(context, "Finding Doctors...", Toast.LENGTH_SHORT).show() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 3. MINI GAMES ---
        Text("Relax & Play", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily)
        Spacer(modifier = Modifier.height(12.dp))

        // List Vertical untuk Games
        // Game 1: Breathing (Sudah ada)
        GameListTile(
            title = "Breathing Exercise",
            desc = "Calm your mind in 1 minute",
            icon = Icons.Rounded.Air,
            color = SkyBlue,
            onClick = { navController.navigate(Screen.Breathing.route) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Game 2: Bubble Wrap (Dummy)
        GameListTile(
            title = "Pop the Anxiety",
            desc = "Satisfying bubble pop game",
            icon = Icons.Rounded.TouchApp,
            color = SoftNeonPink,
            onClick = { Toast.makeText(context, "Pop pop! Coming soon.", Toast.LENGTH_SHORT).show() }
        )

        Spacer(modifier = Modifier.height(100.dp)) // Spacer Bawah
    }
}

// --- KOMPONEN PENDUKUNG (CARD KOTAK) ---
@Composable
fun MoreMenuCard(
    title: String,
    desc: String,
    icon: ImageVector,
    bgColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                Icon(icon, null, tint = iconColor)
            }

            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = PoppinsFamily, color = Color.Black.copy(alpha = 0.8f))
                Text(desc, fontSize = 10.sp, color = Color.Gray, fontFamily = PoppinsFamily, lineHeight = 14.sp)
            }
        }
    }
}

// --- KOMPONEN PENDUKUNG (LIST GAME PANJANG) ---
@Composable
fun GameListTile(
    title: String,
    desc: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Texts
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = PoppinsFamily)
                Text(desc, fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily)
            }

            // Arrow Icon
            Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, tint = Color.LightGray)
        }
    }
}