package id.antasari.mentalityapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.antasari.mentalityapp.data.local.MoodDao
import id.antasari.mentalityapp.data.local.MoodEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoodViewModel(private val moodDao: MoodDao) : ViewModel() {

    // StateFlow untuk menampung list history
    private val _moodHistory = MutableStateFlow<List<MoodEntity>>(emptyList())
    val moodHistory = _moodHistory.asStateFlow()

    init {
        // Load data awal saat ViewModel dibuat
        fetchMoodHistory()
    }

    private fun fetchMoodHistory() {
        viewModelScope.launch {
            moodDao.getAllMoods().collect { logs ->
                _moodHistory.value = logs
            }
        }
    }

    // 🔥 FUNGSI ADD DIPERBARUI
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

            // Pisahkan Pertanyaan dan Jawaban (Format: "Pertanyaan -> Jawaban")
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
                intensity = intensity, // Simpan slider value
                contextPrompt = prompt,
                contextAnswer = answer,
                timestamp = System.currentTimeMillis()
            )
            moodDao.insertMood(newMood)
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