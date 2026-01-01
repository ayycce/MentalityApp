package id.antasari.mentalityapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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

@Composable
fun CheckinHistoryScreen(
    navController: NavController,
    viewModel: MoodViewModel
) {
    val moodHistory by viewModel.moodHistory.collectAsState()

    // State untuk Kalender
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGradient)
    ) {
        // --- HEADER ---
        item { HeaderSection(navController) }

        // --- CALENDAR SECTION ---
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

        // --- WEEKLY CHART SECTION (YANG DIUPDATE) ---
        item {
            WeeklyChartSection(
                selectedDate = selectedDate,
                historyData = moodHistory
            )
        }

        // --- DAILY LOGS TITLE ---
        item {
            val dateTitle = selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            Text(
                text = "Logs for $dateTitle",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PoppinsFamily,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 12.dp)
            )
        }

        // Filter List berdasarkan Tanggal
        val logsForSelectedDate = moodHistory.filter {
            val logDate = Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
            logDate == selectedDate
        }.sortedByDescending { it.timestamp }

        if (logsForSelectedDate.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No logs on this date", color = Color.Gray, fontFamily = PoppinsFamily)
                }
            }
        } else {
            items(logsForSelectedDate) { mood ->
                HistoryItemCard(mood)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        item { Spacer(modifier = Modifier.height(130.dp)) }
    }
}

// ================= KOMPONEN UI =================

