package id.antasari.mentalityapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_table")
data class JournalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,          // Judul Jurnal (Opsional, bisa otomatis tanggal)
    val content: String,        // Isi Curhatan Panjang
    val moodTag: Int? = null,   // (Opsional) Tag mood saat nulis: 0-4
    val timestamp: Long = System.currentTimeMillis(),
    val imagePaths: List<String> = emptyList(),
    val isArchived: Boolean = false
)