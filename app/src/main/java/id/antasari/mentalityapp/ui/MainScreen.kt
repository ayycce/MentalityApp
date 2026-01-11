package id.antasari.mentalityapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.antasari.mentalityapp.ui.components.BottomNavBar
import id.antasari.mentalityapp.ui.navigation.Screen
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.viewmodel.MoodViewModel
import id.antasari.mentalityapp.ui.screens.*
import id.antasari.mentalityapp.ui.viewmodel.UserViewModel

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: MoodViewModel,
    userViewModel: UserViewModel = viewModel()
) {
    val userName by userViewModel.userName.collectAsState()
    // 1. Cek Route Saat Ini
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 2. Tentukan Kapan Navbar Muncul
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Vent.route,
        Screen.Profile.route, // Profile adalah Main Menu
        Screen.Garden.route,
        Screen.More.route // Insight/More
    )

    // 3. LAYOUT UTAMA
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGradient)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = Color.Black
        ) { innerPadding ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {

                // --- LAYER 1: KONTEN (Di Belakang) ---
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.padding(
                        // Padding bawah agar konten paling bawah tidak ketutup navbar
                        bottom = if (showBottomBar) 100.dp else 0.dp
                    )
                ) {
                    // --- HALAMAN UTAMA (Main Tabs) ---
                    composable(Screen.Home.route) {
                        HomeScreen(
                            userName = userName, // Parameter baru
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                    composable(Screen.Vent.route) { DailyDumpScreen(navController, viewModel) }
                    composable(Screen.More.route) { MoreScreen(navController) } // Insight
                    composable(Screen.Profile.route) {
                        // ✅ Tambahkan parameter viewModel di sini
                        ProfileScreen(navController = navController, viewModel = viewModel)
                    }
                    // Kalau Feed & Garden belum ada filenya, comment dulu biar gak error
                    composable(Screen.Feed.route) { FeedScreen() }
                    composable(Screen.Garden.route) { GardenScreen() }

                    // --- HALAMAN DETAIL / SUB-MENU ---
                    composable(Screen.Archive.route) { ArchiveScreen(navController, viewModel) } // 🔥 JANGAN LUPA INI
                    composable(Screen.Breathing.route) { BreathingScreen(navController) }
                    composable(Screen.CheckinHistory.route) { CheckinHistoryScreen(navController, viewModel) }
                    composable(Screen.JournalDetail.route) { JournalDetailScreen(navController, viewModel) }
                }

                // --- LAYER 2: NAVBAR (Di Depan/Atas) ---
                if (showBottomBar) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding() // Penting buat HP layarnya full
                            .padding(bottom = 20.dp) // Kasih jarak dikit dari bawah biar melayang cantik
                    ) {
                        BottomNavBar(navController)
                    }
                }
            }
        }
    }
}