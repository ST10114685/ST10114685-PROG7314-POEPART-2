package com.example.deliberate.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.deliberate.data.local.DeliberateDatabase
import com.example.deliberate.data.local.entity.LearningModuleEntity
import com.example.deliberate.data.local.entity.PracticeSessionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PracticeSessionRepositoryTest {

    private lateinit var database: DeliberateDatabase
    private lateinit var moduleRepository: LearningModuleRepository
    private lateinit var sessionRepository: PracticeSessionRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            DeliberateDatabase::class.java
        ).allowMainThreadQueries().build()

        moduleRepository = LearningModuleRepositoryImpl(database.learningModuleDao())
        sessionRepository = PracticeSessionRepositoryImpl(database.practiceSessionDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertSessionAndCalculateTotals() = runTest {
        val moduleId = moduleRepository.insertModule(
            LearningModuleEntity(title = "Coding", description = "Kotlin", category = "Tech")
        )

        val s1 = PracticeSessionEntity(
            moduleId = moduleId,
            title = "Coroutines Drills",
            objective = "Understand Flow",
            successCriteria = "Pass tests",
            targetDurationMinutes = 30,
            actualDurationSeconds = 1800,
            status = "COMPLETED"
        )
        val s2 = PracticeSessionEntity(
            moduleId = moduleId,
            title = "Room Database",
            objective = "Entities & DAOs",
            successCriteria = "All pass",
            targetDurationMinutes = 45,
            actualDurationSeconds = 2700,
            status = "COMPLETED"
        )

        sessionRepository.insertSession(s1)
        sessionRepository.insertSession(s2)

        val completedCount = sessionRepository.getCompletedSessionCount().first()
        assertEquals(2, completedCount)

        val totalTime = sessionRepository.getTotalPracticeTimeSeconds().first()
        assertEquals(4500L, totalTime)
    }

    @Test
    fun getSessionsForModule() = runTest {
        val m1Id = moduleRepository.insertModule(LearningModuleEntity(title = "M1", description = "", category = ""))
        val m2Id = moduleRepository.insertModule(LearningModuleEntity(title = "M2", description = "", category = ""))

        sessionRepository.insertSession(PracticeSessionEntity(moduleId = m1Id, title = "S1", objective = "", successCriteria = ""))
        sessionRepository.insertSession(PracticeSessionEntity(moduleId = m1Id, title = "S2", objective = "", successCriteria = ""))
        sessionRepository.insertSession(PracticeSessionEntity(moduleId = m2Id, title = "S3", objective = "", successCriteria = ""))

        val m1Sessions = sessionRepository.getSessionsForModule(m1Id).first()
        assertEquals(2, m1Sessions.size)
    }
}
