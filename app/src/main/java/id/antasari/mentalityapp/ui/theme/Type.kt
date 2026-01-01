package id.antasari.mentalityapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp

// Setup Google Font Provider
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = id.antasari.mentalityapp.R.array.com_google_android_gms_fonts_certs
)

// Definisikan Font Poppins
val PoppinsFont = GoogleFont("Poppins")

val PoppinsFamily = FontFamily(
    Font(googleFont = PoppinsFont, fontProvider = provider),
    Font(googleFont = PoppinsFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = PoppinsFont, fontProvider = provider, weight = FontWeight.Medium)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    headlineMedium = TextStyle( // Buat Nama User
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    titleMedium = TextStyle( // Buat Judul Section
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    )
    // Kamu bisa tambah style lain kalau perlu
)