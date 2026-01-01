package id.antasari.mentalityapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import id.antasari.mentalityapp.ui.theme.SkyBlue
import id.antasari.mentalityapp.ui.theme.SoftNeonPink

@Composable
fun StreakScreen(navController: NavController) {
    // Brush Api (Orange ke Merah)
    val fireBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFAB91), Color(0xFFFF5722))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGradient)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back Button (Pojok Kiri)
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Rounded.ChevronLeft, null, tint = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // API BESAR (Lingkaran)
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.LocalFireDepartment,
                contentDescription = null,
                tint = Color(0xFFFF7043), // Warna Orange Api
                modifier = Modifier.size(120.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("You're on Fire!", fontSize = 28.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily, color = Color(0xFFE64A19))
        Text("You've checked in for 5 days in a row.", fontSize = 14.sp, color = Color.Gray, fontFamily = PoppinsFamily)

        Spacer(modifier = Modifier.height(40.dp))

        // GRID STATS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StreakStatItem("Current Streak", "5 Days", SkyBlue, Modifier.weight(1f))
            StreakStatItem("Best Streak", "12 Days", SoftNeonPink, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // NEXT REWARD (Gamification)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎁", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Next Reward: Frozen Ice", fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily)
                        Text("Reach 7 days streak", fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Progress Bar
                LinearProgressIndicator(
                    progress = { 0.7f }, // 5 dari 7 hari
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)),
                    color = Color(0xFFFFAB91),
                    trackColor = Color(0xFFEEEEEE),
                )
            }
        }
    }
}

@Composable
fun StreakStatItem(label: String, value: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color, fontFamily = PoppinsFamily)
            Text(label, fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily)
        }
    }
}