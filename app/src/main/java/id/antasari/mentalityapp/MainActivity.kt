package id.antasari.mentalityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import id.antasari.mentalityapp.data.local.MentalityDatabase
import id.antasari.mentalityapp.ui.MainScreen
import id.antasari.mentalityapp.ui.navigation.Screen
import id.antasari.mentalityapp.ui.screens.NameInputScreen
import id.antasari.mentalityapp.ui.screens.SplashScreen
import id.antasari.mentalityapp.ui.screens.WelcomeScreen
import id.antasari.mentalityapp.ui.theme.MentalityAppTheme
import id.antasari.mentalityapp.ui.viewmodel.MoodViewModel
import id.antasari.mentalityapp.ui.viewmodel.MoodViewModelFactory
import id.antasari.mentalityapp.ui.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Inisialisasi Database & Factory
        val database = MentalityDatabase.getDatabase(applicationContext)
        val viewModelFactory = MoodViewModelFactory(database.moodDao())

        setContent {
            MentalityAppTheme {
                // 2. Siapkan Controller & ViewModels
                val rootNavController = rememberNavController() // Navigasi Utama (Splash -> App)
                val moodViewModel: MoodViewModel = viewModel(factory = viewModelFactory)
                val userViewModel: UserViewModel = viewModel() // 🔥 ViewModel User (DataStore)

                // 3. SETUP NAVIGASI ROOT
                // Ini mengatur perpindahan dari Splash -> Onboarding -> Aplikasi Utama
                NavHost(
                    navController = rootNavController,
                    startDestination = Screen.Splash.route // 🔥 Mulai dari Splash
                ) {
                    // A. Flow Splash & Onboarding
                    composable(Screen.Splash.route) {
                        SplashScreen(navController = rootNavController, userViewModel = userViewModel)
                    }
                    composable(Screen.NameInput.route) {
                        NameInputScreen(navController = rootNavController, userViewModel = userViewModel)
                    }
                    composable(Screen.Welcome.route) {
                        WelcomeScreen(navController = rootNavController, userViewModel = userViewModel)
                    }

                    // B. Flow Utama Aplikasi (MainScreen)
                    // Saat masuk ke sini, kita panggil MainScreen yang di dalamnya ada NavHost sendiri untuk Tab Bar
                    composable(Screen.Home.route) {
                        // Kita buat NavController BARU khusus untuk MainScreen (BottomBar)
                        // Agar sejarah navigasi Splash tidak tercampur dengan tab Home/Garden
                        MainScreen(
                            navController = rememberNavController(),
                            viewModel = moodViewModel,
                            userViewModel = userViewModel // Kirim ini agar Home bisa baca Nama User
                        )
                    }
                }
            }
        }
    }
}