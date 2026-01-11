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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import id.antasari.mentalityapp.data.local.JournalEntity
import id.antasari.mentalityapp.data.local.MoodEntity
import id.antasari.mentalityapp.ui.navigation.Screen
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import id.antasari.mentalityapp.ui.theme.SkyBlue
import id.antasari.mentalityapp.ui.theme.SoftNeonPink
import id.antasari.mentalityapp.ui.viewmodel.MoodViewModel
import id.antasari.mentalityapp.ui.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: MoodViewModel,
    userViewModel: UserViewModel = viewModel()
) {
    // 1. AMBIL DATA REAL-TIME
    val journalList by viewModel.activeJournals.collectAsState()
    val moodList by viewModel.moodHistory.collectAsState()

    // Data User
    val userName by userViewModel.userName.collectAsState()
    val userAvatar by userViewModel.userAvatar.collectAsState()

    // 2. HITUNG STATS UTAMA
    val stats = remember(journalList, moodList) {
        calculateStats(journalList, moodList)
    }

    // 3. HITUNG MONTHLY RECAP (WRAP)
    val monthlyRecap = remember(journalList, moodList) {
        calculateMonthlyRecap(journalList, moodList)
    }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // State Dialogs
    var showCareDaysInfo by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showRecapDialog by remember { mutableStateOf(false) } // 🔥 Dialog Recap
    var isNotificationOn by remember { mutableStateOf(false) }

    val headerGradient = Brush.verticalGradient(
        colors = listOf(SoftNeonPink.copy(alpha = 0.2f), Color.Transparent)
    )

    Box(modifier = Modifier.fillMaxSize()) {
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
                    // Avatar Circle
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .border(4.dp, Color.White, CircleShape)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            if (userAvatar.isNotEmpty()) {
                                Text(text = userAvatar, fontSize = 50.sp)
                            } else {
                                Text(
                                    text = userName.firstOrNull()?.toString()?.uppercase() ?: "A",
                                    fontSize = 40.sp, fontWeight = FontWeight.Bold, color = SoftNeonPink, fontFamily = PoppinsFamily
                                )
                            }
                        }
                        // Tombol Edit
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SkyBlue)
                                .border(2.dp, Color.White, CircleShape)
                                .clickable { showEditProfileDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Edit, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = userName, fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily, color = Color(0xFF37474F))
                    Text(text = "Trying to take care of myself 🌱", fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                }
            }

            // --- 2. MAIN STATS CARDS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // CARD 1: CARE DAYS
                ClickableStatCard(
                    label = "Care Days",
                    value = "${stats.totalCareDays} Days",
                    icon = "🌱",
                    subText = "Tap for info",
                    backgroundColor = Color(0xFFF1F8E9),
                    modifier = Modifier.weight(1f),
                    onClick = { showCareDaysInfo = true }
                )

                // CARD 2: MOMENTS (MODIFIKASI: Tanpa Angka)
                ClickableStatCard(
                    label = "Analysis & History",
                    value = "View All", // 🔥 Teks Statis
                    icon = "📊",
                    subText = "Tap to analyze",
                    backgroundColor = Color(0xFFE0F7FA),
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.CheckinHistory.route) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 3. MONTHLY RECAP CARD (PENGGANTI INSIGHT) ---
            MonthlyRecapCard(recap = monthlyRecap, onClick = { showRecapDialog = true })

            Spacer(modifier = Modifier.height(32.dp))

            // --- 4. SETTINGS ---
            SectionHeader("My Journey")
            SettingsGroup {
                SettingsItem(Icons.Rounded.Inventory2, "Archived Journals") { navController.navigate(Screen.Archive.route) }
                SettingsItem(Icons.Rounded.EmojiEvents, "Achievements", isLast = true) { Toast.makeText(context, "Coming Soon!", Toast.LENGTH_SHORT).show() }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader("Look & Feel")
            SettingsGroup {
                SettingsItem(Icons.Rounded.Palette, "App Theme", "Soft Pink (Default)") { }
                SettingsItem(Icons.Rounded.Translate, "Language", "English", isLast = true) { }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader("Preferences")
            SettingsGroup {
                SettingsSwitchItem(Icons.Rounded.Notifications, "Daily Reminder", isNotificationOn, { isNotificationOn = it }, isLast = true)
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader("Data & Security")
            SettingsGroup {
                SettingsItem(Icons.Rounded.CloudUpload, "Backup & Restore") { }
                SettingsItem(Icons.Rounded.Security, "Privacy Policy", isLast = true) { }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // LOGOUT
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text("Log Out", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily)
            }

            Spacer(modifier = Modifier.height(120.dp))
        }

        // ==========================================
        // 🔥 DIALOGS AREA 🔥
        // ==========================================

        // 1. RECAP DIALOG (WRAPPED)
        if (showRecapDialog) {
            RecapDetailDialog(recap = monthlyRecap, onDismiss = { showRecapDialog = false })
        }

        // 2. EDIT PROFILE
        if (showEditProfileDialog) {
            EditProfileDialog(
                currentName = userName,
                currentAvatar = userAvatar,
                onDismiss = { showEditProfileDialog = false },
                onSave = { name, avatar ->
                    userViewModel.saveName(name)
                    userViewModel.saveAvatar(avatar)
                    showEditProfileDialog = false
                }
            )
        }

        // 3. CARE DAYS INFO
        if (showCareDaysInfo) {
            CareDaysInfoDialog(
                onDismiss = { showCareDaysInfo = false },
                onVisitGarden = {
                    showCareDaysInfo = false
                    navController.navigate(Screen.Garden.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

// ==========================================
// 🔥 UI COMPONENTS & DIALOGS 🔥
// ==========================================

@Composable
fun RecapDetailDialog(recap: MonthlyRecap, onDismiss: () -> Unit) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 600.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. PEMBUKA
                Text("This month, you showed up in your own way.", fontSize = 14.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                Spacer(modifier = Modifier.height(24.dp))

                // 2. CARE SNAPSHOT
                Text("${recap.careDaysCount}", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = SkyBlue, fontFamily = PoppinsFamily)
                Text("Care Days", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F), fontFamily = PoppinsFamily)
                Text("You showed up on ${recap.careDaysCount} different days.", fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily)

                Spacer(modifier = Modifier.height(32.dp))
                Divider(color = Color(0xFFF5F5F5))
                Spacer(modifier = Modifier.height(32.dp))

                // 3. MOMENTS OVERVIEW
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Spa, null, tint = SoftNeonPink, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Moments Recorded", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F), fontFamily = PoppinsFamily)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("You recorded ${recap.totalEntries} moments.", fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                Text("Some were short, some were longer.", fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily)

                Spacer(modifier = Modifier.height(32.dp))

                // 4. EMOTIONAL FLOW (MINI CHART)
                Text("Emotional Flow 🌊", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F), fontFamily = PoppinsFamily)
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color(0xFFFAFAFA), RoundedCornerShape(12.dp))
                ) {
                    if (recap.moodFlow.isNotEmpty() && recap.moodFlow.size > 1) {
                        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                            val points = recap.moodFlow
                            val widthPerPoint = size.width / (points.size - 1)
                            val path = androidx.compose.ui.graphics.Path()
                            points.forEachIndexed { i, moodVal ->
                                val x = i * widthPerPoint
                                val y = size.height - ((moodVal / 5f) * size.height)
                                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }
                            drawPath(path, color = SoftNeonPink, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()))
                        }
                    } else {
                        Text("Not enough data for flow", modifier = Modifier.align(Alignment.Center), fontSize = 10.sp, color = Color.LightGray, fontFamily = PoppinsFamily)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("This month had a mix of ups and downs.", fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily)

                Spacer(modifier = Modifier.height(32.dp))

                // 5. MOST COMMON MOOD
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(recap.dominantIcon, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Most felt: ${recap.dominantMood}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5D4037), fontFamily = PoppinsFamily)
                            Text("This doesn't define your whole month.", fontSize = 10.sp, color = Color(0xFF5D4037).copy(0.7f), fontFamily = PoppinsFamily)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // 6. CLOSING MESSAGE
                Text("Thank you for taking care of yourself,\neven on hard days.", textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 14.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, color = Color(0xFF37474F), fontFamily = PoppinsFamily)

                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = SkyBlue), shape = RoundedCornerShape(50)) {
                    Text("Close Recap", fontFamily = PoppinsFamily)
                }
            }
        }
    }
}

