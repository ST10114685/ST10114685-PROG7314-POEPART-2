package com.example.deliberate.ui.focusarea

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deliberate.data.local.entity.FocusAreaEntity
import com.example.deliberate.data.local.entity.PracticeSessionEntity
import com.example.deliberate.data.repository.FocusAreaRepository
import com.example.deliberate.data.repository.PracticeSessionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class FocusAreaViewModel(
    private val focusAreaRepository: FocusAreaRepository,
    private val sessionRepository: PracticeSessionRepository
) : ViewModel() {

    private val _focusAreaId = MutableStateFlow<Long?>(null)
    val focusAreaId: StateFlow<Long?> = _focusAreaId.asStateFlow()

    val focusArea: StateFlow<FocusAreaEntity?> = _focusAreaId
        .flatMapLatest { id ->
            if (id != null && id > 0) focusAreaRepository.getFocusAreaById(id)
            else flowOf(null)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val sessions: StateFlow<List<PracticeSessionEntity>> = _focusAreaId
        .flatMapLatest { id ->
            if (id != null && id > 0) sessionRepository.getSessionsForFocusArea(id)
            else flowOf(emptyList())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun loadFocusArea(id: Long) {
        _focusAreaId.value = id
    }

    fun updateCurrentRating(newRating: Int) {
        viewModelScope.launch {
            val currentId = _focusAreaId.value ?: return@launch
            focusAreaRepository.updateFocusAreaRating(currentId, newRating)
        }
    }

    fun updateStatus(newStatus: String) {
        viewModelScope.launch {
            val current = focusArea.value ?: return@launch
            focusAreaRepository.updateFocusArea(current.copy(status = newStatus))
        }
    }

    fun deleteFocusArea(onDeleted: () -> Unit) {
        viewModelScope.launch {
            val currentId = _focusAreaId.value ?: return@launch
            focusAreaRepository.deleteFocusAreaById(currentId)
            onDeleted()
        }
    }

    class Factory(
        private val focusAreaRepository: FocusAreaRepository,
        private val sessionRepository: PracticeSessionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FocusAreaViewModel(focusAreaRepository, sessionRepository) as T
        }
    }
}
