package id.antasari.mentalityapp.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.antasari.mentalityapp.R
import id.antasari.mentalityapp.data.local.JournalEntity
import id.antasari.mentalityapp.data.local.MoodEntity
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import id.antasari.mentalityapp.ui.theme.SkyBlue
import id.antasari.mentalityapp.ui.theme.SoftNeonPink
import id.antasari.mentalityapp.ui.viewmodel.MoodViewModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

// Interface untuk menyatukan Mood & Journal dalam satu list
sealed interface TimelineEvent {
    val timestamp: Long
}
data class MoodEvent(val data: MoodEntity) : TimelineEvent { override val timestamp = data.timestamp }
data class JournalEvent(val data: JournalEntity) : TimelineEvent { override val timestamp = data.timestamp }

@Composable
fun CheckinHistoryScreen(
    navController: NavController,
    viewModel: MoodViewModel
) {
    // 1. AMBIL DATA
    val moodHistory by viewModel.moodHistory.collectAsState()
    val journalHistory by viewModel.activeJournals.collectAsState()

    // State untuk Kalender
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    // 🔥 FIX: PINDAHKAN LOGIKA TIMELINE KE SINI (DI LUAR LAZYCOLUMN) 🔥
    val timelineItems = remember(moodHistory, journalHistory, selectedDate) {
        val moods = moodHistory
            .filter { Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate() == selectedDate }
            .map { MoodEvent(it) }

        val journals = journalHistory
            .filter { Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate() == selectedDate }
            .map { JournalEvent(it) }

        // Gabung dan Sortir berdasarkan waktu (Terbaru di atas)
        (moods + journals).sortedByDescending { it.timestamp }
    }

    // 2. TAMPILAN UI UTAMA
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGradient)
    ) {
        // --- HEADER ---
        item {
            HeaderSection(
                title = "Analysis & History",
                onBack = { navController.popBackStack() }
            )
        }

        // --- WEEKLY INSIGHTS ---
        item { SectionTitle("Weekly Insights") }
        item { WeeklyTrendCard(selectedDate, moodHistory) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { WeeklyMoodPieCard(selectedDate, moodHistory) }
        item { Spacer(modifier = Modifier.height(32.dp)) }

        // --- CALENDAR NAVIGATOR ---
        item { SectionTitle("Select Date") }
        item {
            CalendarSection(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                historyData = moodHistory,
                onMonthChange = { currentMonth = it },
                onDateSelected = { date ->
                    selectedDate = date
                    if (YearMonth.from(date) != currentMonth) {
                        currentMonth = YearMonth.from(date)
                    }
                }
            )
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }

        // --- TIMELINE HEADER ---
        item {
            val dateTitle = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy"))
            SectionTitle("Timeline for $dateTitle 📝")
        }

        // --- LIST TIMELINE ---
        if (timelineItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🍃", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No activity recorded", color = Color.Gray.copy(0.6f), fontFamily = PoppinsFamily)
                    }
                }
            }
        } else {
            items(timelineItems) { item ->
                when (item) {
                    is MoodEvent -> MoodHistoryCard(item.data)
                    is JournalEvent -> JournalHistoryCard(item.data)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

// ================= KOMPONEN UI =================

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        fontFamily = PoppinsFamily,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
    )
}

@Composable
fun HeaderSection(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, bottom = 16.dp, start = 24.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .background(Color.White, CircleShape)
                .border(1.dp, Color(0xFFF5F5F5), CircleShape)
        ) {
            Icon(Icons.Rounded.ArrowBack, null, tint = Color.Black)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily, color = Color(0xFF37474F))
    }
}

