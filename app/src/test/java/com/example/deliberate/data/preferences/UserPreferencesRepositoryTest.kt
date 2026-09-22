package com.example.deliberate.data.preferences

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class UserPreferencesRepositoryTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())

    private lateinit var repository: UserPreferencesRepository

    @Before
    fun setup() {
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("test_user_preferences.preferences_pb") }
        )
        repository = UserPreferencesRepositoryImpl(testDataStore)
    }

    @Test
    fun defaultPreferences() = runTest {
        val prefs = repository.userPreferencesFlow.first()
        assertEquals("Practitioner", prefs.userName)
        assertEquals("SYSTEM", prefs.themeMode)
        assertEquals(30, prefs.dailyGoalMinutes)
        assertFalse(prefs.isLoggedIn)
    }

    @Test
    fun updatePreferences() = runTest {
        repository.updateUserName("Alex Developer")
        repository.updateThemeMode("DARK")
        repository.updateDailyGoalMinutes(45)
        repository.setLoggedIn(true)
        repository.updateStreak(5, 123456789L)

        val updated = repository.userPreferencesFlow.first()
        assertEquals("Alex Developer", updated.userName)
        assertEquals("DARK", updated.themeMode)
        assertEquals(45, updated.dailyGoalMinutes)
        assertTrue(updated.isLoggedIn)
        assertEquals(5, updated.currentStreakDays)
        assertEquals(123456789L, updated.lastPracticeTimestamp)
    }
}
