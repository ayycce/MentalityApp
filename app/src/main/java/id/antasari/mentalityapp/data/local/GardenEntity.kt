package id.antasari.mentalityapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "garden_table")
data class GardenEntity(
    @PrimaryKey val id: Int = 1,
    val currentXp: Int = 0,
    val waterTokens: Int = 1,
    val level: Int = 1,

    // 🔥 PENCATATAN WAKTU (Agar tidak spam token)
    val lastDailyReset: Long = System.currentTimeMillis(),
    val lastMoodTokenDate: Long = 0L,   // Kapan terakhir dapat dari Mood
    val lastJournalTokenDate: Long = 0L, // Kapan terakhir dapat dari Jurnal

    // 🔥 SISTEM ANTRIAN HADIAH (Untuk Animasi saat masuk Garden)
    // Kalau true, berarti nanti pas masuk Garden bakal muncul popup
    val pendingDailyReward: Boolean = false,
    val pendingMoodReward: Boolean = false,
    val pendingJournalReward: Boolean = false
)