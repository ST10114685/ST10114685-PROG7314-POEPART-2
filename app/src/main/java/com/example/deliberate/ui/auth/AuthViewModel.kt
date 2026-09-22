package com.example.deliberate.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deliberate.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loginWithGoogle() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000) // Simulate network handshake latency
            userPreferencesRepository.updateUserName("Google Practitioner")
            userPreferencesRepository.updateUserEmail("practitioner@google.com")
            userPreferencesRepository.setLoggedIn(true)
            _isLoading.value = false
        }
    }

    fun loginWithGitHub() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000)
            userPreferencesRepository.updateUserName("GitHub Developer")
            userPreferencesRepository.updateUserEmail("developer@github.com")
            userPreferencesRepository.setLoggedIn(true)
            _isLoading.value = false
        }
    }

    fun loginWithEnterprise(email: String) {
        if (email.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            delay(1200)
            val computedName = email.substringBefore("@").replaceFirstChar { it.uppercase() }
            userPreferencesRepository.updateUserName(computedName)
            userPreferencesRepository.updateUserEmail(email.trim())
            userPreferencesRepository.setLoggedIn(true)
            _isLoading.value = false
        }
    }

    class Factory(
        private val userPreferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(userPreferencesRepository) as T
        }
    }
}
