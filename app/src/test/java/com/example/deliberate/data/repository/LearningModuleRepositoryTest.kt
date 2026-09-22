package com.example.deliberate.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.deliberate.data.local.DeliberateDatabase
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
class LearningModuleRepositoryTest {

    private lateinit var database: DeliberateDatabase
    private lateinit var repository: LearningModuleRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            DeliberateDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = LearningModuleRepositoryImpl(database.learningModuleDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetModule() = runTest {
        val module = LearningModuleEntity(
            title = "Kotlin Jetpack Compose",
            description = "Mastering Compose UI",
            category = "Programming",
            baselineRating = 3,
            targetRating = 9,
            currentRating = 5
        )

        val id = repository.insertModule(module)
        val fetched = repository.getModuleByIdDirect(id)

        assertNotNull(fetched)
        assertEquals("Kotlin Jetpack Compose", fetched?.title)
        assertEquals(5, fetched?.currentRating)
    }

    @Test
    fun getAllActiveModules() = runTest {
        val m1 = LearningModuleEntity(title = "Module 1", description = "Desc 1", category = "Cat 1", isArchived = false)
        val m2 = LearningModuleEntity(title = "Module 2", description = "Desc 2", category = "Cat 2", isArchived = true)

        repository.insertModule(m1)
        repository.insertModule(m2)

        val activeList = repository.getActiveModules().first()
        assertEquals(1, activeList.size)
        assertEquals("Module 1", activeList[0].title)
    }

    @Test
    fun updateAndDeleteModule() = runTest {
        val module = LearningModuleEntity(title = "Old Title", description = "Desc", category = "Cat")
        val id = repository.insertModule(module)

        repository.updateModuleRating(id, 8)
        val updated = repository.getModuleByIdDirect(id)
        assertEquals(8, updated?.currentRating)

        repository.deleteModuleById(id)
        val deleted = repository.getModuleByIdDirect(id)
        assertNull(deleted)
    }
}
