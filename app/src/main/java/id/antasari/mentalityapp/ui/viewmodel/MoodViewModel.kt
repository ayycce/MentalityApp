package id.antasari.mentalityapp.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.antasari.mentalityapp.data.local.JournalEntity
import id.antasari.mentalityapp.data.local.MoodDao
import id.antasari.mentalityapp.data.local.MoodEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class MoodViewModel(private val moodDao: MoodDao) : ViewModel() {



    // --- MOOD STATE ---
    private val _moodHistory = MutableStateFlow<List<MoodEntity>>(emptyList())
    val moodHistory = _moodHistory.asStateFlow()

    // --- JOURNAL STATE ---

    // --- STATE JURNAL AKTIF (Untuk Halaman Vent) ---
    private val _activeJournals = MutableStateFlow<List<JournalEntity>>(emptyList())
    val activeJournals = _activeJournals.asStateFlow()

    // --- STATE JURNAL ARSIP (Untuk Halaman Arsip Baru) ---
    private val _archivedJournals = MutableStateFlow<List<JournalEntity>>(emptyList())
    val archivedJournals = _archivedJournals.asStateFlow()

    init {
        fetchJournals()
    }

    private fun fetchJournals() {
        viewModelScope.launch {
            // Ambil yang Aktif
            launch {
                moodDao.getActiveJournals().collect { _activeJournals.value = it }
            }
            // Ambil yang Arsip
            launch {
                moodDao.getArchivedJournals().collect { _archivedJournals.value = it }
            }
        }
    }

    // --- FUNGSI PINDAHKAN KE ARSIP / KEMBALIKAN ---
    fun toggleArchiveStatus(journal: JournalEntity) {
        viewModelScope.launch {
            // Kalau true jadi false, kalau false jadi true (Switch)
            val updatedJournal = journal.copy(isArchived = !journal.isArchived)
            moodDao.updateJournal(updatedJournal)
        }
    }
    private val _journalHistory = MutableStateFlow<List<JournalEntity>>(emptyList())
    val journalHistory = _journalHistory.asStateFlow()

    var selectedJournal by mutableStateOf<JournalEntity?>(null)

    init {
        fetchMoodHistory()
        fetchJournalHistory()
    }

    private fun fetchMoodHistory() {
        viewModelScope.launch {
            moodDao.getAllMoods().collect { logs ->
                _moodHistory.value = logs
            }
        }
    }

    private fun fetchJournalHistory() {
        viewModelScope.launch {
            moodDao.getAllJournals().collect { journals ->
                _journalHistory.value = journals
            }
        }
    }

    // Fungsi Add Mood (Tetap Sama)
    fun addMood(index: Int, intensity: Float, context: String?) {
        viewModelScope.launch {
            val label = when (index) {
                0 -> "Sad"
                1 -> "Upset"
                2 -> "Neutral"
                3 -> "Happy"
                4 -> "Excited"
                else -> "Neutral"
            }

            val prompt: String?
            val answer: String?
            if (context != null && context.contains("->")) {
                val parts = context.split("->")
                prompt = parts.getOrNull(0)?.trim()
                answer = parts.getOrNull(1)?.trim()
            } else {
                prompt = null
                answer = context
            }

            val newMood = MoodEntity(
                moodIndex = index,
                moodLabel = label,
                intensity = intensity,
                contextPrompt = prompt,
                contextAnswer = answer,
                timestamp = System.currentTimeMillis()
            )
            moodDao.insertMood(newMood)
        }
    }

    // ---------------------------------------------------------
    // 🔥 BAGIAN YANG DI-UPDATE UNTUK FOTO 🔥
    // ---------------------------------------------------------

    // 1. Helper: Simpan foto fisik ke folder aplikasi
    private fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            // Buat nama file unik
            val fileName = "IMG_${UUID.randomUUID()}.jpg"

            // Buka aliran data dari foto asli
            val inputStream = context.contentResolver.openInputStream(uri)

            // Siapkan folder tujuan
            val outputDir = File(context.filesDir, "journal_images")
            if (!outputDir.exists()) outputDir.mkdirs() // Bikin folder kalau belum ada

            // Siapkan file tujuan
            val outputFile = File(outputDir, fileName)

            // Copy data
            val outputStream = FileOutputStream(outputFile)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            // Kembalikan alamat file barunya (Path)
            outputFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 2. Update Fungsi Add Journal: Terima Context & List URI
    fun addJournal(
        context: Context,   // Tambahan: Butuh context buat akses file
        title: String,
        content: String,
        uris: List<Uri>     // Tambahan: List foto dari UI
    ) {
        // Gunakan Dispatchers.IO karena proses copy file itu berat
        viewModelScope.launch(Dispatchers.IO) {

            // A. Proses simpan semua foto dulu
            val savedPaths = uris.mapNotNull { uri ->
                saveImageToInternalStorage(context, uri)
            }

            // B. Simpan ke Database
            val newJournal = JournalEntity(
                title = title.ifBlank { "Untitled" },
                content = content,
                timestamp = System.currentTimeMillis(),
                imagePaths = savedPaths // 🔥 Masukkan path foto ke sini
            )
            moodDao.insertJournal(newJournal)
        }
    }

    // Update Journal (Untuk saat ini teks saja, foto lama dipertahankan)
    fun updateJournal(journal: JournalEntity, newTitle: String, newContent: String) {
        viewModelScope.launch {
            val updatedEntry = journal.copy(
                title = newTitle,
                content = newContent,
                timestamp = System.currentTimeMillis()
                // imagePaths tidak diubah, jadi foto lama tetap aman
            )
            moodDao.updateJournal(updatedEntry)
            selectedJournal = updatedEntry
        }
    }

    fun deleteJournal(journal: JournalEntity) {
        viewModelScope.launch {
            moodDao.deleteJournal(journal)
        }
    }
}

// Factory (Tetap sama)
class MoodViewModelFactory(private val dao: MoodDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoodViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MoodViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}