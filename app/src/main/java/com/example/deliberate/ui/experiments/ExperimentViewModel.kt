package com.example.deliberate.ui.experiments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deliberate.data.local.entity.ExperimentEntity
import com.example.deliberate.data.local.entity.PracticeCycleEntity
import com.example.deliberate.data.repository.ExperimentRepository
import com.example.deliberate.data.repository.PracticeCycleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExperimentViewModel(
    private val experimentRepository: ExperimentRepository,
    private val cycleRepository: PracticeCycleRepository
) : ViewModel() {

    val experiments: StateFlow<List<ExperimentEntity>> = experimentRepository.getAllExperiments()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val cycles: StateFlow<List<PracticeCycleEntity>> = cycleRepository.getAllCycles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activeCycle: StateFlow<PracticeCycleEntity?> = cycleRepository.getActiveCycle()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun addExperiment(
        title: String,
        hypothesis: String,
        actionItems: String,
        metricToTrack: String,
        moduleId: Long? = null
    ) {
        viewModelScope.launch {
            val experiment = ExperimentEntity(
                moduleId = moduleId,
                title = title.trim(),
                hypothesis = hypothesis.trim(),
                actionItems = actionItems.trim(),
                metricToTrack = metricToTrack.trim(),
                status = "ACTIVE"
            )
            experimentRepository.insertExperiment(experiment)
        }
    }

    fun completeExperiment(
        id: Long,
        outcomeNotes: String,
        rating: Int
    ) {
        viewModelScope.launch {
            val exp = experimentRepository.getExperimentByIdDirect(id)
            if (exp != null) {
                val updated = exp.copy(
                    status = "COMPLETED",
                    outcomeNotes = outcomeNotes,
                    rating = rating
                )
                experimentRepository.updateExperiment(updated)
            }
        }
    }

    fun addCycle(title: String, goal: String, durationDays: Int = 14) {
        viewModelScope.launch {
            val cycle = PracticeCycleEntity(
                title = title.trim(),
                goal = goal.trim(),
                startDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis() + (durationDays * 24 * 60 * 60 * 1000L),
                status = "ACTIVE"
            )
            cycleRepository.insertCycle(cycle)
        }
    }

    fun completeCycle(id: Long, reviewNotes: String) {
        viewModelScope.launch {
            val cycle = cycleRepository.getCycleByIdDirect(id)
            if (cycle != null) {
                val updated = cycle.copy(
                    status = "COMPLETED",
                    reviewNotes = reviewNotes
                )
                cycleRepository.updateCycle(updated)
            }
        }
    }

    fun logCyclePractice(id: Long) {
        viewModelScope.launch {
            val cycle = cycleRepository.getCycleByIdDirect(id)
            if (cycle != null) {
                cycleRepository.updateCycleStreak(id, cycle.streakCount + 1)
            }
        }
    }

    class Factory(
        private val experimentRepository: ExperimentRepository,
        private val cycleRepository: PracticeCycleRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ExperimentViewModel(experimentRepository, cycleRepository) as T
        }
    }
}
