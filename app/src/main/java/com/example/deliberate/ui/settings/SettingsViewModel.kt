package com.example.deliberate.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deliberate.data.preferences.UserPreferences
import com.example.deliberate.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = userPreferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun updateUserName(name: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateUserName(name.trim())
        }
    }

    fun updateUserEmail(email: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateUserEmail(email.trim())
        }
    }

    fun updateThemeMode(themeMode: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateThemeMode(themeMode)
        }
    }

    fun updateDailyGoalMinutes(minutes: Int) {
        viewModelScope.launch {
            userPreferencesRepository.updateDailyGoalMinutes(minutes)
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateNotificationsEnabled(enabled)
        }
    }

    fun resetPreferences() {
        viewModelScope.launch {
            userPreferencesRepository.clearUserPreferences()
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.setLoggedIn(false)
        }
    }

    class Factory(
        private val userPreferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(userPreferencesRepository) as T
        }
    }
}
