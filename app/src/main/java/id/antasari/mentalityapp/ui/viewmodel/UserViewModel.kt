package id.antasari.mentalityapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.antasari.mentalityapp.data.local.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = UserPreferences(application)

    // Data Nama (Siap pakai di UI)
    val userName = prefs.userName.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "there"
    )

    // Status First Run
    val isFirstRun = prefs.isFirstRun.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        true
    )

    // Data Avatar
    val userAvatar = prefs.userAvatar.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ""
    )

    fun saveName(name: String) {
        viewModelScope.launch {
            prefs.saveUserName(name)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            prefs.setOnboardingCompleted()
        }
    }

    fun saveAvatar(avatar: String) {
        viewModelScope.launch {
            prefs.saveUserAvatar(avatar)
        }
    }
}