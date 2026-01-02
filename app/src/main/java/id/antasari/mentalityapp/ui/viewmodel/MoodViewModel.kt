package id.antasari.mentalityapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.*
import id.antasari.mentalityapp.data.local.JournalEntity
import id.antasari.mentalityapp.data.local.MoodDao
import id.antasari.mentalityapp.data.local.MoodEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoodViewModel(private val moodDao: MoodDao) : ViewModel() {

    // --- MOOD STATE ---
    private val _moodHistory = MutableStateFlow<List<MoodEntity>>(emptyList())
    val moodHistory = _moodHistory.asStateFlow()

    // --- JOURNAL STATE (BARU) ---
    private val _journalHistory = MutableStateFlow<List<JournalEntity>>(emptyList())
    val journalHistory = _journalHistory.asStateFlow()

    var selectedJournal by mutableStateOf<JournalEntity?>(null)

    init {
        fetchMoodHistory()
        fetchJournalHistory() // Panggil fungsi load jurnal
    }

    private fun fetchMoodHistory() {
        viewModelScope.launch {
            moodDao.getAllMoods().collect { logs ->
                _moodHistory.value = logs
            }
        }
    }

    // Load Data Jurnal
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

            // Logic split context (Pertanyaan -> Jawaban)
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

    // --- FUNGSI BARU: SIMPAN JURNAL ---
    fun addJournal(title: String, content: String) {
        viewModelScope.launch {
            val newJournal = JournalEntity(
                title = title.ifBlank { "Untitled" }, // Default kalau judul kosong
                content = content,
                timestamp = System.currentTimeMillis()
            )
            moodDao.insertJournal(newJournal)
        }
    }

    fun updateJournal(journal: JournalEntity, newTitle: String, newContent: String) {
        viewModelScope.launch {
            // Kita bikin copy data lama, tapi judul, isi, dan waktu kita update
            val updatedEntry = journal.copy(
                title = newTitle,
                content = newContent,
                timestamp = System.currentTimeMillis()
            )

            // Simpan perubahan ke Database
            moodDao.updateJournal(updatedEntry)

            // Update data yang lagi dipegang UI sekarang biar ID-nya sinkron
            selectedJournal = updatedEntry
        }
    }

    // --- FUNGSI BARU: HAPUS JURNAL ---
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