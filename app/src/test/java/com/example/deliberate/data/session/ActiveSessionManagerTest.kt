package com.example.deliberate.data.session

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.deliberate.data.local.DeliberateDatabase
import com.example.deliberate.data.local.entity.LearningModuleEntity
import com.example.deliberate.data.repository.LearningModuleRepository
import com.example.deliberate.data.repository.LearningModuleRepositoryImpl
import com.example.deliberate.data.repository.PracticeSessionRepository
import com.example.deliberate.data.repository.PracticeSessionRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ActiveSessionManagerTest {

    private lateinit var database: DeliberateDatabase
    private lateinit var moduleRepo: LearningModuleRepository
    private lateinit var sessionRepo: PracticeSessionRepository
    private lateinit var manager: ActiveSessionManager

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            DeliberateDatabase::class.java
        ).allowMainThreadQueries().build()

        moduleRepo = LearningModuleRepositoryImpl(database.learningModuleDao())
        sessionRepo = PracticeSessionRepositoryImpl(database.practiceSessionDao())
        manager = ActiveSessionManager(sessionRepo, testScope)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun startPauseResumeCompleteSessionFlow() = runTest {
        val moduleId = moduleRepo.insertModule(
            LearningModuleEntity(title = "Test Domain", description = "Desc", category = "Test")
        )

        val session = manager.startSession(
            moduleId = moduleId,
            focusAreaId = null,
            title = "Test Practice",
            objective = "Test Timer",
            successCriteria = "Works",
            targetDurationMinutes = 25
        )

        assertNotNull(manager.activeSession.value)
        assertEquals("Test Practice", manager.activeSession.value?.title)
        assertTrue(manager.isTimerRunning.value)

        manager.pauseTimer()
        assertTrue(manager.isPaused.value)

        manager.resumeTimer()
        assertTrue(manager.isTimerRunning.value)

        val completedId = manager.completeSession("Completed with success")
        assertEquals(session.id, completedId)
        assertNull(manager.activeSession.value)

        val savedSession = sessionRepo.getSessionByIdDirect(completedId)
        assertNotNull(savedSession)
        assertEquals("COMPLETED", savedSession?.status)
        assertEquals("Completed with success", savedSession?.notes)
    }

    @Test
    fun cancelSessionFlow() = runTest {
        val moduleId = moduleRepo.insertModule(
            LearningModuleEntity(title = "Test Domain 2", description = "Desc 2", category = "Test")
        )

        val session = manager.startSession(
            moduleId = moduleId,
            focusAreaId = null,
            title = "Cancelled Practice",
            objective = "Obj",
            successCriteria = "Crit",
            targetDurationMinutes = 15
        )

        assertNotNull(manager.activeSession.value)

        manager.cancelSession()
        assertNull(manager.activeSession.value)

        val savedSession = sessionRepo.getSessionByIdDirect(session.id)
        assertNotNull(savedSession)
        assertEquals("CANCELLED", savedSession?.status)
    }
}
