package id.antasari.mentalityapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Article
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.SelfImprovement // Icon untuk Breathe/Garden
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.ui.graphics.vector.ImageVector

// Tambahkan parameter 'icon' di sini 👇
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {

    // --- MENU UTAMA (Punya Icon) ---
    object Home : Screen("home", "Home", Icons.Rounded.Home)
    object Vent : Screen("vent", "Vent", Icons.Rounded.MenuBook)
    object Feed : Screen("feed", "Feed", Icons.Rounded.Article)
    object Garden : Screen("garden", "Garden", Icons.Rounded.Spa)
    object More : Screen("more", "More", Icons.Rounded.AutoAwesome)

    // --- SUB-MENU / HALAMAN DETAIL (Icon Dummy saja karena tidak muncul di Navbar) ---
    // Kita kasih icon default (misal Home) biar tidak error, tapi tidak dipakai.
    object Profile : Screen("profile", "Profile", Icons.Rounded.Home)
    object Breathing : Screen("breathing", "Breathe", Icons.Rounded.SelfImprovement)
    object CheckinHistory : Screen("checkin_history", "History", Icons.Rounded.Article)
    object JournalDetail : Screen("journal_detail", "Journal", Icons.Rounded.Edit)
}