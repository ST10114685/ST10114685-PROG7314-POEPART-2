package com.example.deliberate.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deliberate.data.local.entity.ExperimentEntity
import com.example.deliberate.data.local.entity.LearningModuleEntity
import com.example.deliberate.data.local.entity.PracticeSessionEntity
import com.example.deliberate.data.preferences.UserPreferences
import com.example.deliberate.data.preferences.UserPreferencesRepository
import com.example.deliberate.data.repository.ExperimentRepository
import com.example.deliberate.data.repository.LearningModuleRepository
import com.example.deliberate.data.repository.PracticeSessionRepository
import com.example.deliberate.data.repository.ReflectionRepository
import com.example.deliberate.data.remote.NetworkClient
import com.example.deliberate.data.remote.PracticeDataBackup
import com.example.deliberate.data.remote.SessionPayload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val sessionRepository: PracticeSessionRepository,
    private val reflectionRepository: ReflectionRepository,
    private val moduleRepository: LearningModuleRepository,
    private val experimentRepository: ExperimentRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = userPreferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    val completedSessionCount: StateFlow<Int> = sessionRepository.getCompletedSessionCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val totalPracticeTimeSeconds: StateFlow<Long> = sessionRepository.getTotalPracticeTimeSeconds()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0L
        )

    val reflectionCount: StateFlow<Int> = reflectionRepository.getReflectionCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val averageRating: StateFlow<Float> = reflectionRepository.getAverageRating()
        .map { it ?: 0.0f }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0f
        )

    val recentSessions: StateFlow<List<PracticeSessionEntity>> = sessionRepository.getCompletedSessions()
        .map { list -> list.take(5) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activeModules: StateFlow<List<LearningModuleEntity>> = moduleRepository.getActiveModules()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activeExperiments: StateFlow<List<ExperimentEntity>> = experimentRepository.getExperimentsByStatus("ACTIVE")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _syncStatusMessage = MutableStateFlow("Not Synced")
    val syncStatusMessage: StateFlow<String> = _syncStatusMessage

    fun syncCloudData() {
        viewModelScope.launch {
            _syncStatusMessage.value = "Synchronizing..."
            try {
                val currentPrefs = userPreferences.value
                val sessionsList = sessionRepository.getCompletedSessions().first().take(10)
                val payloads = sessionsList.map {
                    SessionPayload(id = it.id, title = it.title, durationSeconds = it.actualDurationSeconds, startTime = it.startTime)
                }
                val totalMinutes = (totalPracticeTimeSeconds.value / 60).toInt()
                val backup = PracticeDataBackup(
                    userEmail = currentPrefs.userEmail.ifBlank { "anonymous@deliberate.internal" },
                    sessions = payloads,
                    totalMinutes = totalMinutes
                )
                // Trigger full REST client round-trip backup sequence
                val response = NetworkClient.syncService.uploadBackupData(
                    bearerToken = "Bearer mock-cloud-token-secret-12345",
                    backup = backup
                )
                _syncStatusMessage.value = response.message
            } catch (e: Exception) {
                _syncStatusMessage.value = "Synced Locally (Cloud Pending)"
            }
        }
    }

    class Factory(
        private val sessionRepository: PracticeSessionRepository,
        private val reflectionRepository: ReflectionRepository,
        private val moduleRepository: LearningModuleRepository,
        private val experimentRepository: ExperimentRepository,
        private val userPreferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DashboardViewModel(
                sessionRepository,
                reflectionRepository,
                moduleRepository,
                experimentRepository,
                userPreferencesRepository
            ) as T
        }
    }
}
