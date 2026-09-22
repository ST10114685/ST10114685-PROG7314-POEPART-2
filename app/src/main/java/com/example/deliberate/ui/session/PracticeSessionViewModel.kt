package com.example.deliberate.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deliberate.data.local.entity.PracticeSessionEntity
import com.example.deliberate.data.session.ActiveSessionManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PracticeSessionViewModel(
    private val activeSessionManager: ActiveSessionManager
) : ViewModel() {

    val activeSession: StateFlow<PracticeSessionEntity?> = activeSessionManager.activeSession
    val elapsedSeconds: StateFlow<Long> = activeSessionManager.elapsedSeconds
    val isTimerRunning: StateFlow<Boolean> = activeSessionManager.isTimerRunning
    val isPaused: StateFlow<Boolean> = activeSessionManager.isPaused

    fun startSession(
        moduleId: Long,
        focusAreaId: Long? = null,
        title: String,
        objective: String,
        successCriteria: String,
        targetDurationMinutes: Int,
        onStarted: () -> Unit = {}
    ) {
        viewModelScope.launch {
            activeSessionManager.startSession(
                moduleId = moduleId,
                focusAreaId = focusAreaId,
                title = title,
                objective = objective,
                successCriteria = successCriteria,
                targetDurationMinutes = targetDurationMinutes
            )
            onStarted()
        }
    }

    fun pauseSession() {
        activeSessionManager.pauseTimer()
    }

    fun resumeSession() {
        activeSessionManager.resumeTimer()
    }

    fun completeSession(notes: String = "", onCompleted: (sessionId: Long) -> Unit) {
        viewModelScope.launch {
            val sessionId = activeSessionManager.completeSession(notes)
            onCompleted(sessionId)
        }
    }

    fun cancelSession(onCancelled: () -> Unit = {}) {
        viewModelScope.launch {
            activeSessionManager.cancelSession()
            onCancelled()
        }
    }

    class Factory(
        private val activeSessionManager: ActiveSessionManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PracticeSessionViewModel(activeSessionManager) as T
        }
    }
}
