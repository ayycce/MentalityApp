package id.antasari.mentalityapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.antasari.mentalityapp.ui.components.BottomNavBar
import id.antasari.mentalityapp.ui.navigation.Screen
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.viewmodel.MoodViewModel

// Import Semua Halaman
import id.antasari.mentalityapp.ui.screens.*

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: MoodViewModel
) {
    // 1. Cek Route Saat Ini
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 2. Tentukan Kapan Navbar Muncul
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Vent.route,
        Screen.Feed.route,
        Screen.Garden.route,
        Screen.More.route
    )

    // 3. LAYOUT UTAMA (Gradient Full Screen)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGradient) // Gradient Background
    ) {
        // Gunakan Scaffold Transparan (Hanya untuk handle Status Bar padding)
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = Color.Black
        ) { innerPadding ->

            // BOX WRAPPER (Untuk Teknik Overlay/Tumpuk)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()) // Jarak aman dari Status Bar
            ) {

                // --- LAYER 1: KONTEN HALAMAN (Di Belakang) ---
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.padding(
                        // Kalau Navbar muncul, kita kasih jarak bawah EXTRA (100dp)
                        // Supaya konten paling bawah bisa discroll dan tidak ketutup Navbar melayang
                        bottom = if (showBottomBar) 130.dp else 0.dp
                        // Tips: Set 0.dp jika ingin konten "mengalir" di belakang navbar (efek kaca),
                        // atau set 100.dp jika ingin konten berhenti tepat di atas navbar.
                    )
                ) {
                    // HALAMAN UTAMA
                    composable(Screen.Home.route) { HomeScreen(navController, viewModel) }
                    composable(Screen.Vent.route) { DailyDumpScreen(navController, viewModel) }
                    composable(Screen.Feed.route) { FeedScreen() } // Hapus parameter jika FeedScreen tidak butuh
                    composable(Screen.Garden.route) { GardenScreen() } // Hapus parameter jika GardenScreen tidak butuh
                    composable(Screen.More.route) { MoreScreen(navController) }

                    // HALAMAN DETAIL
                    composable(Screen.Profile.route) { ProfileScreen(navController) }
                    composable(Screen.Breathing.route) { BreathingScreen(navController) }
                    composable(Screen.CheckinHistory.route) { CheckinHistoryScreen(navController, viewModel) }
                    composable(Screen.JournalDetail.route) { JournalDetailScreen(navController, viewModel) }

                }

                // --- LAYER 2: NAVBAR MELAYANG (Di Depan/Atas) ---
                if (showBottomBar) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter) // Tempel di Bawah Tengah
                            .navigationBarsPadding() // Jaga jarak dari Garis Gesture HP (Edge-to-Edge)

                    ) {
                        BottomNavBar(navController)
                    }
                }
            }
        }
    }
}