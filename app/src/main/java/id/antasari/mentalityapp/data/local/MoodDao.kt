package id.antasari.mentalityapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {
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
}