@Composable
fun EditProfileDialog(currentName: String, currentAvatar: String, onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var tempName by remember { mutableStateOf(currentName) }
    var tempAvatar by remember { mutableStateOf(currentAvatar) }
    val avatars = listOf("", "🌱", "🐱", "🐻", "🦊", "🐰", "🌻", "☕", "🌙", "🎨", "🐸", "🐧")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text("Edit Profile", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, color = SkyBlue) },
        text = {
            Column {
                Text("Display Name", fontFamily = PoppinsFamily, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = PoppinsFamily, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF37474F)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SkyBlue, unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color(0xFFFAFAFA), unfocusedContainerColor = Color(0xFFFAFAFA),
                        focusedTextColor = Color(0xFF37474F), unfocusedTextColor = Color(0xFF37474F),
                        cursorColor = SkyBlue
                    ),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Choose Avatar", fontFamily = PoppinsFamily, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(4.dp)) {
                    items(avatars) { avatar ->
                        val isSelected = tempAvatar == avatar
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(48.dp).clip(CircleShape).background(if (isSelected) SkyBlue else Color(0xFFF5F5F5)).clickable { tempAvatar = avatar }.border(if (isSelected) 2.dp else 0.dp, if (isSelected) SkyBlue else Color.Transparent, CircleShape)) {
                            if (avatar.isEmpty()) Text("A", fontWeight = FontWeight.Bold, color = if(isSelected) Color.White else Color.Gray) else Text(avatar, fontSize = 24.sp)
                        }
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { onSave(tempName, tempAvatar) }, colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)) { Text("Save", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray, fontFamily = PoppinsFamily) } }
    )
}

