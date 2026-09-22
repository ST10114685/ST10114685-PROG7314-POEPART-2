package com.example.deliberate.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferences(
    val userName: String = "Practitioner",
    val userEmail: String = "",
    val isLoggedIn: Boolean = false,
    val themeMode: String = "SYSTEM", // SYSTEM, LIGHT, DARK
    val dailyGoalMinutes: Int = 30,
    val notificationsEnabled: Boolean = true,
    val currentStreakDays: Int = 0,
    val lastPracticeTimestamp: Long = 0L
)

interface UserPreferencesRepository {
    val userPreferencesFlow: Flow<UserPreferences>
    suspend fun updateUserName(name: String)
    suspend fun updateUserEmail(email: String)
    suspend fun setLoggedIn(loggedIn: Boolean)
    suspend fun updateThemeMode(themeMode: String)
    suspend fun updateDailyGoalMinutes(minutes: Int)
    suspend fun updateNotificationsEnabled(enabled: Boolean)
    suspend fun updateStreak(streakDays: Int, lastPracticeTimestamp: Long)
    suspend fun clearUserPreferences()
}

class UserPreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    private object PreferenceKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DAILY_GOAL_MINUTES = intPreferencesKey("daily_goal_minutes")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val CURRENT_STREAK_DAYS = intPreferencesKey("current_streak_days")
        val LAST_PRACTICE_TIMESTAMP = longPreferencesKey("last_practice_timestamp")
    }

    override val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .map { preferences ->
            UserPreferences(
                userName = preferences[PreferenceKeys.USER_NAME] ?: "Practitioner",
                userEmail = preferences[PreferenceKeys.USER_EMAIL] ?: "",
                isLoggedIn = preferences[PreferenceKeys.IS_LOGGED_IN] ?: false,
                themeMode = preferences[PreferenceKeys.THEME_MODE] ?: "SYSTEM",
                dailyGoalMinutes = preferences[PreferenceKeys.DAILY_GOAL_MINUTES] ?: 30,
                notificationsEnabled = preferences[PreferenceKeys.NOTIFICATIONS_ENABLED] ?: true,
                currentStreakDays = preferences[PreferenceKeys.CURRENT_STREAK_DAYS] ?: 0,
                lastPracticeTimestamp = preferences[PreferenceKeys.LAST_PRACTICE_TIMESTAMP] ?: 0L
            )
        }

    override suspend fun updateUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.USER_NAME] = name
        }
    }

    override suspend fun updateUserEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.USER_EMAIL] = email
        }
    }

    override suspend fun setLoggedIn(loggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.IS_LOGGED_IN] = loggedIn
        }
    }

    override suspend fun updateThemeMode(themeMode: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.THEME_MODE] = themeMode
        }
    }

    override suspend fun updateDailyGoalMinutes(minutes: Int) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.DAILY_GOAL_MINUTES] = minutes
        }
    }

    override suspend fun updateNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    override suspend fun updateStreak(streakDays: Int, lastPracticeTimestamp: Long) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.CURRENT_STREAK_DAYS] = streakDays
            preferences[PreferenceKeys.LAST_PRACTICE_TIMESTAMP] = lastPracticeTimestamp
        }
    }

    override suspend fun clearUserPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
