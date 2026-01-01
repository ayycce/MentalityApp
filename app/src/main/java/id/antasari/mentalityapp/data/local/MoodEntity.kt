package id.antasari.mentalityapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_table")
data class MoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val moodIndex: Int,       // 0-4
    val moodLabel: String,    // "Happy", "Sad", dll
    val intensity: Float,     // BARU: 1.0 - 5.0
    val contextPrompt: String? = null, // "Apa yang bikin capek?"
    val contextAnswer: String? = null, // "Kerjaan"
    val timestamp: Long = System.currentTimeMillis()
)