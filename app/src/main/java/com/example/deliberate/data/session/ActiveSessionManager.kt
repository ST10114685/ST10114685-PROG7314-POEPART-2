package com.example.deliberate.data.session

import com.example.deliberate.data.local.entity.PracticeSessionEntity
import com.example.deliberate.data.repository.PracticeSessionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActiveSessionManager(
    private val sessionRepository: PracticeSessionRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) {

    private val _activeSession = MutableStateFlow<PracticeSessionEntity?>(null)
    val activeSession: StateFlow<PracticeSessionEntity?> = _activeSession.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0L)
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private var timerJob: Job? = null

    suspend fun startSession(
        moduleId: Long,
        focusAreaId: Long? = null,
        title: String,
        objective: String,
        successCriteria: String,
        targetDurationMinutes: Int
    ): PracticeSessionEntity {
        val newSession = PracticeSessionEntity(
            moduleId = moduleId,
            focusAreaId = if (focusAreaId != null && focusAreaId > 0) focusAreaId else null,
            title = title.ifBlank { "Practice Session" },
            objective = objective,
            successCriteria = successCriteria,
            targetDurationMinutes = targetDurationMinutes,
            actualDurationSeconds = 0,
            status = "IN_PROGRESS",
            startTime = System.currentTimeMillis()
        )

        val id = sessionRepository.insertSession(newSession)
        val sessionWithId = newSession.copy(id = id)

        _activeSession.value = sessionWithId
        _elapsedSeconds.value = 0L
        _isPaused.value = false

        startTimerInternal()
        return sessionWithId
    }

    fun pauseTimer() {
        if (_isTimerRunning.value && !_isPaused.value) {
            timerJob?.cancel()
            _isTimerRunning.value = false
            _isPaused.value = true
        }
    }

    fun resumeTimer() {
        if (_isPaused.value || !_isTimerRunning.value) {
            _isPaused.value = false
            startTimerInternal()
        }
    }

    private fun startTimerInternal() {
        timerJob?.cancel()
        _isTimerRunning.value = true
        timerJob = scope.launch {
            while (_isTimerRunning.value) {
                delay(1000L)
                _elapsedSeconds.value += 1
                
                // Periodically update DB with current elapsed time every 10 seconds
                val current = _activeSession.value
                if (current != null && _elapsedSeconds.value % 10 == 0L) {
                    sessionRepository.updateSession(
                        current.copy(actualDurationSeconds = _elapsedSeconds.value)
                    )
                }
            }
        }
    }

    suspend fun completeSession(notes: String = ""): Long {
        timerJob?.cancel()
        _isTimerRunning.value = false
        _isPaused.value = false

        val session = _activeSession.value
        val completedId: Long
        if (session != null) {
            val completedSession = session.copy(
                actualDurationSeconds = _elapsedSeconds.value,
                status = "COMPLETED",
                endTime = System.currentTimeMillis(),
                notes = notes
            )
            sessionRepository.updateSession(completedSession)
            completedId = session.id
        } else {
            completedId = -1L
        }

        _activeSession.value = null
        _elapsedSeconds.value = 0L
        return completedId
    }

    suspend fun cancelSession() {
        timerJob?.cancel()
        _isTimerRunning.value = false
        _isPaused.value = false

        val session = _activeSession.value
        if (session != null) {
            val cancelledSession = session.copy(
                actualDurationSeconds = _elapsedSeconds.value,
                status = "CANCELLED",
                endTime = System.currentTimeMillis()
            )
            sessionRepository.updateSession(cancelledSession)
        }

        _activeSession.value = null
        _elapsedSeconds.value = 0L
    }

    companion object {
        @Volatile
        private var INSTANCE: ActiveSessionManager? = null

        fun getInstance(sessionRepository: PracticeSessionRepository): ActiveSessionManager {
            return INSTANCE ?: synchronized(this) {
                val instance = INSTANCE ?: ActiveSessionManager(sessionRepository)
                INSTANCE = instance
                instance
            }
        }
    }
}
