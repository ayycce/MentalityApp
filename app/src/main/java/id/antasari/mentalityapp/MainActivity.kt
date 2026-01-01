package id.antasari.mentalityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import id.antasari.mentalityapp.data.local.MentalityDatabase
import id.antasari.mentalityapp.ui.MainScreen // Pastikan ini ter-import!
import id.antasari.mentalityapp.ui.theme.MentalityAppTheme
import id.antasari.mentalityapp.ui.viewmodel.MoodViewModel
import id.antasari.mentalityapp.ui.viewmodel.MoodViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Inisialisasi Database
        val database = MentalityDatabase.getDatabase(applicationContext)
        val viewModelFactory = MoodViewModelFactory(database.moodDao())

        setContent {
            MentalityAppTheme {
                // 2. Siapkan Controller & ViewModel
                val navController = rememberNavController()
                val moodViewModel: MoodViewModel = viewModel(factory = viewModelFactory)

                // 3. PANGGIL MAIN SCREEN (Wrapper Utama)
                // Jangan panggil NavHost di sini, karena NavHost sudah ada di dalam MainScreen
                MainScreen(
                    navController = navController,
                    viewModel = moodViewModel
                )
            }
        }
    }
}