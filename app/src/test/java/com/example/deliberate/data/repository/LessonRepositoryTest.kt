package com.example.deliberate.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.deliberate.data.local.DeliberateDatabase
import com.example.deliberate.data.local.entity.LearningModuleEntity
import com.example.deliberate.data.local.entity.LessonEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LessonRepositoryTest {

    private lateinit var database: DeliberateDatabase
    private lateinit var moduleRepository: LearningModuleRepository
    private lateinit var lessonRepository: LessonRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            DeliberateDatabase::class.java
        ).allowMainThreadQueries().build()

        moduleRepository = LearningModuleRepositoryImpl(database.learningModuleDao())
        lessonRepository = LessonRepositoryImpl(database.lessonDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndToggleFavoriteLesson() = runTest {
        val moduleId = moduleRepository.insertModule(
            LearningModuleEntity(title = "Public Speaking", description = "Speeches", category = "Soft Skills")
        )

        val lesson = LessonEntity(
            moduleId = moduleId,
            title = "Pause for emphasis",
            lessonText = "Never rush after key arguments.",
            category = "Technique",
            isFavorite = false
        )

        val lessonId = lessonRepository.insertLesson(lesson)
        
        var favorites = lessonRepository.getFavoriteLessons().first()
        assertEquals(0, favorites.size)

        lessonRepository.updateFavoriteStatus(lessonId, true)
        favorites = lessonRepository.getFavoriteLessons().first()
        assertEquals(1, favorites.size)
        assertTrue(favorites[0].isFavorite)
    }
}
