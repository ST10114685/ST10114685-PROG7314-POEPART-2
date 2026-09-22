package com.example.deliberate.ui.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deliberate.data.local.entity.FocusAreaEntity
import com.example.deliberate.data.local.entity.LearningModuleEntity
import com.example.deliberate.data.repository.FocusAreaRepository
import com.example.deliberate.data.repository.LearningModuleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class LearningModuleViewModel(
    private val moduleRepository: LearningModuleRepository,
    private val focusAreaRepository: FocusAreaRepository
) : ViewModel() {

    val activeModules: StateFlow<List<LearningModuleEntity>> = moduleRepository.getActiveModules()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedModuleId = MutableStateFlow<Long?>(null)
    val selectedModuleId: StateFlow<Long?> = _selectedModuleId.asStateFlow()

    val selectedModule: StateFlow<LearningModuleEntity?> = _selectedModuleId
        .flatMapLatest { id ->
            if (id != null && id > 0) moduleRepository.getModuleById(id)
            else flowOf(null)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val selectedModuleFocusAreas: StateFlow<List<FocusAreaEntity>> = _selectedModuleId
        .flatMapLatest { id ->
            if (id != null && id > 0) focusAreaRepository.getFocusAreasForModule(id)
            else flowOf(emptyList())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectModule(moduleId: Long?) {
        _selectedModuleId.value = moduleId
    }

    fun addModule(
        title: String,
        description: String,
        category: String,
        iconName: String = "code",
        colorHex: String = "#6750A4",
        baselineRating: Int = 1,
        targetRating: Int = 10
    ) {
        viewModelScope.launch {
            val newModule = LearningModuleEntity(
                title = title.trim(),
                description = description.trim(),
                category = category.trim().ifBlank { "General" },
                iconName = iconName,
                colorHex = colorHex,
                baselineRating = baselineRating,
                targetRating = targetRating,
                currentRating = baselineRating
            )
            val id = moduleRepository.insertModule(newModule)
            _selectedModuleId.value = id
        }
    }

    fun updateModule(module: LearningModuleEntity) {
        viewModelScope.launch {
            moduleRepository.updateModule(module.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun archiveModule(moduleId: Long) {
        viewModelScope.launch {
            val module = moduleRepository.getModuleByIdDirect(moduleId)
            if (module != null) {
                moduleRepository.updateModule(module.copy(isArchived = true))
            }
        }
    }

    fun addFocusArea(
        moduleId: Long,
        title: String,
        description: String,
        baselineRating: Int = 1,
        targetRating: Int = 10
    ) {
        viewModelScope.launch {
            val focusArea = FocusAreaEntity(
                moduleId = moduleId,
                title = title.trim(),
                description = description.trim(),
                baselineRating = baselineRating,
                targetRating = targetRating,
                currentRating = baselineRating,
                status = "ACTIVE"
            )
            focusAreaRepository.insertFocusArea(focusArea)
        }
    }

    class Factory(
        private val moduleRepository: LearningModuleRepository,
        private val focusAreaRepository: FocusAreaRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LearningModuleViewModel(moduleRepository, focusAreaRepository) as T
        }
    }
}
