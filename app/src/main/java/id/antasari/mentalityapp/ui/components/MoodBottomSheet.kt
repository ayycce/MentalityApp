package id.antasari.mentalityapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// IMPORT PENTING: Mengambil Logic Brain dari MoodData
import id.antasari.mentalityapp.data.PromptBank
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import id.antasari.mentalityapp.ui.theme.SkyBlue
import id.antasari.mentalityapp.ui.theme.SoftNeonPink

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MoodBottomSheetContent(
    moodIndex: Int,
    moodLabel: String,
    onDismiss: () -> Unit,
    onSave: (Float, String?) -> Unit // Mengirim Intensity & Context String
) {
    // 1. AMBIL PERTANYAAN CERDAS DARI BANK 🧠
    // Menggunakan 'remember' agar pertanyaan tidak berubah saat user geser slider
    val prompt = remember(moodIndex) {
        PromptBank.getPromptForMood(moodIndex)
    }

    // State Input User
    var intensity by remember { mutableFloatStateOf(3f) } // Default tengah (3)
    var selectedChip by remember { mutableStateOf<String?>(null) }

    // Warna Gradient
    val mainGradient = Brush.horizontalGradient(
        colors = listOf(SkyBlue, SoftNeonPink)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 1. HEADER NETRAL (Sesuai Request) ---
        Text(
            text = "Ceritain dikit dong...",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PoppinsFamily,
            color = Color(0xFF37474F)
        )
        Text(
            text = "Gimana detail perasaanmu saat ini",
            fontSize = 12.sp,
            color = Color.Gray,
            fontFamily = PoppinsFamily
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- 2. INTENSITAS (SLIDER 1-5) ---
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Seberapa kuat rasanya?", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = PoppinsFamily)
                Text("${intensity.toInt()}/5", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SoftNeonPink, fontFamily = PoppinsFamily)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Slider(
                value = intensity,
                onValueChange = { intensity = it },
                valueRange = 1f..5f,
                steps = 3, // Snap ke angka bulat (1, 2, 3, 4, 5)
                colors = SliderDefaults.colors(
                    thumbColor = SoftNeonPink,
                    activeTrackColor = SkyBlue,
                    inactiveTrackColor = SkyBlue.copy(alpha = 0.2f)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Samar", fontSize = 10.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                Text("Dominan", fontSize = 10.sp, color = Color.Gray, fontFamily = PoppinsFamily)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 3. KONTEKS (PERTANYAAN DARI PROMPT BANK) ---
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tampilkan Pertanyaan yang diambil dari PromptBank
                Text(
                    text = prompt.question,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = PoppinsFamily,
                    color = Color(0xFF37474F),
                    modifier = Modifier.weight(1f)
                )

                // Tombol Reset Pilihan
                if (selectedChip != null) {
                    Text(
                        text = "Reset",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontFamily = PoppinsFamily,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable { selectedChip = null }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tampilkan Opsi Jawaban (Chips)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                prompt.options.forEach { option ->
                    val isSelected = selectedChip == option

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .border(
                                width = 1.dp,
                                color = if (isSelected) SoftNeonPink else Color.LightGray.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(50)
                            )
                            .background(
                                color = if (isSelected) SoftNeonPink.copy(alpha = 0.1f) else Color.Transparent
                            )
                            .clickable {
                                // Toggle select/unselect
                                selectedChip = if (isSelected) null else option
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = option,
                            fontSize = 12.sp,
                            color = if (isSelected) SoftNeonPink else Color.Gray,
                            fontFamily = PoppinsFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- 4. TOMBOL SIMPAN ---
        Button(
            onClick = {
                // Format Data untuk disimpan ke Database
                // Kita gabungkan Pertanyaan + Jawaban agar di History jelas konteksnya
                val contextToSave = if (selectedChip != null) {
                    "${prompt.question} -> $selectedChip"
                } else {
                    null // Kalau user skip / tidak pilih chip
                }

                onSave(intensity, contextToSave)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SoftNeonPink),
            contentPadding = PaddingValues(0.dp)
        ) {
                Text(
                    text = "Simpan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = PoppinsFamily
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
