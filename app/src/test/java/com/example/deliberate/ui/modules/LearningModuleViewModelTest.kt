package com.example.deliberate.ui.modules

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.deliberate.data.local.DeliberateDatabase
import com.example.deliberate.data.repository.FocusAreaRepository
import com.example.deliberate.data.repository.FocusAreaRepositoryImpl
import com.example.deliberate.data.repository.LearningModuleRepository
import com.example.deliberate.data.repository.LearningModuleRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class LearningModuleViewModelTest {

    private lateinit var database: DeliberateDatabase
    private lateinit var moduleRepo: LearningModuleRepository
    private lateinit var focusAreaRepo: FocusAreaRepository
    private lateinit var viewModel: LearningModuleViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            DeliberateDatabase::class.java
        ).allowMainThreadQueries().build()

        moduleRepo = LearningModuleRepositoryImpl(database.learningModuleDao())
        focusAreaRepo = FocusAreaRepositoryImpl(database.focusAreaDao())
        viewModel = LearningModuleViewModel(moduleRepo, focusAreaRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        database.close()
    }

    @Test
    fun addAndSelectModuleFlow() = runTest {
        backgroundScope.launch(testDispatcher) { viewModel.activeModules.collect {} }
        backgroundScope.launch(testDispatcher) { viewModel.selectedModuleFocusAreas.collect {} }

        viewModel.addModule(
            title = "Kotlin Jetpack Compose",
            description = "UI Framework",
            category = "Programming",
            baselineRating = 2,
            targetRating = 9
        )

        advanceUntilIdle()

        val activeModules = moduleRepo.getActiveModules().first()
        assertEquals(1, activeModules.size)
        assertEquals("Kotlin Jetpack Compose", activeModules[0].title)

        val selectedId = viewModel.selectedModuleId.value
        assertEquals(activeModules[0].id, selectedId)

        viewModel.addFocusArea(
            moduleId = selectedId!!,
            title = "Layouts & Modifiers",
            description = "Box, Column, Row",
            baselineRating = 3,
            targetRating = 8
        )

        advanceUntilIdle()

        val focusAreas = focusAreaRepo.getFocusAreasForModule(selectedId).first()
        assertEquals(1, focusAreas.size)
        assertEquals("Layouts & Modifiers", focusAreas[0].title)
    }
}
