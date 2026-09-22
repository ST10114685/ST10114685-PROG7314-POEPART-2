package com.example.deliberate.ui.lessons

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.deliberate.data.local.DeliberateDatabase
import com.example.deliberate.data.repository.LessonRepository
import com.example.deliberate.data.repository.LessonRepositoryImpl
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class LessonViewModelTest {

    private lateinit var database: DeliberateDatabase
    private lateinit var lessonRepo: LessonRepository
    private lateinit var viewModel: LessonViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            DeliberateDatabase::class.java
        ).allowMainThreadQueries().build()

        lessonRepo = LessonRepositoryImpl(database.lessonDao())
        viewModel = LessonViewModel(lessonRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        database.close()
    }

    @Test
    fun addAndFavoriteLessonFlow() = runTest {
        backgroundScope.launch(testDispatcher) { viewModel.allLessons.collect {} }
        backgroundScope.launch(testDispatcher) { viewModel.favoriteLessons.collect {} }

        viewModel.addLesson(
            title = "StateFlow replay",
            lessonText = "StateFlow replays current value to new collectors.",
            category = "Technique"
        )

        advanceUntilIdle()

        val lessons = lessonRepo.getAllLessons().first()
        assertEquals(1, lessons.size)

        val lessonId = lessons[0].id
        viewModel.toggleFavorite(lessonId, true)

        advanceUntilIdle()

        val favorites = lessonRepo.getFavoriteLessons().first()
        assertEquals(1, favorites.size)
        assertTrue(favorites[0].isFavorite)
    }
}
