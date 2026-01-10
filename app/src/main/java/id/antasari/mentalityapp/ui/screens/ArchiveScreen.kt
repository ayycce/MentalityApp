package id.antasari.mentalityapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.antasari.mentalityapp.ui.navigation.Screen
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import id.antasari.mentalityapp.ui.theme.SoftNeonPink
import id.antasari.mentalityapp.ui.viewmodel.MoodViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ArchiveScreen(
    navController: NavController,
    viewModel: MoodViewModel
) {
    // 1. Ambil Data Arsip dari ViewModel
    val archivedList by viewModel.archivedJournals.collectAsState()

    // Warna Card (Sama kayak halaman depan)
    val cardColors = listOf(Color(0xFFFFF0F5), Color(0xFFE0F7FA), Color(0xFFF3E5F5), Color(0xFFFFF8E1))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGradient) // 🔥 Background Gradient
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // --- HEADER (Top Bar) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tombol Back
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .background(Color.White, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = SoftNeonPink
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Judul Halaman
                Column {
                    Text(
                        text = "Archived Notes",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = SoftNeonPink,
                        fontFamily = PoppinsFamily
                    )
                    Text(
                        text = "Hidden from timeline, but not forgotten.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontFamily = PoppinsFamily
                    )
                }
            }

            // --- KONTEN UTAMA ---
            if (archivedList.isEmpty()) {
                // Tampilan Kalau Arsip Kosong
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Rounded.Inventory2,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No archived notes yet",
                            fontFamily = PoppinsFamily,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                // Tampilan Grid (Sama persis kayak DailyDump)
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    verticalItemSpacing = 16.dp,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(archivedList) { index, journal ->
                        val bgColor = cardColors[index % cardColors.size]
                        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
                        val dateStr = dateFormat.format(Date(journal.timestamp))
                        val displayTitle = if (journal.title.isBlank()) "Journal #${index + 1}" else journal.title

                        // Pakai Card yang sama dengan Halaman Depan
                        JournalItemCard(
                            title = displayTitle,
                            content = journal.content,
                            date = dateStr,
                            bgColor = bgColor,
                            onClick = {
                                // Klik Card -> Buka Detail
                                viewModel.selectedJournal = journal
                                navController.navigate(Screen.JournalDetail.route)
                            },
                            onDeleteClick = {
                                viewModel.deleteJournal(journal)
                            },
                            onArchiveClick = {
                                // Klik Unarchive -> Kembalikan ke Home
                                viewModel.toggleArchiveStatus(journal)
                            },
                            // 🔥 Ubah tulisan menu jadi "Unarchive"
                            archiveLabel = "Unarchive"
                        )
                    }
                }
            }
        }
    }
}