# Project Plan

Deliberate: A comprehensive mobile application for intentional practice and structured reflection. Features include User Authentication, Learning Modules, Focus Areas, Practice Sessions with Timers/Objectives, Structured Reflection, Custom Reflection Prompts, Personal Lessons, Practice Experiments, Practice Cycles, Milestones & Reflection Streaks, Dashboard with Analytics/Charts, and Settings. Native Android in Kotlin with Jetpack Compose, Material Design 3, MVVM, Room + Retrofit, unit testing and GitHub Actions setup.

## Project Brief

# Deliberate - Project Brief

**Tagline:** Practice with purpose.

---

## Features

1. **Learning Modules & Focus Areas**: Create and manage skill domains (e.g., Digital Art, Kotlin Programming) and targeted focus areas, complete with baseline self-assessments and target tracking.
2. **Interactive Practice Sessions**: Initiate structured practice sessions defined by explicit objectives, success criteria, and time tracking.
3. **Structured Reflection & Lessons**: Complete post-session reflections using self-ratings (1–10) and guided prompts (*"What went well?"*, *"What could be improved?"*, *"Key lessons learned"*).
4. **Dashboard & Growth Analytics**: Track progress over time via key practice metrics, reflection streaks, ratings history, and practice logs on a unified dashboard.
5. **Practice Experiments & Cycles**: Test hypotheses through targeted sessions and conduct cycle reviews.
6. **Authentication & User Settings**: Secure account access and customizable preferences.

---

## High-Level Tech Stack

- **Language:** Kotlin
- **UI & Design System:** Jetpack Compose, Material Design 3 (vibrant theme, dark/light modes)
- **Navigation & Adaptive Strategy:** Jetpack Navigation Compose, State-driven navigation
- **Architecture:** MVVM with ViewModels, Repositories, Room database for local caching/offline capability, Kotlin Coroutines, StateFlow
- **Networking:** Retrofit with OkHttp and Gson/Moshi for REST API integration
- **Testing & CI/CD**: JUnit, Compose UI Test, GitHub Actions integration workflow

## Implementation Steps
**Total Duration:** 36m 30s

### Task_1_Data_Architecture_Repositories: Define Room database entities, DAOs, Database class, DataStore preferences, and Repositories for Learning Modules, Focus Areas, Practice Sessions, Reflections, Lessons, and Experiments.
- **Status:** COMPLETED
- **Updates:** Implemented Room entities (LearningModule, FocusArea, PracticeSession, Reflection, ReflectionPrompt, ReflectionResponse, Lesson, Experiment, PracticeCycle), DAOs, Database with TypeConverters, DataStore preferences repository, domain repositories, and 14 unit tests, all passing.
- **Acceptance Criteria:**
  - Room entities created for Modules, Focus Areas, Sessions, Reflections, Lessons, and Experiments
  - Room DAOs and Database configured with TypeConverters
  - Repositories implemented with reactive StateFlow/Flow APIs
  - Unit tests for database and repository layers pass
- **Duration:** 15m 9s

### Task_2_Modules_FocusAreas_Sessions: Implement UI screens, ViewModels, and navigation for Learning Modules, Focus Areas, and Interactive Practice Sessions with a live timer and target tracking.
- **Status:** COMPLETED
- **Updates:** Implemented ViewModels, Navigation, and Composables for Learning Modules (list and detail), Focus Area detail view with baseline/target tracking, Start Practice screen, and Practice Session screen with live timer controls and ActiveSessionManager state persistence. Verified with 18 unit tests passing and successful debug build.
- **Acceptance Criteria:**
  - Learning Modules screen allows creating and viewing skill domains and focus areas
  - Focus Area detail view displays target tracking and baseline self-assessments
  - Practice Session screen features timer controls, objectives, and success criteria
  - Active session state persists correctly during screen transitions
- **Duration:** 7m 28s

### Task_3_Reflection_Experiments_Dashboard: Implement Structured Reflection post-session flow, Practice Experiments & Cycles, Dashboard with Analytics/Streaks, and User Settings.
- **Status:** COMPLETED
- **Updates:** Implemented Reflection screen with 1-10 self-rating and guided prompts, Experiments & Cycles management and cycle review dialogs, Dashboard with streak calculation and growth analytics/metrics, Settings screen with theme/goal preferences, full NavigationBar structure, and 21 unit tests passing with successful debug build.
- **Acceptance Criteria:**
  - Post-session Reflection screen includes self-rating (1-10) and guided prompts
  - Practice Experiments & Cycles feature allows testing hypotheses and conducting cycle reviews
  - Dashboard screen displays key practice metrics, reflection streaks, rating history, and practice logs
  - Settings screen allows configuring preferences and viewing profile
- **Duration:** 9m 30s

### Task_4_AppIcon_M3Theme_RunAndVerify: Implement adaptive app icon, vibrant Material Design 3 light/dark theme, edge-to-edge layout, and perform full app execution and build verification.
- **Status:** COMPLETED
- **Updates:** Created adaptive app icon matching Deliberate branding, styled vibrant Material 3 light/dark color schemes and edge-to-edge layout, verified all 21 unit tests pass, and successfully assembled debug build.
- **Acceptance Criteria:**
  - Adaptive app icon matching Deliberate branding added
  - Vibrant Material Design 3 theme with light/dark support and edge-to-edge setup
  - make sure all existing tests pass
  - build pass
  - app does not crash
- **Duration:** 4m 23s

