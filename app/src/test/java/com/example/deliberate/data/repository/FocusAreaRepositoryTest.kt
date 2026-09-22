package com.example.deliberate.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.deliberate.data.local.DeliberateDatabase
import com.example.deliberate.data.local.entity.FocusAreaEntity
import com.example.deliberate.data.local.entity.LearningModuleEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FocusAreaRepositoryTest {

    private lateinit var database: DeliberateDatabase
    private lateinit var moduleRepository: LearningModuleRepository
    private lateinit var focusAreaRepository: FocusAreaRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            DeliberateDatabase::class.java
        ).allowMainThreadQueries().build()

        moduleRepository = LearningModuleRepositoryImpl(database.learningModuleDao())
        focusAreaRepository = FocusAreaRepositoryImpl(database.focusAreaDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetFocusAreasForModule() = runTest {
        val moduleId = moduleRepository.insertModule(
            LearningModuleEntity(title = "Art", description = "Digital Painting", category = "Art")
        )

        val fa1 = FocusAreaEntity(moduleId = moduleId, title = "Color Theory", description = "Harmonies")
        val fa2 = FocusAreaEntity(moduleId = moduleId, title = "Anatomy", description = "Proportions")

        focusAreaRepository.insertFocusArea(fa1)
        focusAreaRepository.insertFocusArea(fa2)

        val focusAreas = focusAreaRepository.getFocusAreasForModule(moduleId).first()
        assertEquals(2, focusAreas.size)
    }

    @Test
    fun updateRatingAndCascadeDelete() = runTest {
        val moduleId = moduleRepository.insertModule(
            LearningModuleEntity(title = "Music", description = "Guitar", category = "Music")
        )

        val faId = focusAreaRepository.insertFocusArea(
            FocusAreaEntity(moduleId = moduleId, title = "Chords", description = "Barre Chords", currentRating = 2)
        )

        focusAreaRepository.updateFocusAreaRating(faId, 7)
        val fetched = focusAreaRepository.getFocusAreaByIdDirect(faId)
        assertNotNull(fetched)
        assertEquals(7, fetched?.currentRating)

        // Cascade delete on module deletion
        moduleRepository.deleteModuleById(moduleId)
        val deletedFA = focusAreaRepository.getFocusAreaByIdDirect(faId)
        assertNull(deletedFA)
    }
}
