package id.antasari.mentalityapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Buat ekstensi context agar mudah dipanggil
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val USER_AVATAR_KEY = stringPreferencesKey("user_avatar")
        val IS_FIRST_RUN_KEY = booleanPreferencesKey("is_first_run")
    }

    // Ambil Avatar (Default-nya kosong "" artinya pakai inisial huruf)
    val userAvatar: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USER_AVATAR_KEY] ?: ""
        }

    // Simpan Avatar
    suspend fun saveUserAvatar(avatar: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_AVATAR_KEY] = avatar
        }
    }

    // Ambil Data Nama (Flow biar real-time)
    val userName: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USER_NAME_KEY] ?: "there" // Default kalau kosong
        }

    // Ambil Status First Run
    val isFirstRun: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_FIRST_RUN_KEY] ?: true // Default TRUE (artinya user baru)
        }

    // Simpan Nama
    suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = if (name.isBlank()) "there" else name
        }
    }

    // Tandai Onboarding Selesai
    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { preferences ->
            preferences[IS_FIRST_RUN_KEY] = false
        }
    }
}