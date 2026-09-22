package com.example.deliberate.ui.reflection

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.deliberate.data.local.DeliberateDatabase
import com.example.deliberate.data.local.entity.LearningModuleEntity
import com.example.deliberate.data.preferences.UserPreferencesRepository
import com.example.deliberate.data.preferences.UserPreferencesRepositoryImpl
import com.example.deliberate.data.repository.LearningModuleRepository
import com.example.deliberate.data.repository.LearningModuleRepositoryImpl
import com.example.deliberate.data.repository.LessonRepository
import com.example.deliberate.data.repository.LessonRepositoryImpl
import com.example.deliberate.data.repository.PracticeSessionRepository
import com.example.deliberate.data.repository.PracticeSessionRepositoryImpl
import com.example.deliberate.data.repository.ReflectionRepository
import com.example.deliberate.data.repository.ReflectionRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ReflectionViewModelTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private lateinit var database: DeliberateDatabase
    private lateinit var reflectionRepo: ReflectionRepository
    private lateinit var sessionRepo: PracticeSessionRepository
    private lateinit var moduleRepo: LearningModuleRepository
    private lateinit var lessonRepo: LessonRepository
    private lateinit var userPrefsRepo: UserPreferencesRepository
    private lateinit var viewModel: ReflectionViewModel

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            DeliberateDatabase::class.java
        ).allowMainThreadQueries().build()

        reflectionRepo = ReflectionRepositoryImpl(database.reflectionDao())
        sessionRepo = PracticeSessionRepositoryImpl(database.practiceSessionDao())
        moduleRepo = LearningModuleRepositoryImpl(database.learningModuleDao())
        lessonRepo = LessonRepositoryImpl(database.lessonDao())

        val testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("test_reflection_user_preferences.preferences_pb") }
        )
        userPrefsRepo = UserPreferencesRepositoryImpl(testDataStore)

        viewModel = ReflectionViewModel(
            reflectionRepo,
            sessionRepo,
            moduleRepo,
            lessonRepo,
            userPrefsRepo
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        database.close()
    }

    @Test
    fun submitReflectionFlow() = runTest {
        backgroundScope.launch(testDispatcher) { userPrefsRepo.userPreferencesFlow.collect {} }

        val moduleId = moduleRepo.insertModule(
            LearningModuleEntity(title = "Kotlin Flow", description = "Desc", category = "Tech", currentRating = 3)
        )

        var isCompleted = false
        val latch = java.util.concurrent.CountDownLatch(1)
        viewModel.submitReflection(
            sessionId = null,
            moduleId = moduleId,
            focusAreaId = null,
            rating = 8,
            whatWentWell = "Understood SharedFlow vs StateFlow",
            whatToImprove = "Practice replay cache",
            keyTakeaway = "StateFlow is a hot stream with initial value",
            saveAsLesson = true,
            lessonCategory = "Technique",
            onCompleted = {
                isCompleted = true
                latch.countDown()
            }
        )

        latch.await(2, java.util.concurrent.TimeUnit.SECONDS)
        assertEquals(true, isCompleted)

        val reflections = reflectionRepo.getAllReflections().first()
        assertEquals(1, reflections.size)
        assertEquals(8, reflections[0].rating)

        val lessons = lessonRepo.getAllLessons().first()
        assertEquals(1, lessons.size)
        assertEquals("StateFlow is a hot stream with initial value", lessons[0].lessonText)

        val prefs = userPrefsRepo.userPreferencesFlow.first()
        assertEquals(1, prefs.currentStreakDays)
    }
}
