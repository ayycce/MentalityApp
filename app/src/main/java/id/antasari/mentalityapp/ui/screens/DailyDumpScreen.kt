package id.antasari.mentalityapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.antasari.mentalityapp.ui.navigation.Screen
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import id.antasari.mentalityapp.ui.theme.SoftNeonPink
import id.antasari.mentalityapp.ui.viewmodel.MoodViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DailyDumpScreen(
    navController: NavController,
    viewModel: MoodViewModel
) {
    // 1. STATE & DATA
    var text by remember { mutableStateOf("") }
    val journalList by viewModel.journalHistory.collectAsState()

    // Animasi Burn
    var isBurning by remember { mutableStateOf(false) }

    // Pop-up Dialog Judul
    var showTitleDialog by remember { mutableStateOf(false) }
    var titleInput by remember { mutableStateOf("") }

    LaunchedEffect(isBurning) {
        if (isBurning) {
            delay(2500)
            text = ""
            isBurning = false
        }
    }

    val saveButtonBrush = Brush.horizontalGradient(listOf(Color(0xFFD6A4FF), Color(0xFFFF85A2)))
    val cardColors = listOf(Color(0xFFFFF0F5), Color(0xFFE0F7FA), Color(0xFFF3E5F5), Color(0xFFFFF8E1))

    Box(modifier = Modifier.fillMaxSize().background(MainGradient)) {

        // --- LAYOUT UTAMA: STAGGERED GRID (MASONRY) ---
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2), // 2 Kolom
            verticalItemSpacing = 16.dp,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 40.dp, start = 24.dp, end = 24.dp, bottom = 120.dp),
            modifier = Modifier.fillMaxSize()
        ) {

            // ITEM 1: HEADER & INPUT (Full Span)
            item(span = StaggeredGridItemSpan.FullLine) {
                Column {
                    // Header
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Column { Text(
                            text = "Vent Here",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = SoftNeonPink,
                            fontFamily = PoppinsFamily
                        )
                            Text(
                                text = "Let it all out, no judgment",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontFamily = PoppinsFamily
                            )
                        }

                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Input Card
                    Card(
                        modifier = Modifier.fillMaxWidth().height(280.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(20.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                if (text.isEmpty()) Text(
                                    "What's messy in your head right now?",
                                    color = Color.LightGray,
                                    fontSize = 16.sp,
                                    fontFamily = PoppinsFamily
                                )
                                BasicTextField(
                                    value = text, onValueChange = { text = it },
                                    textStyle = TextStyle(
                                        fontFamily = PoppinsFamily,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    ),
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // --- GARIS TIPIS (DIVIDER) ---
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                thickness = 0.5.dp,
                                color = Color.LightGray.copy(alpha = 0.5f)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "${text.length} characters",
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    fontFamily = PoppinsFamily
                                )
                                Text(
                                    "Private & safe space",
                                    fontSize = 10.sp,
                                    color = Color.LightGray,
                                    fontFamily = PoppinsFamily
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.weight(1f).height(50.dp)
                                .clip(RoundedCornerShape(50)).background(saveButtonBrush)
                                .clickable {
                                    if (text.isNotBlank()) {
                                        showTitleDialog = true // Tampilkan Dialog
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Rounded.Save,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Save to Journal",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = PoppinsFamily
                                )
                            }
                        }
                        Box(
                            modifier = Modifier.weight(1f).height(50.dp)
                                .clip(RoundedCornerShape(50))
                                .border(1.dp, Color(0xFFFF7043), RoundedCornerShape(50))
                                .clickable { if (text.isNotEmpty()) isBurning = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Rounded.LocalFireDepartment,
                                    null,
                                    tint = Color(0xFFFF7043),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Burn It",
                                    color = Color(0xFFFF7043),
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = PoppinsFamily
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    if (journalList.isNotEmpty()) {
                        Text(
                            "Your Journal",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PoppinsFamily
                        )
                        Text(
                            "Previous thoughts & reflections",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontFamily = PoppinsFamily
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // ITEM LIST (Grid Masonry)
            itemsIndexed(journalList) { index, journal ->
                val bgColor = cardColors[index % cardColors.size]
                val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
                val dateStr = dateFormat.format(Date(journal.timestamp))

                // Logic Judul Default
                val displayTitle = if (journal.title.isBlank()) "Journal #${journalList.size - index}" else journal.title

                JournalItemCard(
                    title = displayTitle,
                    content = journal.content,
                    date = dateStr,
                    bgColor = bgColor,
                    onClick = {
                        // Aksi kalau KARTU diklik -> Buka Detail
                        viewModel.selectedJournal = journal
                        navController.navigate(Screen.JournalDetail.route)
                    },
                    // 🔥 UPDATE DI SINI: PANGGIL FUNGSI HAPUS 🔥
                    onDeleteClick = {
                        viewModel.deleteJournal(journal)
                    }
                )
            }
        } // Tutup LazyVerticalStaggeredGrid

        // --- DIALOG JUDUL ---
        if (showTitleDialog) {
            AlertDialog(
                onDismissRequest = { showTitleDialog = false },
                containerColor = Color.White,
                title = {
                    Text(
                        "Give it a title?",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SoftNeonPink

                    )
                },
                text = {
                    Column {
                        Text(
                            "Optional. We'll generate a default one if you skip.",
                            fontFamily = PoppinsFamily,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            placeholder = { Text("Enter title...", color = Color.LightGray) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SoftNeonPink,
                                cursorColor = SoftNeonPink,

                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.addJournal(titleInput, text)
                            text = ""
                            titleInput = ""
                            showTitleDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SoftNeonPink),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Save", fontFamily = PoppinsFamily, color = Color.White) }
                },
                dismissButton = {
                    TextButton(onClick = { showTitleDialog = false }) {
                        Text("Cancel", fontFamily = PoppinsFamily, color = Color.Gray)
                    }
                }
            )
        }

        // --- ANIMASI BURN ---
        AnimatedVisibility(
            isBurning,
            enter = fadeIn(tween(800)),
            exit = fadeOut(tween(800)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(0.9f)))
        }
        if (isBurning) {
            val scale by animateFloatAsState(
                if (isBurning) 1f else 0f,
                spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
            )
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.scale(scale)
                ) {
                    Icon(
                        Icons.Rounded.LocalFireDepartment,
                        null,
                        tint = Color(0xFFFF5722),
                        modifier = Modifier.size(150.dp)
                    )
                    Text(
                        "Letting go...",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PoppinsFamily
                    )
                }
            }
        }
    }
}

// 🔥 CARD SESUAI DESAIN UPLOAD + TITIK TIGA CLICKABLE 🔥
@Composable
fun JournalItemCard(
    title: String,
    content: String,
    date: String,
    bgColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit // Parameter Hapus
) {
    // State untuk Popup Menu
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 100.dp) // Minimal tinggi, tapi bisa memanjang (Masonry)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Judul
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = PoppinsFamily,
                    color = Color.Black.copy(0.9f),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                // Isi
                Text(
                    text = content,
                    fontSize = 12.sp,
                    color = Color.Black.copy(0.7f),
                    fontFamily = PoppinsFamily,
                    lineHeight = 16.sp,
                    maxLines = 5, overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(date, fontSize = 10.sp, color = Color.Gray, fontFamily = PoppinsFamily)

                // --- BAGIAN POPUP MENU ---
                Box { // Bungkus dalam Box agar menu muncul pas di icon
                    IconButton(
                        onClick = { showMenu = true }, // Klik titik 3 -> Buka Menu
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MoreHoriz,
                            contentDescription = "More Options",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Menu Popup Kecil
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }, // Klik luar -> Tutup
                        containerColor = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Delete",
                                    fontFamily = PoppinsFamily,
                                    color = Color.Red
                                )
                            },
                            onClick = {
                                showMenu = false
                                onDeleteClick() // Panggil aksi hapus
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Rounded.Delete,
                                    null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}