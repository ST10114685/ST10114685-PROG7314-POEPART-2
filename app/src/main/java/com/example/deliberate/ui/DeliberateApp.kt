package com.example.deliberate.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deliberate.data.local.DeliberateDatabase
import com.example.deliberate.data.preferences.UserPreferencesRepositoryImpl
import com.example.deliberate.data.preferences.dataStore
import com.example.deliberate.data.repository.ExperimentRepositoryImpl
import com.example.deliberate.data.repository.FocusAreaRepositoryImpl
import com.example.deliberate.data.repository.LearningModuleRepositoryImpl
import com.example.deliberate.data.repository.LessonRepositoryImpl
import com.example.deliberate.data.repository.PracticeCycleRepositoryImpl
import com.example.deliberate.data.repository.PracticeSessionRepositoryImpl
import com.example.deliberate.data.repository.ReflectionRepositoryImpl
import com.example.deliberate.data.session.ActiveSessionManager
import com.example.deliberate.ui.components.ActiveSessionBanner
import com.example.deliberate.ui.dashboard.DashboardScreen
import com.example.deliberate.ui.dashboard.DashboardViewModel
import com.example.deliberate.ui.experiments.ExperimentViewModel
import com.example.deliberate.ui.experiments.ExperimentsScreen
import com.example.deliberate.ui.focusarea.FocusAreaDetailScreen
import com.example.deliberate.ui.focusarea.FocusAreaViewModel
import com.example.deliberate.ui.lessons.LessonViewModel
import com.example.deliberate.ui.lessons.LessonsScreen
import com.example.deliberate.ui.modules.LearningModuleViewModel
import com.example.deliberate.ui.modules.ModuleDetailScreen
import com.example.deliberate.ui.modules.ModulesListScreen
import com.example.deliberate.ui.reflection.ReflectionScreen
import com.example.deliberate.ui.reflection.ReflectionViewModel
import com.example.deliberate.ui.session.PracticeSessionScreen
import com.example.deliberate.ui.session.PracticeSessionViewModel
import com.example.deliberate.ui.session.StartSessionScreen
import com.example.deliberate.ui.settings.SettingsScreen
import com.example.deliberate.ui.settings.SettingsViewModel
import com.example.deliberate.ui.auth.AuthScreen
import com.example.deliberate.ui.auth.AuthViewModel
import com.example.deliberate.ui.theme.DeliberateTheme

sealed class Screen {
    object Dashboard : Screen()
    object ModulesList : Screen()
    data class ModuleDetail(val moduleId: Long) : Screen()
    data class FocusAreaDetail(val focusAreaId: Long) : Screen()
    data class StartSession(val moduleId: Long? = null, val focusAreaId: Long? = null) : Screen()
    object ActiveSession : Screen()
    data class Reflection(val sessionId: Long) : Screen()
    object Experiments : Screen()
    object Lessons : Screen()
    object Settings : Screen()
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val screen: Screen
)

