package com.example.deliberate.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.deliberate.data.local.DeliberateDatabase
import com.example.deliberate.data.local.entity.LearningModuleEntity
import com.example.deliberate.data.local.entity.PracticeSessionEntity
import com.example.deliberate.data.local.entity.ReflectionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReflectionRepositoryTest {

    private lateinit var database: DeliberateDatabase
    private lateinit var moduleRepository: LearningModuleRepository
    private lateinit var sessionRepository: PracticeSessionRepository
    private lateinit var reflectionRepository: ReflectionRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            DeliberateDatabase::class.java
        ).allowMainThreadQueries().build()

        moduleRepository = LearningModuleRepositoryImpl(database.learningModuleDao())
        sessionRepository = PracticeSessionRepositoryImpl(database.practiceSessionDao())
        reflectionRepository = ReflectionRepositoryImpl(database.reflectionDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveReflection() = runTest {
        val moduleId = moduleRepository.insertModule(
            LearningModuleEntity(title = "Design", description = "UI UX", category = "Design")
        )
        val sessionId = sessionRepository.insertSession(
            PracticeSessionEntity(moduleId = moduleId, title = "Design System", objective = "M3 Tokens", successCriteria = "Complete")
        )

        val reflection = ReflectionEntity(
            sessionId = sessionId,
            moduleId = moduleId,
            rating = 8,
            whatWentWell = "Understood color schemes",
            whatToImprove = "Typography hierarchy",
            keyTakeaway = "Dynamic color is key"
        )

        val reflectionId = reflectionRepository.insertReflection(reflection)
        val fetched = reflectionRepository.getReflectionByIdDirect(reflectionId)

        assertNotNull(fetched)
        assertEquals(8, fetched?.rating)
        assertEquals("Understood color schemes", fetched?.whatWentWell)

        val avgRating = reflectionRepository.getAverageRating().first()
        assertEquals(8.0f, avgRating ?: 0f, 0.01f)
    }
}
