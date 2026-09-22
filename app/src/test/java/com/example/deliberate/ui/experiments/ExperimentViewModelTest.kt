package com.example.deliberate.ui.experiments

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.deliberate.data.local.DeliberateDatabase
import com.example.deliberate.data.repository.ExperimentRepository
import com.example.deliberate.data.repository.ExperimentRepositoryImpl
import com.example.deliberate.data.repository.PracticeCycleRepository
import com.example.deliberate.data.repository.PracticeCycleRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ExperimentViewModelTest {

    private lateinit var database: DeliberateDatabase
    private lateinit var experimentRepo: ExperimentRepository
    private lateinit var cycleRepo: PracticeCycleRepository
    private lateinit var viewModel: ExperimentViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            DeliberateDatabase::class.java
        ).allowMainThreadQueries().build()

        experimentRepo = ExperimentRepositoryImpl(database.experimentDao())
        cycleRepo = PracticeCycleRepositoryImpl(database.practiceCycleDao())
        viewModel = ExperimentViewModel(experimentRepo, cycleRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        database.close()
    }

    @Test
    fun addExperimentAndCycleFlow() = runTest {
        backgroundScope.launch(testDispatcher) { viewModel.experiments.collect {} }
        backgroundScope.launch(testDispatcher) { viewModel.cycles.collect {} }

        viewModel.addExperiment(
            title = "Test Sprint",
            hypothesis = "Hypothesis",
            actionItems = "Action",
            metricToTrack = "Metric"
        )

        advanceUntilIdle()

        val experiments = experimentRepo.getAllExperiments().first()
        assertEquals(1, experiments.size)

        viewModel.addCycle(
            title = "Spring Sprint",
            goal = "Practice 14 days",
            durationDays = 14
        )

        advanceUntilIdle()

        val cycles = cycleRepo.getAllCycles().first()
        assertEquals(1, cycles.size)
        assertEquals("Spring Sprint", cycles[0].title)
    }
}
