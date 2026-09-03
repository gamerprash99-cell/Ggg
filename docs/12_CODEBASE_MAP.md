# LifeOS — Complete Codebase Map & Directory Structure

This document provides a visual and functional map of all source directories, configuration files, resources, and modules across the LifeOS repository.

---

## 1. Visual Directory Tree

```
LifeOS/
├── .github/
│   └── workflows/
│       └── android.yml                # Automated CI/CD pipeline (Build, Unit Tests, Roborazzi)
├── app/
│   ├── build.gradle.kts               # Module build configuration, plugins, dependencies, SDK targets
│   ├── proguard-rules.pro             # R8 / ProGuard obfuscation rules
│   └── src/
│       ├── androidTest/               # Android Instrumented Tests
│       │   └── java/com/example/
│       │       └── ExampleInstrumentedTest.kt
│       ├── main/
│       │   ├── AndroidManifest.xml    # App manifest, permissions, Application & Activity declarations
│       │   ├── java/com/example/
│       │   │   ├── LifeOSApp.kt       # Application class, Singleton AppDatabase & Repo init, data seed
│       │   │   ├── MainActivity.kt    # Single activity entry point, NavHost container, Edge-to-Edge
│       │   │   ├── ai/
│       │   │   │   └── LifeOSAI.kt    # Dual-mode AI engine (Offline heuristic NLP + Gemini REST fallback)
│       │   │   ├── data/
│       │   │   │   ├── local/
│       │   │   │   │   ├── AppDatabase.kt  # Room database definition ("lifeos_database", v1)
│       │   │   │   │   ├── Converters.kt   # Room TypeConverters for Enums
│       │   │   │   │   ├── dao/
│       │   │   │   │   │   └── Daos.kt    # 8 Room Data Access Object interfaces
│       │   │   │   │   └── entity/
│       │   │   │   │       └── Entities.kt# 8 SQLite Room database entity definitions
│       │   │   │   └── repository/
│       │   │   │       └── LifeOSRepository.kt # Central business logic hub, CRUD & JSON backup
│       │   │   ├── domain/
│       │   │   │   └── model/
│       │   │   │       └── Models.kt      # Domain enums, UI helper models & AIPermissions
│       │   │   └── ui/
│       │   │       ├── components/
│       │   │       │   └── Components.kt  # Reusable UI widgets (GlassCard, Banner, LockDialog)
│       │   │       ├── navigation/
│       │   │       │   └── Navigation.kt  # Sealed screen routes & LifeOSBottomBar
│       │   │       ├── screens/
│       │   │       │   ├── capture/
│       │   │       │   │   └── CaptureSheet.kt    # Sub-3s Quick Capture modal bottom sheet
│       │   │       │   ├── home/
│       │   │       │   │   ├── HomeScreen.kt      # Daily dashboard, progress & quick search
│       │   │       │   │   └── HomeViewModel.kt   # Dashboard StateFlow combine logic
│       │   │       │   ├── insights/
│       │   │       │   │   ├── InsightsScreen.kt  # Productivity, expense & mood charts
│       │   │       │   │   └── InsightsViewModel.kt # Analytical computation engine
│       │   │       │   ├── life/
│       │   │       │   │   ├── LifeHubScreen.kt   # 7-in-1 Central Life Hub (Timeline, Expenses, etc.)
│       │   │       │   │   └── LifeViewModel.kt   # Hub StateFlow & chat message manager
│       │   │       │   └── notes/
│       │   │       │       ├── NoteEditorScreen.kt # Fullscreen note create & edit interface
│       │   │       │       ├── NotesScreen.kt      # Folder-based notes list & AI tools
│       │   │       │       └── NotesViewModel.kt   # Note filters, AI task extraction VM
│       │   │       └── theme/
│       │   │           ├── Color.kt       # Vibrant Palette design tokens & backward-compatible aliases
│       │   │           ├── Theme.kt       # Material 3 ColorScheme & LifeOSTheme wrapper
│       │   │           └── Type.kt        # Material 3 typography definitions
│       │   └── res/
│       │       ├── drawable/          # Vector drawables & launcher icons
│       │       ├── mipmap-*/          # Adaptive app launcher bitmaps
│       │       ├── values/
│       │       │   ├── colors.xml     # Base XML color resources
│       │       │   ├── strings.xml    # App name ("LifeOS") & localized strings
│       │       │   └── themes.xml     # XML theme styles
│       │       └── xml/
│       │           ├── backup_rules.xml
│       │           └── data_extraction_rules.xml
│       └── test/                      # Local JVM Unit & Screenshot Tests
│           ├── java/com/example/
│           │   ├── ExampleUnitTest.kt
│           │   ├── ExampleRobolectricTest.kt
│           │   └── GreetingScreenshotTest.kt
│           └── screenshots/           # Roborazzi golden image references
├── docs/                              # 25-File Master Project Knowledge & Handover Suite
├── gradle/
│   ├── libs.versions.toml             # Central Gradle Version Catalog (dependencies & plugins)
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties  # Gradle distribution (v8.11.1)
├── build.gradle.kts                   # Root project Gradle configuration
├── settings.gradle.kts                # Project name ("LifeOS") & repository settings
└── metadata.json                      # AI Studio platform identification & capability metadata
```

---

## 2. Source Directory Responsibilities

| Package Path | Architectural Layer | Key Responsibility |
| :--- | :--- | :--- |
| `com.example.ai` | Intelligence Layer | Local heuristic NLP parsing, task extraction from notes, and optional Gemini REST fallback. |
| `com.example.data.local` | Data Persistence | SQLite database creation via Room, TypeConverters, 8 entity tables, and 8 DAO interfaces. |
| `com.example.data.repository`| Domain Business Logic | Central repository coordinating cross-module side effects (e.g., auto-generating timeline events on task completion). |
| `com.example.domain.model` | Domain Models | Pure Kotlin enums and data structures defining life priorities, moods, expense categories, and permissions. |
| `com.example.ui.components`| UI Components | Standardized, reusable Material 3 composables styled according to the Vibrant Palette design system. |
| `com.example.ui.navigation`| UI Routing | Sealed route definitions and the custom bottom navigation bar with the elevated Quick Capture FAB. |
| `com.example.ui.screens.*` | Presentation Layer | Feature-specific screens and their associated ViewModels managing UI states via Kotlin `StateFlow`. |
| `com.example.ui.theme` | Styling & Theming | Centralized color palette, dark mode definitions, and typography scales. |
