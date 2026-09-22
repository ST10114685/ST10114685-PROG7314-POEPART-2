package com.example.deliberate.ui.lessons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deliberate.data.local.entity.LessonEntity
import com.example.deliberate.data.repository.LessonRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LessonViewModel(
    private val lessonRepository: LessonRepository
) : ViewModel() {

    val allLessons: StateFlow<List<LessonEntity>> = lessonRepository.getAllLessons()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteLessons: StateFlow<List<LessonEntity>> = lessonRepository.getFavoriteLessons()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addLesson(
        title: String,
        lessonText: String,
        category: String = "General"
    ) {
        viewModelScope.launch {
            val lesson = LessonEntity(
                title = title.trim(),
                lessonText = lessonText.trim(),
                category = category.trim().ifBlank { "General" },
                createdAt = System.currentTimeMillis()
            )
            lessonRepository.insertLesson(lesson)
        }
    }

    fun toggleFavorite(lessonId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            lessonRepository.updateFavoriteStatus(lessonId, isFavorite)
        }
    }

    fun deleteLesson(lessonId: Long) {
        viewModelScope.launch {
            lessonRepository.deleteLessonById(lessonId)
        }
    }

    class Factory(
        private val lessonRepository: LessonRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LessonViewModel(lessonRepository) as T
        }
    }
}
