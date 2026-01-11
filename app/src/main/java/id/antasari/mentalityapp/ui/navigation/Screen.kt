package id.antasari.mentalityapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Article
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.ui.graphics.vector.ImageVector

// 🔥 PERBAIKAN DI SINI: Tambahkan '? = null' agar icon & title tidak wajib diisi
sealed class Screen(
    val route: String,
    val title: String? = null,
    val icon: ImageVector? = null
) {

    // 1. SCREEN KHUSUS ONBOARDING (Gak perlu title & icon, jadi aman sekarang)
    object Splash : Screen("splash_screen")
    object NameInput : Screen("name_input_screen")
    object Welcome : Screen("welcome_screen")

    // 2. SCREEN UTAMA (Navbar)
    object Home : Screen("home", "Home", Icons.Rounded.Home)
    object Vent : Screen("vent", "Vent", Icons.AutoMirrored.Rounded.MenuBook) // Pakai AutoMirrored buat buku
    object More : Screen("more", "Explore", Icons.Rounded.AutoAwesome)

    // Kalau Feed & Garden mau dimasukin navbar:
    object Feed : Screen("feed", "Feed", Icons.Rounded.Article)
    object Garden : Screen("garden", "Garden", Icons.Rounded.Spa)
    object Profile : Screen("profile", "Profile", Icons.Rounded.Person)

    // 3. SCREEN DETAIL / LAINNYA
    object Archive : Screen("archive_screen", "Archive", Icons.Rounded.Inventory2)
    object Breathing : Screen("breathing", "Breathe", Icons.Rounded.SelfImprovement)
    object CheckinHistory : Screen("checkin_history", "History", Icons.Rounded.Article)
    object JournalDetail : Screen("journal_detail", "Journal", Icons.Rounded.Edit)
    object MoodCheck : Screen("mood_check_screen") // Tambahan biar mood check gak error
}