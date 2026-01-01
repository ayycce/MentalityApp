package id.antasari.mentalityapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import id.antasari.mentalityapp.ui.theme.SoftNeonPink

@Composable
fun JournalDetailScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGradient) // Tetap pakai tema utama
            .padding(24.dp)
    ) {
        // --- NAVBAR ATAS (Custom) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tombol Back
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }

            // Aksi (Edit & Delete)
            Row {
                IconButton(onClick = { /* Edit Action */ }) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = Color.Gray)
                }
                IconButton(onClick = { /* Delete Action */ }) {
                    Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = SoftNeonPink)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- KONTEN JURNAL ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Tanggal
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.CalendarToday, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tuesday, 24 Oct 2025",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontFamily = PoppinsFamily
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Judul (Opsional, atau ambil kalimat pertama)
            Text(
                text = "Feeling stressed about work",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PoppinsFamily,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Isi Jurnal Lengkap
            Text(
                text = "Today was honestly really overwhelming. The deadlines kept piling up and I felt like I couldn't catch a break. I need to remember to take deep breaths and maybe step outside for a bit. It's okay not to be perfect all the time.\n\nI managed to finish the main report though, so that's a small win. Going to listen to some lo-fi beats and sleep early tonight.",
                fontSize = 16.sp,
                lineHeight = 28.sp,
                fontFamily = PoppinsFamily,
                color = Color.Black.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(100.dp)) // Spacer bawah
        }
    }
}