@Composable
fun CareDaysInfoDialog(onDismiss: () -> Unit, onVisitGarden: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        icon = { Icon(Icons.Rounded.Spa, null, tint = Color(0xFFAED581), modifier = Modifier.size(32.dp)) },
        title = { Text("Care Days", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column {
                Text("Care Days count the days you took time to check in or reflect.", fontFamily = PoppinsFamily, fontSize = 13.sp, color = Color(0xFF37474F))
                Spacer(modifier = Modifier.height(12.dp))
                Text("This isn’t about doing it every day, just showing up when it matters.", fontFamily = PoppinsFamily, fontSize = 13.sp, color = Color(0xFF37474F), fontWeight = FontWeight.Medium)
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Got it", color = Color.Gray, fontFamily = PoppinsFamily) } },
        dismissButton = { TextButton(onClick = onVisitGarden) { Text("Visit Garden", color = SkyBlue, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold) } }
    )
}

@Composable
fun MonthlyRecapCard(recap: MonthlyRecap, onClick: () -> Unit) {
    val mysticGradient = Brush.horizontalGradient(colors = listOf(Color(0xFF8AC6D1), Color(
        0xFF8AC6D1
    )
    ))
    if (!recap.hasData) return

    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).clip(RoundedCornerShape(24.dp)).background(mysticGradient).clickable { onClick() }) {
        Box(modifier = Modifier.offset((-20).dp, (-20).dp).size(100.dp).clip(CircleShape).background(Color.White.copy(0.1f)))
        Box(modifier = Modifier.align(Alignment.BottomEnd).offset(20.dp, 20.dp).size(80.dp).clip(CircleShape).background(Color.White.copy(0.1f)))
        Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Your ${recap.monthName} Recap ✨", color = Color.White.copy(0.8f), fontSize = 12.sp, fontFamily = PoppinsFamily)
                Spacer(modifier = Modifier.height(8.dp))
                Text("You were mostly\n${recap.dominantMood} ${recap.dominantIcon}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily, lineHeight = 28.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tap to see your journey", color = Color.White.copy(0.6f), fontSize = 10.sp, fontFamily = PoppinsFamily)
            }
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(0.2f)), contentAlignment = Alignment.Center) { Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, tint = Color.White) }
        }
    }
}

// ==========================================
// 🔥 LOGIKA DATA (STATS & RECAP) 🔥
// ==========================================

data class UserStats(val totalCareDays: Int, val monthlyMoments: Int)

fun calculateStats(journals: List<JournalEntity>, moods: List<MoodEntity>): UserStats {
    val allTimestamps = journals.map { it.timestamp } + moods.map { it.timestamp }
    val totalCareDays = allTimestamps.map {
        val c = Calendar.getInstance().apply { timeInMillis = it }
        c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0)
        c.timeInMillis
    }.distinct().count()

    // Moments = Total Activity (Moods + Journals)
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)
    val journalsCount = journals.count { val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }; c.get(Calendar.MONTH) == currentMonth && c.get(Calendar.YEAR) == currentYear }
    val moodsCount = moods.count { val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }; c.get(Calendar.MONTH) == currentMonth && c.get(Calendar.YEAR) == currentYear }

    return UserStats(totalCareDays, journalsCount + moodsCount)
}