// 🔥 CARD 1: WEEKLY TREND 🔥
@Composable
fun WeeklyTrendCard(selectedDate: LocalDate, historyData: List<MoodEntity>) {
    val startOfWeek = selectedDate.minusDays((selectedDate.dayOfWeek.value - 1).toLong())
    val chartPoints = (0..6).map { offset ->
        val dateToCheck = startOfWeek.plusDays(offset.toLong())
        val logsOfDay = historyData.filter {
            Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate() == dateToCheck
        }
        if (logsOfDay.isNotEmpty()) logsOfDay.map { it.moodIndex }.average().toFloat() + 1 else 0f
    }

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Mood Flow", fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                        Text("Weekly Trend 🌊", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily, color = Color(0xFF37474F))
                    }
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFE0F7FA)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.TrendingUp, null, tint = SkyBlue, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.height(150.dp)) {
                    Column(modifier = Modifier.fillMaxHeight().padding(end = 12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                        Text("High", fontSize = 10.sp, color = Color.LightGray, fontFamily = PoppinsFamily)
                        Text("Mid", fontSize = 10.sp, color = Color.LightGray, fontFamily = PoppinsFamily)
                        Text("Low", fontSize = 10.sp, color = Color.LightGray, fontFamily = PoppinsFamily)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        if (chartPoints.all { it == 0f }) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No data yet", color = Color.LightGray, fontSize = 12.sp, fontFamily = PoppinsFamily)
                            }
                        } else {
                            ModernMoodChart(dataPoints = chartPoints)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(start = 24.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                        Text(day, fontSize = 10.sp, color = Color.Gray, fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Divider(color = Color(0xFFF5F5F5))
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("High  = lighter emotional intensity\n" +
                            "Mid    = moderate intensity\n" +
                            "Low   = heavier emotional intensity", fontSize = 10.sp, color = Color.LightGray, fontFamily = PoppinsFamily)
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, fontSize = 10.sp, color = Color.Gray, fontFamily = PoppinsFamily)
    }
}

// 🔥 CARD 2: PIE CHART 🔥
@Composable
fun WeeklyMoodPieCard(selectedDate: LocalDate, historyData: List<MoodEntity>) {
    val startOfWeek = selectedDate.minusDays((selectedDate.dayOfWeek.value - 1).toLong())
    val endOfWeek = startOfWeek.plusDays(6)
    val weeklyLogs = historyData.filter {
        val date = Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
        !date.isBefore(startOfWeek) && !date.isAfter(endOfWeek)
    }
    val totalLogs = weeklyLogs.size
    val moodCounts = weeklyLogs.groupingBy { it.moodIndex }.eachCount()
    val moodConfig = listOf(
        Triple(0, "Sad", Color(0xFF9CA3AF)), Triple(1, "Upset", Color(0xFFF87171)),
        Triple(2, "Neutral", Color(0xFFFBBF24)), Triple(3, "Happy", Color(0xFF4ADE80)), Triple(4, "Excited", Color(0xFF60A5FA))
    )

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Mood Mix", fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                        Text("Distribution 🍩", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily, color = Color(0xFF37474F))
                    }
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFF3E5F5)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.DonutLarge, null, tint = Color(0xFFFC70D4), modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                if (totalLogs == 0) {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text("No logs this week", color = Color.LightGray, fontFamily = PoppinsFamily)
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                            Canvas(modifier = Modifier.size(100.dp)) {
                                var startAngle = -90f
                                moodCounts.forEach { (index, count) ->
                                    val sweepAngle = (count.toFloat() / totalLogs) * 360f
                                    val color = moodConfig.find { it.first == index }?.third ?: Color.Gray
                                    drawArc(color = color, startAngle = startAngle, sweepAngle = sweepAngle, useCenter = false, style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Butt))
                                    startAngle += sweepAngle
                                }
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$totalLogs", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF37474F), fontFamily = PoppinsFamily)
                            }
                        }
                        Spacer(modifier = Modifier.width(24.dp))
                        Column {
                            moodConfig.forEach { (index, label, color) ->
                                val count = moodCounts[index] ?: 0
                                if (count > 0) {
                                    val percentage = (count.toFloat() / totalLogs * 100).toInt()
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(label, fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily, modifier = Modifier.weight(1f))
                                        Text("$percentage%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F), fontFamily = PoppinsFamily)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 🔥 KARTU TIMELINE: MOOD 🔥
@Composable
fun MoodHistoryCard(mood: MoodEntity) {
    val (iconRes, color) = getMoodResource(mood.moodIndex)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(timeFormat.format(Date(mood.timestamp)), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.LightGray, fontFamily = PoppinsFamily)
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(mood.moodLabel, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = PoppinsFamily, color = Color(0xFF37474F))
                val displayText = mood.contextAnswer ?: mood.contextPrompt ?: "Mood check-in"
                Text(displayText, fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

// 🔥 KARTU TIMELINE: JOURNAL 🔥
@Composable
fun JournalHistoryCard(journal: JournalEntity) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Text(timeFormat.format(Date(journal.timestamp)), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.LightGray, fontFamily = PoppinsFamily)
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFFFB74D).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.MenuBook, null, tint = Color(0xFFF57C00), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(journal.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = PoppinsFamily, color = Color(0xFF37474F))
                Text(journal.content, fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

// --- HELPER LAINNYA ---
@Composable
fun ModernMoodChart(dataPoints: List<Float>) {
    val graphColor = SkyBlue
    val gradientColors = listOf(SkyBlue.copy(alpha = 0.4f), SkyBlue.copy(alpha = 0.0f))
    val maxMood = 5f; val minMood = 1f; val range = maxMood - minMood
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width; val height = size.height; val spacing = width / 6
        val validPoints = dataPoints.mapIndexed { index, value -> if (value > 0) index to value else null }.filterNotNull()
        if (validPoints.size > 1) {
            val path = Path(); val fillPath = Path()
            validPoints.forEachIndexed { i, (index, value) ->
                val x = index * spacing; val y = height - ((value - minMood) / range) * height
                if (i == 0) { path.moveTo(x, y); fillPath.moveTo(x, height); fillPath.lineTo(x, y) }
                else {
                    val (prevIndex, prevValue) = validPoints[i - 1]
                    val prevX = prevIndex * spacing; val prevY = height - ((prevValue - minMood) / range) * height
                    val conX1 = prevX + (x - prevX) / 2; val conY1 = prevY; val conX2 = prevX + (x - prevX) / 2; val conY2 = y
                    path.cubicTo(conX1, conY1, conX2, conY2, x, y); fillPath.cubicTo(conX1, conY1, conX2, conY2, x, y)
                }
                if (i == validPoints.lastIndex) { fillPath.lineTo(x, height); fillPath.lineTo(validPoints.first().first * spacing, height); fillPath.close() }
            }
            drawPath(path = fillPath, brush = Brush.verticalGradient(gradientColors)); drawPath(path = path, color = graphColor, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
        }
        dataPoints.forEachIndexed { index, value -> if (value > 0) { val x = index * spacing; val y = height - ((value - minMood) / range) * height; drawCircle(color = Color.White, radius = 6.dp.toPx(), center = Offset(x, y)); drawCircle(color = graphColor, radius = 4.dp.toPx(), center = Offset(x, y)) } }
    }
}

@Composable
fun CalendarSection(currentMonth: YearMonth, selectedDate: LocalDate, historyData: List<MoodEntity>, onMonthChange: (YearMonth) -> Unit, onDateSelected: (LocalDate) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(0.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onMonthChange(currentMonth.minusMonths(1)) }) { Icon(Icons.Rounded.ChevronLeft, null, tint = Color.Gray) }
                Text(text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")), fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = PoppinsFamily, color = Color(0xFF37474F))
                IconButton(onClick = { onMonthChange(currentMonth.plusMonths(1)) }) { Icon(Icons.Rounded.ChevronRight, null, tint = Color.Gray) }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) { listOf("M", "T", "W", "T", "F", "S", "S").forEach { Text(it, fontSize = 12.sp, color = SoftNeonPink, fontWeight = FontWeight.Bold) } }
            Spacer(modifier = Modifier.height(12.dp))
            val daysInMonth = currentMonth.lengthOfMonth(); val firstDayOfMonth = currentMonth.atDay(1); val totalSlots = daysInMonth + (firstDayOfMonth.dayOfWeek.value - 1); val rows = (totalSlots / 7) + if (totalSlots % 7 == 0) 0 else 1
            Column { for (row in 0 until rows) { Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceAround) { for (col in 0 until 7) {
                val dayIndex = (row * 7 + col) - (firstDayOfMonth.dayOfWeek.value - 1) + 1
                if (dayIndex in 1..daysInMonth) {
                    val date = currentMonth.atDay(dayIndex); val isSelected = date == selectedDate; val isToday = date == LocalDate.now(); val hasData = historyData.any { Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate() == date }
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(if (isSelected) SoftNeonPink else Color.Transparent).border(1.dp, if(isToday && !isSelected) SoftNeonPink else Color.Transparent, CircleShape).clickable { onDateSelected(date) }, contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(text = dayIndex.toString(), color = if (isSelected) Color.White else if (isToday) SoftNeonPink else Color(0xFF37474F), fontSize = 12.sp, fontWeight = if(isSelected || isToday) FontWeight.Bold else FontWeight.Normal); if (hasData && !isSelected) { Box(modifier = Modifier.padding(top=2.dp).size(4.dp).background(SoftNeonPink, CircleShape)) } }
                    }
                } else { Spacer(modifier = Modifier.size(36.dp)) } } } } } } }
}

fun getMoodResource(index: Int): Pair<Int, Color> { return when (index) { 0 -> Pair(R.drawable.emoji_sad, Color(0xFF9CA3AF)); 1 -> Pair(R.drawable.emoji_upset, Color(0xFFF87171)); 2 -> Pair(R.drawable.emoji_neutral, Color(0xFFFBBF24)); 3 -> Pair(R.drawable.emoji_happy, Color(0xFF4ADE80)); 4 -> Pair(R.drawable.emoji_excited, Color(0xFF60A5FA)); else -> Pair(R.drawable.emoji_neutral, Color.Gray) } }