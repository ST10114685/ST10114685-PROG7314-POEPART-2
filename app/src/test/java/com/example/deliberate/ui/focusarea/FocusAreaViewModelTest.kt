package com.example.deliberate.ui.focusarea

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.deliberate.data.local.DeliberateDatabase
import com.example.deliberate.data.local.entity.FocusAreaEntity
import com.example.deliberate.data.local.entity.LearningModuleEntity
import com.example.deliberate.data.repository.FocusAreaRepository
import com.example.deliberate.data.repository.FocusAreaRepositoryImpl
import com.example.deliberate.data.repository.LearningModuleRepository
import com.example.deliberate.data.repository.LearningModuleRepositoryImpl
import com.example.deliberate.data.repository.PracticeSessionRepository
import com.example.deliberate.data.repository.PracticeSessionRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class FocusAreaViewModelTest {

    private lateinit var database: DeliberateDatabase
    private lateinit var moduleRepo: LearningModuleRepository
    private lateinit var focusAreaRepo: FocusAreaRepository
    private lateinit var sessionRepo: PracticeSessionRepository
    private lateinit var viewModel: FocusAreaViewModel

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
        sessionRepo = PracticeSessionRepositoryImpl(database.practiceSessionDao())
        viewModel = FocusAreaViewModel(focusAreaRepo, sessionRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        database.close()
    }

    @Test
    fun loadAndUpdateFocusArea() = runTest {
        backgroundScope.launch(testDispatcher) { viewModel.focusArea.collect {} }

        val moduleId = moduleRepo.insertModule(
            LearningModuleEntity(title = "Design", description = "UI UX", category = "Design")
        )

        val faId = focusAreaRepo.insertFocusArea(
            FocusAreaEntity(
                moduleId = moduleId,
                title = "Typography",
                description = "M3 Type Scale",
                baselineRating = 2,
                currentRating = 4,
                targetRating = 9,
                status = "ACTIVE"
            )
        )

        viewModel.loadFocusArea(faId)
        advanceUntilIdle()

        viewModel.updateCurrentRating(7)
        advanceUntilIdle()

        val updated = focusAreaRepo.getFocusAreaByIdDirect(faId)
        assertNotNull(updated)
        assertEquals(7, updated?.currentRating)

        viewModel.updateStatus("MASTERED")
        advanceUntilIdle()

        val mastered = focusAreaRepo.getFocusAreaByIdDirect(faId)
        assertEquals("MASTERED", mastered?.status)
    }
}