@Composable
fun HeaderSection(navController: NavController) {
    Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(40.dp).background(Color.White, CircleShape)
            ) {
                Icon(Icons.Rounded.ArrowBack, null, tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Your Analysis", fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily, color = Color(0xFF37474F))
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun CalendarSection(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    historyData: List<MoodEntity>,
    onMonthChange: (YearMonth) -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Bulan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onMonthChange(currentMonth.minusMonths(1)) }) {
                    Icon(Icons.Rounded.ChevronLeft, null)
                }
                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = PoppinsFamily
                )
                IconButton(onClick = { onMonthChange(currentMonth.plusMonths(1)) }) {
                    Icon(Icons.Rounded.ChevronRight, null)
                }
            }

            // Grid Hari
            val daysInMonth = currentMonth.lengthOfMonth()
            val firstDayOfMonth = currentMonth.atDay(1)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                listOf("M", "T", "W", "T", "F", "S", "S").forEach {
                    Text(it, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            val totalSlots = daysInMonth + (firstDayOfMonth.dayOfWeek.value - 1)
            val rows = (totalSlots / 7) + if (totalSlots % 7 == 0) 0 else 1

            Column {
                for (row in 0 until rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        for (col in 0 until 7) {
                            val dayIndex = (row * 7 + col) - (firstDayOfMonth.dayOfWeek.value - 1) + 1
                            if (dayIndex in 1..daysInMonth) {
                                val date = currentMonth.atDay(dayIndex)
                                val isSelected = date == selectedDate
                                val isToday = date == LocalDate.now()
                                val hasData = historyData.any {
                                    Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate() == date
                                }

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) SoftNeonPink else Color.Transparent)
                                        .border(1.dp, if(isToday && !isSelected) SoftNeonPink else Color.Transparent, CircleShape)
                                        .clickable { onDateSelected(date) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = dayIndex.toString(),
                                            color = if (isSelected) Color.White else Color.Black,
                                            fontSize = 12.sp
                                        )
                                        if (hasData && !isSelected) {
                                            Box(modifier = Modifier.size(4.dp).background(SoftNeonPink, CircleShape))
                                        }
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.size(36.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- BAGIAN UTAMA YANG DIUBAH (GRAFIK) ---
@Composable
fun WeeklyChartSection(
    selectedDate: LocalDate,
    historyData: List<MoodEntity>
) {
    // 1. Logika Tanggal: Cari hari Senin dari minggu ini
    val startOfWeek = selectedDate.minusDays((selectedDate.dayOfWeek.value - 1).toLong())

    // 2. Logika Data: Ambil Rata-rata Mood per Hari
    val chartPoints = (0..6).map { offset ->
        val dateToCheck = startOfWeek.plusDays(offset.toLong())
        val logsOfDay = historyData.filter {
            Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate() == dateToCheck
        }

        if (logsOfDay.isNotEmpty()) {
            // Rata-rata moodIndex (0-4) + 1 supaya jadi skala 1-5
            val avgMood = logsOfDay.map { it.moodIndex }.average() + 1
            avgMood.toFloat()
        } else {
            0f // Tidak ada data
        }
    }

    // 3. UI KARTU (Sesuai Permintaan)
    Column(modifier = Modifier.padding(24.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Header Kartu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Weekly Insight", fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                        Text("Mood Flow 🌊", fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily, color = SoftNeonPink)
                    }
                    Icon(Icons.Rounded.CalendarToday, null, tint = SkyBlue)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // AREA GRAFIK + KETERANGAN VERTIKAL
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    // Keterangan Vertikal (Y-Axis Labels)
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(end = 8.dp), // Jarak ke grafik
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("High", fontSize = 10.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                        Text("Neu", fontSize = 10.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                        Text("Low", fontSize = 10.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                    }

                    // Canvas Grafik
                    Box(modifier = Modifier.weight(1f)) {
                        if (chartPoints.all { it == 0f }) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No data", color = Color.LightGray, fontSize = 12.sp, fontFamily = PoppinsFamily)
                            }
                        } else {
                            MoodChartCanvas(dataPoints = chartPoints)
                        }
                    }
                }

                // Label Hari (Horizontal)
                // Kita pastikan labelnya sesuai urutan data (Senin - Minggu)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp), // Padding biar lurus sama grafik (krn ada label High/Low di kiri)
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                        Text(day, fontSize = 10.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                    }
                }
            }
        }
    }
}

@Composable
fun MoodChartCanvas(dataPoints: List<Float>) {
    val graphColor = SoftNeonPink
    val gridColor = Color.LightGray.copy(alpha = 0.3f)
    val maxMood = 5f
    val minMood = 1f
    val range = maxMood - minMood

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        // Kita bagi 6 spasi karena ada 7 titik (0..6)
        val spacing = width / 6

        // 1. Garis Grid (High, Mid, Low)
        listOf(5f, 3f, 1f).forEach { level ->
            val y = height - ((level - minMood) / range) * height
            drawLine(color = gridColor, start = Offset(0f, y), end = Offset(width, y), strokeWidth = 1.dp.toPx())
        }

        // 2. Gambar Kurva
        val validPoints = dataPoints.mapIndexed { index, value ->
            if (value > 0) index to value else null
        }.filterNotNull()

        if (validPoints.size > 1) {
            val path = Path()
            validPoints.forEachIndexed { i, (index, value) ->
                val x = index * spacing
                val y = height - ((value - minMood) / range) * height
                if (i == 0) {
                    path.moveTo(x, y)
                } else {
                    val (prevIndex, prevValue) = validPoints[i - 1]
                    val prevX = prevIndex * spacing
                    val prevY = height - ((prevValue - minMood) / range) * height
                    val conX = (prevX + x) / 2
                    path.cubicTo(conX, prevY, conX, y, x, y)
                }
            }
            drawPath(path = path, color = graphColor, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
        }

        // 3. Gambar Titik
        dataPoints.forEachIndexed { index, value ->
            if (value > 0) {
                val x = index * spacing
                val y = height - ((value - minMood) / range) * height
                drawCircle(color = Color.White, radius = 6.dp.toPx(), center = Offset(x, y))
                drawCircle(color = graphColor, radius = 4.dp.toPx(), center = Offset(x, y))
            }
        }
    }
}

@Composable
fun HistoryItemCard(mood: MoodEntity) {
    val (iconRes, color) = getMoodResource(mood.moodIndex)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(mood.moodLabel, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = PoppinsFamily)
                val displayText = mood.contextAnswer ?: mood.contextPrompt ?: "No notes"
                Text(displayText, fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(timeFormat.format(Date(mood.timestamp)), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, fontFamily = PoppinsFamily)
        }
    }
}

fun getMoodResource(index: Int): Pair<Int, Color> {
    return when (index) {
        0 -> Pair(R.drawable.emoji_sad, Color(0xFF9CA3AF))
        1 -> Pair(R.drawable.emoji_upset, Color(0xFFF87171))
        2 -> Pair(R.drawable.emoji_neutral, Color(0xFFFBBF24))
        3 -> Pair(R.drawable.emoji_happy, Color(0xFF4ADE80))
        4 -> Pair(R.drawable.emoji_excited, Color(0xFF60A5FA))
        else -> Pair(R.drawable.emoji_neutral, Color.Gray)
    }
}