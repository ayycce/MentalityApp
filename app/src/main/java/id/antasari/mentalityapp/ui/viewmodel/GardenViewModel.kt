package id.antasari.mentalityapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.antasari.mentalityapp.data.local.MentalityDatabase // ⚠️ Pastikan nama ini sesuai file database kamu (AppDatabase/MentalityDatabase)
import id.antasari.mentalityapp.data.local.GardenEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class GardenViewModel(application: Application) : AndroidViewModel(application) {
    // Akses DAO (Pastikan nama class Database sesuai)
    private val dao = MentalityDatabase.getDatabase(application).moodDao()

    // STATE GARDEN (XP, Level, Token)
    private val _gardenState = MutableStateFlow(GardenEntity())
    val gardenState = _gardenState.asStateFlow()

    // STATISTIK MINGGUAN (Senin: Hadir, Selasa: Absen, dst)
    private val _weeklyStats = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList())
    val weeklyStats = _weeklyStats.asStateFlow()

    // 🔥 SATU INIT BLOCK UNTUK SEMUA 🔥
    init {
        loadGarden()
        loadWeeklyStats()
    }

    // ----------------------------------------------------------------
    // BAGIAN 1: LOGIKA GARDEN (XP & TOKEN)
    // ----------------------------------------------------------------

    private fun loadGarden() {
        viewModelScope.launch {
            dao.getGarden().collect { garden ->
                if (garden == null) {
                    val newGarden = GardenEntity(pendingDailyReward = true) // User baru dapet welcome gift
                    dao.insertGarden(newGarden)
                } else {
                    checkDailyReset(garden)
                }
            }
        }
    }

    // 1️⃣ LOGIC RESET HARIAN (Otomatis dapet 1 Token per hari)
    private suspend fun checkDailyReset(garden: GardenEntity) {
        if (!isSameDay(garden.lastDailyReset)) {
            val updated = garden.copy(
                waterTokens = (garden.waterTokens + 1).coerceAtMost(5), // Max nimbun 5 token
                lastDailyReset = System.currentTimeMillis(),
                pendingDailyReward = true
            )
            dao.insertGarden(updated)
        } else {
            _gardenState.value = garden
        }
    }

    // 2️⃣ LOGIC MOOD CHECK (Panggil saat save Mood)
    fun claimMoodToken() {
        viewModelScope.launch {
            val current = _gardenState.value
            if (!isSameDay(current.lastMoodTokenDate)) {
                val updated = current.copy(
                    waterTokens = current.waterTokens + 1,
                    lastMoodTokenDate = System.currentTimeMillis(),
                    pendingMoodReward = true
                )
                dao.insertGarden(updated)
            }
        }
    }

    // 3️⃣ LOGIC JOURNAL (Panggil saat save Jurnal)
    fun claimJournalToken() {
        viewModelScope.launch {
            val current = _gardenState.value
            if (!isSameDay(current.lastJournalTokenDate)) {
                val updated = current.copy(
                    waterTokens = current.waterTokens + 1,
                    lastJournalTokenDate = System.currentTimeMillis(),
                    pendingJournalReward = true
                )
                dao.insertGarden(updated)
            }
        }
    }

    // 4️⃣ BERSIH-BERSIH POPUP (Panggil setelah animasi selesai)
    fun clearPendingRewards() {
        viewModelScope.launch {
            val current = _gardenState.value
            if (current.pendingDailyReward || current.pendingMoodReward || current.pendingJournalReward) {
                dao.insertGarden(current.copy(
                    pendingDailyReward = false,
                    pendingMoodReward = false,
                    pendingJournalReward = false
                ))
            }
        }
    }

    // 5️⃣ SIRAM TANAMAN
    fun waterPlant() {
        val current = _gardenState.value
        if (current.waterTokens > 0) {
            val newXp = current.currentXp + 20
            val newLevel = (newXp / 100) + 1
            viewModelScope.launch {
                dao.insertGarden(current.copy(
                    currentXp = newXp,
                    waterTokens = current.waterTokens - 1,
                    level = newLevel
                ))
            }
        }
    }

    // ----------------------------------------------------------------
    // BAGIAN 2: STATISTIK MINGGUAN (MOOD + JOURNAL)
    // ----------------------------------------------------------------

    private fun loadWeeklyStats() {
        viewModelScope.launch {
            // 🔥 MENGGABUNGKAN DATA MOOD & JURNAL
            // Definisi "Hadir" = Mengisi Mood ATAU Mengisi Jurnal
            combine(
                dao.getAllMoods(),
                dao.getAllJournals()
            ) { moods, journals ->
                // 1. Kumpulkan semua timestamp
                val allTimestamps = moods.map { it.timestamp } + journals.map { it.timestamp }

                // 2. Normalisasi ke tanggal (jam 00:00) & Hapus duplikat
                val activeDays = allTimestamps.map { timestamp ->
                    val c = Calendar.getInstance().apply { timeInMillis = timestamp }
                    c.set(Calendar.HOUR_OF_DAY, 0)
                    c.set(Calendar.MINUTE, 0)
                    c.set(Calendar.SECOND, 0)
                    c.set(Calendar.MILLISECOND, 0)
                    c.timeInMillis
                }.toSet()

                // 3. Loop 7 Hari Terakhir
                val stats = mutableListOf<Pair<String, Boolean>>()
                val calendar = Calendar.getInstance()

                // Reset hari ini ke jam 00:00
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                // Mundur 6 hari ke belakang
                calendar.add(Calendar.DAY_OF_YEAR, -6)

                // Loop maju sampai hari ini
                for (i in 0..6) {
                    val dateToCheck = calendar.timeInMillis

                    // Ambil nama hari (M, T, W...)
                    val dayName = java.text.SimpleDateFormat("EEEEE", Locale.ENGLISH).format(dateToCheck)

                    // Cek kehadiran
                    val isActive = activeDays.contains(dateToCheck)

                    stats.add(dayName to isActive)

                    // Lanjut besok
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }
                stats
            }.collect { result ->
                _weeklyStats.value = result
            }
        }
    }

    // ----------------------------------------------------------------
    // HELPER
    // ----------------------------------------------------------------
    private fun isSameDay(timestamp: Long): Boolean {
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }
        val today = Calendar.getInstance()
        return date.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                date.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    }
}