@Composable
fun DeliberateApp() {
    val context = LocalContext.current
    val database = remember { DeliberateDatabase.getInstance(context) }

    val moduleRepo = remember { LearningModuleRepositoryImpl(database.learningModuleDao()) }
    val focusAreaRepo = remember { FocusAreaRepositoryImpl(database.focusAreaDao()) }
    val sessionRepo = remember { PracticeSessionRepositoryImpl(database.practiceSessionDao()) }
    val reflectionRepo = remember { ReflectionRepositoryImpl(database.reflectionDao()) }
    val lessonRepo = remember { LessonRepositoryImpl(database.lessonDao()) }
    val experimentRepo = remember { ExperimentRepositoryImpl(database.experimentDao()) }
    val cycleRepo = remember { PracticeCycleRepositoryImpl(database.practiceCycleDao()) }
    val userPreferencesRepo = remember { UserPreferencesRepositoryImpl(context.dataStore) }

    val activeSessionManager = remember { ActiveSessionManager.getInstance(sessionRepo) }

    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.Factory(sessionRepo, reflectionRepo, moduleRepo, experimentRepo, userPreferencesRepo)
    )
    val moduleViewModel: LearningModuleViewModel = viewModel(
        factory = LearningModuleViewModel.Factory(moduleRepo, focusAreaRepo)
    )
    val focusAreaViewModel: FocusAreaViewModel = viewModel(
        factory = FocusAreaViewModel.Factory(focusAreaRepo, sessionRepo)
    )
    val sessionViewModel: PracticeSessionViewModel = viewModel(
        factory = PracticeSessionViewModel.Factory(activeSessionManager)
    )
    val reflectionViewModel: ReflectionViewModel = viewModel(
        factory = ReflectionViewModel.Factory(reflectionRepo, sessionRepo, moduleRepo, lessonRepo, userPreferencesRepo)
    )
    val experimentViewModel: ExperimentViewModel = viewModel(
        factory = ExperimentViewModel.Factory(experimentRepo, cycleRepo)
    )
    val lessonViewModel: LessonViewModel = viewModel(
        factory = LessonViewModel.Factory(lessonRepo)
    )
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(userPreferencesRepo)
    )
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Factory(userPreferencesRepo)
    )

    val backStack = remember { mutableStateListOf<Screen>(Screen.Dashboard) }
    val currentScreen = backStack.lastOrNull() ?: Screen.Dashboard

    val activeSession by sessionViewModel.activeSession.collectAsState()
    val elapsedSeconds by sessionViewModel.elapsedSeconds.collectAsState()
    val isPaused by sessionViewModel.isPaused.collectAsState()

    val prefs by settingsViewModel.userPreferences.collectAsState()
    val darkTheme = when (prefs.themeMode) {
        "LIGHT" -> false
        "DARK" -> true
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    val navItems = listOf(
        BottomNavItem("Dashboard", Icons.Rounded.Dashboard, Screen.Dashboard),
        BottomNavItem("Modules", Icons.Rounded.School, Screen.ModulesList),
        BottomNavItem("Labs", Icons.Rounded.Science, Screen.Experiments),
        BottomNavItem("Journal", Icons.Rounded.Book, Screen.Lessons),
        BottomNavItem("Settings", Icons.Rounded.Settings, Screen.Settings)
    )

    fun navigateTo(screen: Screen) {
        if (screen is Screen.Dashboard || screen is Screen.ModulesList || screen is Screen.Experiments || screen is Screen.Lessons || screen is Screen.Settings) {
            backStack.clear()
            backStack.add(screen)
        } else {
            backStack.add(screen)
        }
    }

    fun popBackStack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.size - 1)
        }
    }

    val showBottomBar = prefs.isLoggedIn && (currentScreen is Screen.Dashboard ||
            currentScreen is Screen.ModulesList ||
            currentScreen is Screen.Experiments ||
            currentScreen is Screen.Lessons ||
            currentScreen is Screen.Settings)

    DeliberateTheme(darkTheme = darkTheme) {
        Scaffold(
            bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    navItems.forEach { item ->
                        val selected = currentScreen::class == item.screen::class
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navigateTo(item.screen) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Show Active Session Banner across screens (except when already on ActiveSession or Reflection screen)
            if (prefs.isLoggedIn && currentScreen !is Screen.ActiveSession && currentScreen !is Screen.Reflection) {
                ActiveSessionBanner(
                    activeSession = activeSession,
                    elapsedSeconds = elapsedSeconds,
                    isPaused = isPaused,
                    onClick = { navigateTo(Screen.ActiveSession) }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                if (!prefs.isLoggedIn) {
                    AuthScreen(viewModel = authViewModel)
                } else {
                    when (val screen = currentScreen) {
                        is Screen.Dashboard -> {
                            DashboardScreen(
                                viewModel = dashboardViewModel,
                                onStartPracticeClick = {
                                    navigateTo(Screen.StartSession())
                                },
                                onModuleClick = { moduleId ->
                                    navigateTo(Screen.ModuleDetail(moduleId))
                                }
                            )
                        }

                        is Screen.ModulesList -> {
                            ModulesListScreen(
                                viewModel = moduleViewModel,
                                onModuleClick = { moduleId ->
                                    navigateTo(Screen.ModuleDetail(moduleId))
                                },
                                onStartSessionClick = { moduleId ->
                                    navigateTo(Screen.StartSession(moduleId = moduleId))
                                }
                            )
                        }

                        is Screen.ModuleDetail -> {
                            ModuleDetailScreen(
                                moduleId = screen.moduleId,
                                viewModel = moduleViewModel,
                                onBackClick = { popBackStack() },
                                onFocusAreaClick = { focusAreaId ->
                                    navigateTo(Screen.FocusAreaDetail(focusAreaId))
                                },
                                onStartSessionClick = { moduleId, focusAreaId ->
                                    navigateTo(Screen.StartSession(moduleId = moduleId, focusAreaId = focusAreaId))
                                }
                            )
                        }

                        is Screen.FocusAreaDetail -> {
                            FocusAreaDetailScreen(
                                focusAreaId = screen.focusAreaId,
                                viewModel = focusAreaViewModel,
                                onBackClick = { popBackStack() },
                                onStartSessionClick = { moduleId, focusAreaId ->
                                    navigateTo(Screen.StartSession(moduleId = moduleId, focusAreaId = focusAreaId))
                                }
                            )
                        }

                        is Screen.StartSession -> {
                            StartSessionScreen(
                                initialModuleId = screen.moduleId,
                                initialFocusAreaId = screen.focusAreaId,
                                moduleViewModel = moduleViewModel,
                                sessionViewModel = sessionViewModel,
                                onBackClick = { popBackStack() },
                                onSessionStarted = {
                                    navigateTo(Screen.ActiveSession)
                                }
                            )
                        }

                        is Screen.ActiveSession -> {
                            PracticeSessionScreen(
                                viewModel = sessionViewModel,
                                onBackClick = { popBackStack() },
                                onSessionCompleted = { sessionId ->
                                    backStack.removeAt(backStack.size - 1)
                                    navigateTo(Screen.Reflection(sessionId))
                                },
                                onSessionCancelled = {
                                    popBackStack()
                                }
                            )
                        }

                        is Screen.Reflection -> {
                            ReflectionScreen(
                                sessionId = screen.sessionId,
                                viewModel = reflectionViewModel,
                                onBackClick = { popBackStack() },
                                onReflectionSubmitted = {
                                    backStack.clear()
                                    backStack.add(Screen.Dashboard)
                                }
                            )
                        }

                        is Screen.Experiments -> {
                            ExperimentsScreen(viewModel = experimentViewModel)
                        }

                        is Screen.Lessons -> {
                            LessonsScreen(viewModel = lessonViewModel)
                        }

                        is Screen.Settings -> {
                            SettingsScreen(viewModel = settingsViewModel)
                        }
                    }
                }
            }
        }
    }
}
}
