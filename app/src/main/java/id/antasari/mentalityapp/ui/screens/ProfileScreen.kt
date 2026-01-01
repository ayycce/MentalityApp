package id.antasari.mentalityapp.ui.screens

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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.antasari.mentalityapp.R
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import id.antasari.mentalityapp.ui.theme.SkyBlue
import id.antasari.mentalityapp.ui.theme.SoftNeonPink

@Composable
fun ProfileScreen(navController: NavController) {
    // Scroll state untuk layar
    val scrollState = rememberScrollState()

    // Warna Gradient Header
    val headerGradient = Brush.verticalGradient(
        colors = listOf(SoftNeonPink.copy(alpha = 0.2f), Color.Transparent)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGradient)
            .verticalScroll(scrollState)
    ) {
        // --- 1. HEADER PROFILE ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            // Background Hiasan
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(headerGradient)
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Avatar dengan Border
                Box(
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .border(4.dp, Color.White, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        // Placeholder Avatar (Huruf A)
                        Text(
                            text = "A",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftNeonPink,
                            fontFamily = PoppinsFamily
                        )
                        // Kalau mau pakai gambar:
                        // Image(painter = painterResource(id = R.drawable.avatar_placeholder), ...)
                    }

                    // Tombol Edit Kecil
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SkyBlue)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { /* Edit Logic */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Edit, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Aisyah Safitri",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PoppinsFamily,
                    color = Color(0xFF37474F)
                )
                Text(
                    text = "Mental Health Warrior 🌱",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontFamily = PoppinsFamily
                )
            }
        }

        // --- 2. STATS SUMMARY (Row of 3) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileStatCard(label = "Streak", value = "7 Days", icon = "🔥", modifier = Modifier.weight(1f))
            ProfileStatCard(label = "Check-ins", value = "24", icon = "📝", modifier = Modifier.weight(1f))
            ProfileStatCard(label = "Level", value = "Sprout", icon = "🌿", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 3. ACHIEVEMENTS / BADGES ---
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = "Achievements",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PoppinsFamily,
                color = Color(0xFF37474F)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Horizontal Scroll untuk Badges
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(listOf("Zen Master", "Early Bird", "Consistent", "Explorer")) { badge ->
                    BadgeCard(badge)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 4. SETTINGS MENU ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
        ) {
            SettingsItem(icon = Icons.Rounded.Person, title = "Account Settings")
            SettingsItem(icon = Icons.Rounded.Notifications, title = "Notifications")
            SettingsItem(icon = Icons.Rounded.Security, title = "Privacy & Data")
            SettingsItem(icon = Icons.Rounded.Settings, title = "App Preferences", isLast = true)
        }

        // Tombol Logout (Merah Soft)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { /* Logout Logic */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)), // Soft Red bg
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(
                text = "Log Out",
                color = Color(0xFFEF4444), // Red text
                fontWeight = FontWeight.Bold,
                fontFamily = PoppinsFamily
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

// --- KOMPONEN PENDUKUNG ---

@Composable
fun ProfileStatCard(label: String, value: String, icon: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = PoppinsFamily, color = Color(0xFF37474F))
            Text(label, fontSize = 10.sp, color = Color.Gray, fontFamily = PoppinsFamily)
        }
    }
}

@Composable
fun BadgeCard(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3F4F6)) // Gray-100 placeholder
                .border(2.dp, SoftNeonPink.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Icon Badge Placeholder
            Text("🏅", fontSize = 32.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(name, fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily)
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, isLast: Boolean = false) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = SkyBlue, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(title, fontSize = 14.sp, fontFamily = PoppinsFamily, color = Color(0xFF37474F))
            }
            Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
        }
        if (!isLast) {
            Divider(color = Color.Fills, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

// Helper untuk Divider color (Jika belum ada di theme)
private val Color.Companion.Fills get() = Color(0xFFEEEEEE)