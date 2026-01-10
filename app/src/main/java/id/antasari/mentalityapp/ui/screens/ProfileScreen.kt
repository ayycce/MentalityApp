package id.antasari.mentalityapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import id.antasari.mentalityapp.data.local.JournalEntity
import id.antasari.mentalityapp.ui.navigation.Screen
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import id.antasari.mentalityapp.ui.theme.SkyBlue
import id.antasari.mentalityapp.ui.theme.SoftNeonPink
import id.antasari.mentalityapp.ui.viewmodel.MoodViewModel
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: MoodViewModel
) {
    // Data & Context
    val journalList by viewModel.activeJournals.collectAsState()

    // 🔥 UPDATE LOGIKA STATISTIK
    val stats = remember(journalList) { calculateStats(journalList) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var isNotificationOn by remember { mutableStateOf(false) }

    // Header Gradient
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
            Box(Modifier.fillMaxSize().background(headerGradient))

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Avatar
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .border(4.dp, Color.White, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftNeonPink,
                            fontFamily = PoppinsFamily
                        )
                    }
                    // Edit Icon
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SkyBlue)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { Toast.makeText(context, "Edit Profile Coming Soon!", Toast.LENGTH_SHORT).show() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Edit, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Aisyah Safitri",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PoppinsFamily,
                    color = Color(0xFF37474F)
                )
                Text(
                    text = "Trying to take care of myself 🌱",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontFamily = PoppinsFamily
                )
            }
        }

        // --- 2. MAIN STATS (CLICKABLE CARDS) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // CARD 1: CARE DAYS (STREAK) -> GARDEN
            ClickableStatCard(
                label = "Care Days",
                value = "${stats.streak} Days",
                icon = "🔥",
                subText = "See Garden",
                backgroundColor = Color(0xFFFFF0F5),
                modifier = Modifier.weight(1f),
                onClick = {
                    // 🔥 PERBAIKAN NAVIGASI: Pindah Tab dengan Benar
                    navController.navigate(Screen.Garden.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            // CARD 2: MOMENTS (THIS MONTH) -> HISTORY
            ClickableStatCard(
                label = "Moments (Month)",
                value = "${stats.monthlyCheckins}", // 🔥 GANTI JADI DATA BULANAN UNIK
                icon = "📅",
                subText = "View History",
                backgroundColor = Color(0xFFE0F7FA),
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate(Screen.CheckinHistory.route)
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 3. PERSONAL INSIGHT BANNER ---
        if (journalList.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.8f))
                    .border(1.dp, Color.White, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = SoftNeonPink,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Daily Insight",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftNeonPink,
                            fontFamily = PoppinsFamily
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stats.insightText,
                            fontSize = 14.sp,
                            color = Color(0xFF37474F),
                            fontFamily = PoppinsFamily,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // --- 4. SETTINGS SECTIONS ---

        SectionHeader("My Journey")
        SettingsGroup {
            SettingsItem(
                icon = Icons.Rounded.Inventory2,
                title = "Archived Journals",
                onClick = { navController.navigate(Screen.Archive.route) }
            )
            SettingsItem(
                icon = Icons.Rounded.EmojiEvents,
                title = "Achievements",
                isLast = true,
                onClick = { Toast.makeText(context, "Achievements Page Coming Soon!", Toast.LENGTH_SHORT).show() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader("Look & Feel")
        SettingsGroup {
            SettingsItem(
                icon = Icons.Rounded.Palette,
                title = "App Theme",
                subtitle = "Soft Pink (Default)",
                onClick = { Toast.makeText(context, "Theme selection coming soon!", Toast.LENGTH_SHORT).show() }
            )
            SettingsItem(
                icon = Icons.Rounded.Translate,
                title = "Language",
                subtitle = "English",
                isLast = true,
                onClick = { Toast.makeText(context, "Language selection coming soon!", Toast.LENGTH_SHORT).show() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🔥 SECTION BARU: PREFERENCES (NOTIFIKASI DISINI) 🔥
        SectionHeader("Preferences")
        SettingsGroup {
            SettingsSwitchItem(
                icon = Icons.Rounded.Notifications,
                title = "Daily Reminder",
                checked = isNotificationOn,
                onCheckedChange = { newState ->
                    isNotificationOn = newState
                    // Simulasi Logic
                    val msg = if (newState) "Daily Reminder On (20:00)" else "Daily Reminder Off"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                },
                isLast = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader("Data & Security")
        SettingsGroup {
            SettingsItem(
                icon = Icons.Rounded.CloudUpload,
                title = "Backup & Restore",
                onClick = { Toast.makeText(context, "Cloud Backup coming soon!", Toast.LENGTH_SHORT).show() }
            )
            SettingsItem(
                icon = Icons.Rounded.Security,
                title = "Privacy Policy",
                isLast = true,
                onClick = { }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 5. LOGOUT ---
        Button(
            onClick = { /* Logout Logic */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(
                text = "Log Out",
                color = Color(0xFFEF4444),
                fontWeight = FontWeight.Bold,
                fontFamily = PoppinsFamily
            )
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}

// ==========================================
// 🔥 KOMPONEN HELPER 🔥
// ==========================================

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        fontFamily = PoppinsFamily,
        modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White),
        content = content
    )
}

@Composable
fun ClickableStatCard(
    label: String,
    value: String,
    icon: String,
    subText: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                fontFamily = PoppinsFamily,
                color = Color(0xFF37474F)
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray,
                fontFamily = PoppinsFamily
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = subText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftNeonPink,
                    fontFamily = PoppinsFamily
                )
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowForward,
                    null,
                    tint = SoftNeonPink,
                    modifier = Modifier.size(12.dp).padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    isLast: Boolean = false,
    onClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = SkyBlue, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, fontSize = 14.sp, fontFamily = PoppinsFamily, color = Color(0xFF37474F), fontWeight = FontWeight.Medium)
                    if (subtitle != null) {
                        Text(subtitle, fontSize = 12.sp, fontFamily = PoppinsFamily, color = Color.Gray)
                    }
                }
            }
            Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, tint = Color.LightGray.copy(0.7f), modifier = Modifier.size(18.dp))
        }
        if (!isLast) {
            Divider(color = Color(0xFFF5F5F5), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isLast: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Bagian Kiri (Icon + Teks)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = SkyBlue, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontFamily = PoppinsFamily,
                    color = Color(0xFF37474F),
                    fontWeight = FontWeight.Medium
                )
            }

            // Bagian Kanan (Switch)
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = SoftNeonPink,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray.copy(0.4f),
                    uncheckedBorderColor = Color.Transparent
                ),
                modifier = Modifier.scale(0.8f) // Perkecil sedikit biar rapi
            )
        }
        if (!isLast) {
            Divider(color = Color(0xFFF5F5F5), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

// ==========================================
// 🔥 LOGIKA STATISTIK (DIPERBAIKI) 🔥
// ==========================================

data class UserStats(
    val totalCount: Int,
    val streak: Int,
    val monthlyCheckins: Int, // Data Baru
    val insightText: String
)

fun calculateStats(journals: List<JournalEntity>): UserStats {
    if (journals.isEmpty()) return UserStats(0, 0, 0, "Start your journey today.")

    val totalCount = journals.size

    // 1. Hitung Streak (Sederhana: Cek tanggal unik berurutan)
    val sortedDates = journals
        .map { it.timestamp }
        .map {
            val c = Calendar.getInstance().apply { timeInMillis = it }
            // Reset ke jam 00:00 biar hitungannya per hari
            c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0)
            c.timeInMillis
        }
        .distinct()
        .sortedDescending()

    // Logic Streak Simpel (Bisa di-copy dari yang sebelumnya yang lebih kompleks kalau mau)
    var streak = 0
    if (sortedDates.isNotEmpty()) streak = 1 // Placeholder minimal

    // 2. 🔥 HITUNG MOMENTS BULAN INI (UNIK HARI) 🔥
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    val monthlyCheckins = journals
        .filter {
            val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            c.get(Calendar.MONTH) == currentMonth && c.get(Calendar.YEAR) == currentYear
        }
        .map {
            // Ambil Hari-nya saja (Day of Year) untuk menghitung unik
            val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            c.get(Calendar.DAY_OF_YEAR)
        }
        .distinct() // Hapus duplikat di hari yang sama
        .count()

    val insightText = when {
        monthlyCheckins > 15 -> "You've been very mindful this month! 🌟"
        monthlyCheckins > 5 -> "Great job checking in with yourself. 🌱"
        else -> "Every entry helps you understand yourself better."
    }

    return UserStats(totalCount, streak, monthlyCheckins, insightText)
}