data class MonthlyRecap(
    val dominantMood: String, val dominantIcon: String, val totalEntries: Int,
    val careDaysCount: Int, val monthName: String, val moodFlow: List<Float>, val hasData: Boolean
)

fun calculateMonthlyRecap(journals: List<JournalEntity>, moods: List<MoodEntity>): MonthlyRecap {
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)
    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)

    val moodsThisMonth = moods.filter { val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }; c.get(Calendar.MONTH) == currentMonth && c.get(Calendar.YEAR) == currentYear }
    val journalsThisMonth = journals.filter { val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }; c.get(Calendar.MONTH) == currentMonth && c.get(Calendar.YEAR) == currentYear }

    val totalEntries = moodsThisMonth.size + journalsThisMonth.size
    if (totalEntries == 0) return MonthlyRecap("Unknown", "🌱", 0, 0, monthName, emptyList(), false)

    val allTimestamps = moodsThisMonth.map { it.timestamp } + journalsThisMonth.map { it.timestamp }
    val careDaysCount = allTimestamps.map { val c = Calendar.getInstance().apply { timeInMillis = it }; c.get(Calendar.DAY_OF_YEAR) }.distinct().size

    val moodCounts = moodsThisMonth.groupingBy { it.moodLabel }.eachCount()
    val dominant = moodCounts.maxByOrNull { it.value }
    val dominantMood = dominant?.key ?: "Balanced"
    val dominantIcon = when (dominantMood) { "Sad" -> "🌧️"; "Upset" -> "🔥"; "Neutral" -> "😐"; "Happy" -> "☀️"; "Excited" -> "🤩"; else -> "✨" }

    val moodFlow = moodsThisMonth.sortedBy { it.timestamp }.map { (it.moodIndex + 1).toFloat() }

    return MonthlyRecap(dominantMood, dominantIcon, totalEntries, careDaysCount, monthName, moodFlow, true)
}

// Helper Simple Components
@Composable fun SectionHeader(title: String) { Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, fontFamily = PoppinsFamily, modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp)) }
@Composable fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) { Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).clip(RoundedCornerShape(20.dp)).background(Color.White), content = content) }
@Composable fun ClickableStatCard(label: String, value: String, icon: String, subText: String, backgroundColor: Color, modifier: Modifier = Modifier, onClick: () -> Unit) { Card(modifier = modifier.clickable { onClick() }, shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(0.dp)) { Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.Start) { Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(backgroundColor), contentAlignment = Alignment.Center) { Text(icon, fontSize = 20.sp) }; Spacer(modifier = Modifier.height(16.dp)); Text(text = value, fontWeight = FontWeight.Bold, fontSize = 20.sp, fontFamily = PoppinsFamily, color = Color(0xFF37474F)); Text(text = label, fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily); Spacer(modifier = Modifier.height(12.dp)); Row(verticalAlignment = Alignment.CenterVertically) { Text(subText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SoftNeonPink, fontFamily = PoppinsFamily); Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, tint = SoftNeonPink, modifier = Modifier.size(12.dp).padding(start = 4.dp)) } } } }
@Composable fun SettingsItem(icon: ImageVector, title: String, subtitle: String? = null, isLast: Boolean = false, onClick: () -> Unit) { Column { Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = SkyBlue, modifier = Modifier.size(22.dp)); Spacer(modifier = Modifier.width(16.dp)); Column { Text(title, fontSize = 14.sp, fontFamily = PoppinsFamily, color = Color(0xFF37474F), fontWeight = FontWeight.Medium); if (subtitle != null) Text(subtitle, fontSize = 12.sp, fontFamily = PoppinsFamily, color = Color.Gray) } }; Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, tint = Color.LightGray.copy(0.7f), modifier = Modifier.size(18.dp)) }; if (!isLast) Divider(color = Color(0xFFF5F5F5), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp)) } }
@Composable fun SettingsSwitchItem(icon: ImageVector, title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, isLast: Boolean = false) { Column { Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = SkyBlue, modifier = Modifier.size(22.dp)); Spacer(modifier = Modifier.width(16.dp)); Text(text = title, fontSize = 14.sp, fontFamily = PoppinsFamily, color = Color(0xFF37474F), fontWeight = FontWeight.Medium) }; Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SoftNeonPink, uncheckedThumbColor = Color.White, uncheckedTrackColor = Color.LightGray.copy(0.4f), uncheckedBorderColor = Color.Transparent), modifier = Modifier.scale(0.8f)) }; if (!isLast) Divider(color = Color(0xFFF5F5F5), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp)) } }