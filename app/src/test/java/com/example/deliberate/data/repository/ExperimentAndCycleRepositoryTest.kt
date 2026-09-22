package com.example.deliberate.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.deliberate.data.local.DeliberateDatabase
import com.example.deliberate.data.local.entity.ExperimentEntity
import com.example.deliberate.data.local.entity.PracticeCycleEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExperimentAndCycleRepositoryTest {

    private lateinit var database: DeliberateDatabase
    private lateinit var experimentRepository: ExperimentRepository
    private lateinit var cycleRepository: PracticeCycleRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            DeliberateDatabase::class.java
        ).allowMainThreadQueries().build()

        experimentRepository = ExperimentRepositoryImpl(database.experimentDao())
        cycleRepository = PracticeCycleRepositoryImpl(database.practiceCycleDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun experimentLifecycle() = runTest {
        val experiment = ExperimentEntity(
            title = "Pomodoro Technique 25m",
            hypothesis = "Focus improves with shorter sprints",
            actionItems = "Use 25 min timer",
            metricToTrack = "Focus Rating",
            status = "ACTIVE"
        )

        val id = experimentRepository.insertExperiment(experiment)
        val activeExp = experimentRepository.getExperimentsByStatus("ACTIVE").first()

        assertEquals(1, activeExp.size)
        assertEquals("Pomodoro Technique 25m", activeExp[0].title)

        val fetched = experimentRepository.getExperimentByIdDirect(id)
        val updated = fetched?.copy(status = "COMPLETED", outcomeNotes = "Significant increase in focus")
        if (updated != null) {
            experimentRepository.updateExperiment(updated)
        }

        val completedExp = experimentRepository.getExperimentsByStatus("COMPLETED").first()
        assertEquals(1, completedExp.size)
        assertEquals("Significant increase in focus", completedExp[0].outcomeNotes)
    }

    @Test
    fun practiceCycleAndStreak() = runTest {
        val cycle = PracticeCycleEntity(
            title = "Spring Sprint 2025",
            goal = "Practice 14 days straight",
            status = "ACTIVE",
            streakCount = 3
        )

        val cycleId = cycleRepository.insertCycle(cycle)
        val activeCycle = cycleRepository.getActiveCycle().first()

        assertNotNull(activeCycle)
        assertEquals(3, activeCycle?.streakCount)

        cycleRepository.updateCycleStreak(cycleId, 7)
        val updatedCycle = cycleRepository.getCycleByIdDirect(cycleId)
        assertEquals(7, updatedCycle?.streakCount)
    }
}
