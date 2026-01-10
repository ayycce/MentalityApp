package id.antasari.mentalityapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [MoodEntity::class, JournalEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MentalityDatabase : RoomDatabase() {

    abstract fun moodDao(): MoodDao

    companion object {
        @Volatile
        private var INSTANCE: MentalityDatabase? = null

        fun getDatabase(context: Context): MentalityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MentalityDatabase::class.java,
                    "mentality_database"
                )
                    .fallbackToDestructiveMigration() // Aktifkan ini kalau error versi DB pas development
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}