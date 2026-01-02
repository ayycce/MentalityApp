package id.antasari.mentalityapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.antasari.mentalityapp.data.local.JournalEntity
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import id.antasari.mentalityapp.ui.theme.SoftNeonPink
import id.antasari.mentalityapp.ui.viewmodel.MoodViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun JournalDetailScreen(
    navController: NavController,
    viewModel: MoodViewModel
) {
    // 1. DATA & STATE
    var currentJournal by remember { mutableStateOf(viewModel.selectedJournal) }
    var isEditing by remember { mutableStateOf(currentJournal == null) }

    // Input State
    var title by remember { mutableStateOf(currentJournal?.title ?: "") }
    var content by remember { mutableStateOf(currentJournal?.content ?: "") }

    // Deteksi Perubahan
    val originalTitle = remember(currentJournal) { currentJournal?.title ?: "" }
    val originalContent = remember(currentJournal) { currentJournal?.content ?: "" }
    val hasChanges = (title != originalTitle || content != originalContent) && content.isNotBlank()

    // Format Tanggal
    val displayTimestamp = currentJournal?.timestamp ?: System.currentTimeMillis()
    val dateStr = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(Date(displayTimestamp))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGradient)
            .padding(24.dp)
    ) {
        // --- NAVBAR ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BACK BUTTON
            IconButton(
                onClick = {
                    if (isEditing && currentJournal != null) {
                        // Cancel Edit
                        title = originalTitle
                        content = originalContent
                        isEditing = false
                    } else {
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.size(48.dp).background(Color.White, CircleShape)
            ) {
                Icon(Icons.Rounded.ArrowBack, "Back", tint = Color.Black)
            }

            // ACTION BUTTONS
            Row {
                if (isEditing) {
                    // TOMBOL SAVE (FIX BUG DELETE)
                    IconButton(
                        onClick = {
                            if (hasChanges) {
                                if (currentJournal != null) {
                                    // 🔥 UPDATE DATA LAMA (ID TETAP SAMA)
                                    viewModel.updateJournal(currentJournal!!, title, content)
                                    // Update state lokal biar UI refresh tanggal/isi
                                    currentJournal = currentJournal!!.copy(title = title, content = content, timestamp = System.currentTimeMillis())
                                } else {
                                    // 🔥 BUAT BARU
                                    viewModel.addJournal(title, content)
                                }
                                isEditing = false
                            }
                        },
                        enabled = hasChanges
                    ) {
                        Icon(Icons.Rounded.Check, "Save", tint = if (hasChanges) SoftNeonPink else Color.LightGray, modifier = Modifier.size(32.dp))
                    }
                } else {
                    // MODE BACA
                    IconButton(onClick = { isEditing = true }) {
                        Icon(Icons.Rounded.Edit, "Edit", tint = SoftNeonPink)
                    }
                    IconButton(onClick = {
                        if (currentJournal != null) {
                            viewModel.deleteJournal(currentJournal!!)
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Rounded.Delete, "Delete", tint = SoftNeonPink)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- KONTEN (SCROLLABLE) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Tanggal
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.CalendarToday, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = dateStr, fontSize = 14.sp, color = Color.Gray, fontFamily = PoppinsFamily)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- TAMPILAN SESUAI MODE ---
            if (isEditing) {
                // 🔥 MODE EDIT: TAMPILAN CARD & LABEL (Sesuai Request) 🔥

                // 1. INPUT JUDUL
                Text("Title", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = PoppinsFamily, color = Color.Black.copy(0.6f))
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        if (title.isEmpty()) Text("Give it a title...", color = Color.LightGray, fontFamily = PoppinsFamily)
                        BasicTextField(
                            value = title,
                            onValueChange = { title = it },
                            textStyle = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 2. INPUT ISI
                Text("Content", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = PoppinsFamily, color = Color.Black.copy(0.6f))
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        if (content.isEmpty()) Text("Write your thoughts here...", color = Color.LightGray, fontFamily = PoppinsFamily)
                        BasicTextField(
                            value = content,
                            onValueChange = { content = it },
                            textStyle = TextStyle(fontFamily = PoppinsFamily, fontSize = 14.sp, color = Color.Black, lineHeight = 22.sp),
                            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 200.dp)
                        )
                    }
                }

            } else {
                // 🔥 MODE BACA: TAMPILAN BERSIH (Sesuai Desain Awal) 🔥
                Text(
                    text = title.ifEmpty { "Untitled" },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PoppinsFamily,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = content,
                    fontSize = 16.sp,
                    lineHeight = 28.sp,
                    fontFamily = PoppinsFamily,
                    color = Color.Black.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}