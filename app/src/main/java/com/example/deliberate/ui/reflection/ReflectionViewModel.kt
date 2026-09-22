package com.example.deliberate.ui.reflection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deliberate.data.local.entity.LessonEntity
import com.example.deliberate.data.local.entity.PracticeSessionEntity
import com.example.deliberate.data.local.entity.ReflectionEntity
import com.example.deliberate.data.preferences.UserPreferencesRepository
import com.example.deliberate.data.repository.LearningModuleRepository
import com.example.deliberate.data.repository.LessonRepository
import com.example.deliberate.data.repository.PracticeSessionRepository
import com.example.deliberate.data.repository.ReflectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReflectionViewModel(
    private val reflectionRepository: ReflectionRepository,
    private val sessionRepository: PracticeSessionRepository,
    private val moduleRepository: LearningModuleRepository,
    private val lessonRepository: LessonRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _currentSession = MutableStateFlow<PracticeSessionEntity?>(null)
    val currentSession: StateFlow<PracticeSessionEntity?> = _currentSession.asStateFlow()

    fun loadSession(sessionId: Long) {
        viewModelScope.launch {
            if (sessionId > 0) {
                _currentSession.value = sessionRepository.getSessionByIdDirect(sessionId)
            }
        }
    }

    fun submitReflection(
        sessionId: Long?,
        moduleId: Long,
        focusAreaId: Long?,
        rating: Int,
        whatWentWell: String,
        whatToImprove: String,
        keyTakeaway: String,
        saveAsLesson: Boolean,
        lessonCategory: String = "General",
        onCompleted: () -> Unit
    ) {
        viewModelScope.launch {
            val reflection = ReflectionEntity(
                sessionId = sessionId,
                moduleId = moduleId,
                focusAreaId = focusAreaId,
                rating = rating.coerceIn(1, 10),
                whatWentWell = whatWentWell.trim(),
                whatToImprove = whatToImprove.trim(),
                keyTakeaway = keyTakeaway.trim(),
                createdAt = System.currentTimeMillis()
            )

            val reflectionId = reflectionRepository.insertReflection(reflection)

            // Update module current rating if user provided high/new rating
            val module = moduleRepository.getModuleByIdDirect(moduleId)
            if (module != null) {
                val updatedRating = ((module.currentRating + rating) / 2).coerceIn(1, 10)
                moduleRepository.updateModuleRating(moduleId, updatedRating)
            }

            // Save as personal lesson if selected
            if (saveAsLesson && keyTakeaway.isNotBlank()) {
                val lesson = LessonEntity(
                    moduleId = moduleId,
                    focusAreaId = focusAreaId,
                    reflectionId = reflectionId,
                    title = keyTakeaway.take(50),
                    lessonText = keyTakeaway,
                    category = lessonCategory,
                    createdAt = System.currentTimeMillis()
                )
                lessonRepository.insertLesson(lesson)
            }

            try {
                val prefs = userPreferencesRepository.userPreferencesFlow.first()
                userPreferencesRepository.updateStreak(prefs.currentStreakDays + 1, System.currentTimeMillis())
            } catch (_: Exception) {}

            onCompleted()
        }
    }

    class Factory(
        private val reflectionRepository: ReflectionRepository,
        private val sessionRepository: PracticeSessionRepository,
        private val moduleRepository: LearningModuleRepository,
        private val lessonRepository: LessonRepository,
        private val userPreferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReflectionViewModel(
                reflectionRepository,
                sessionRepository,
                moduleRepository,
                lessonRepository,
                userPreferencesRepository
            ) as T
        }
    }
}
