package id.antasari.mentalityapp.data.local

import androidx.room.*
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {

    // 1. Buat Halaman Utama (Vent) -> Ambil yg isArchived = 0 (False)
    @Query("SELECT * FROM journal_table WHERE isArchived = 0 ORDER BY timestamp DESC")
    fun getActiveJournals(): Flow<List<JournalEntity>>

    // 2. Buat Halaman Arsip -> Ambil yg isArchived = 1 (True)
    @Query("SELECT * FROM journal_table WHERE isArchived = 1 ORDER BY timestamp DESC")
    fun getArchivedJournals(): Flow<List<JournalEntity>>

    // 1. Simpan Mood Baru
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(mood: MoodEntity)

    // 2. Ambil Semua History (Urutkan dari yang terbaru)
    // Kita pakai Flow biar datanya "Live" (kalau ada update, UI langsung berubah)
    @Query("SELECT * FROM mood_table ORDER BY timestamp DESC")
    fun getAllMoods(): Flow<List<MoodEntity>>

    // 3. Ambil Mood Hari Ini (Untuk mencegah double check-in nanti - Phase 2)
    @Query("SELECT * FROM mood_table WHERE timestamp >= :startOfDay AND timestamp <= :endOfDay")
    fun getMoodsByDateRange(startOfDay: Long, endOfDay: Long): Flow<List<MoodEntity>>

    // 4. Hapus Semua (Buat testing/reset)
    @Query("DELETE FROM mood_table")
    suspend fun clearAll()

    // --- BAGIAN JURNAL (BARU) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(journal: JournalEntity)

    @Update
    suspend fun updateJournal(journal: JournalEntity)

    @Delete
    suspend fun deleteJournal(journal: JournalEntity)

    // Ambil semua jurnal, urutkan dari yang terbaru
    @Query("SELECT * FROM journal_table ORDER BY timestamp DESC")
    fun getAllJournals(): Flow<List<JournalEntity>>
}