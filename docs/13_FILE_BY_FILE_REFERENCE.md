# LifeOS — File-by-File Technical Reference

This document provides a comprehensive technical audit of every primary source and configuration file in LifeOS.

---

## 1. Application & Activity Roots

### `app/src/main/java/com/example/LifeOSApp.kt`
- **Lines of Code:** ~60
- **Role:** Application subclass and global singleton container.
- **Key Responsibilities:**
  - Initializes `AppDatabase.getDatabase(this)`.
  - Instantiates `LifeOSRepository(database)`.
  - Launches a coroutine in `CoroutineScope(Dispatchers.IO)` on launch to invoke `repository.checkAndSeedInitialData()`.
  - Exposes `LifeOSApp.repo` static accessor for simple ViewModel dependency injection.
- **Imports / Dependencies:** Android Application, Kotlin Coroutines (`CoroutineScope`, `Dispatchers.IO`, `launch`), Room database.

### `app/src/main/java/com/example/MainActivity.kt`
- **Lines of Code:** ~150
- **Role:** Single Activity entry point and root Compose host.
- **Key Responsibilities:**
  - Configures `enableEdgeToEdge()` for immersive window insets.
  - Hosts `LifeOSMainApp()` Composable inside `LifeOSTheme`.
  - Houses the top-level `Scaffold` containing the dynamic top context bar, bottom `LifeOSBottomBar`, and `NavHost`.
  - Manages modal visibility of `CaptureSheet` bottom sheet.
- **Routes Hosted:** `home`, `notes`, `insights`, `life`, and `note_editor/{noteId}`.

---

## 2. Data & Persistence Layer

### `app/src/main/java/com/example/data/local/AppDatabase.kt`
- **Role:** Room Database definition.
- **Entities Registered (8):** `NoteEntity`, `TaskEntity`, `HabitEntity`, `HabitLogEntity`, `ExpenseEntity`, `DiaryEntity`, `CaptureEntity`, `LifeEventEntity`.
- **Database Name:** `"lifeos_database"`
- **Version:** `1` (`exportSchema = false`, `fallbackToDestructiveMigration(false)`).
- **Type Converters:** Registers `Converters::class`.
- **Singleton Pattern:** Thread-safe Double-Checked Locking via `@Volatile private var INSTANCE` and `synchronized(this)`.

### `app/src/main/java/com/example/data/local/Converters.kt`
- **Role:** Room TypeConverters.
- **Converters Implemented:**
  - `Priority` ↔ `String`
  - `Mood` ↔ `String`
  - `ExpenseCategory` ↔ `String`
  - `CaptureType` ↔ `String`
  - `LifeEventType` ↔ `String`

### `app/src/main/java/com/example/data/local/entity/Entities.kt`
- **Role:** SQLite table entity declarations.
- **Classes Defined:**
  1. `NoteEntity` (table: `"notes"`)
  2. `TaskEntity` (table: `"tasks"`)
  3. `HabitEntity` (table: `"habits"`)
  4. `HabitLogEntity` (table: `"habit_logs"`)
  5. `ExpenseEntity` (table: `"expenses"`)
  6. `DiaryEntity` (table: `"diaries"`)
  7. `CaptureEntity` (table: `"captures"`)
  8. `LifeEventEntity` (table: `"life_events"`)

### `app/src/main/java/com/example/data/local/dao/Daos.kt`
- **Role:** Data Access Object interfaces providing SQL queries.
- **DAOs Defined:**
  1. `NoteDao`: Active notes, pinned queries, folder filters, search, soft delete/archive.
  2. `TaskDao`: Today's tasks, pending tasks, date range queries.
  3. `HabitDao`: Active habits, streak updates.
  4. `HabitLogDao`: Completion logs by date and habit ID.
  5. `ExpenseDao`: Chronological expenses, today's expenses, category sums.
  6. `DiaryDao`: Journal entries ordered by date desc, mood queries.
  7. `CaptureDao`: Fleeting capture stream.
  8. `LifeEventDao`: Chronological timeline event stream.

### `app/src/main/java/com/example/data/repository/LifeOSRepository.kt`
- **Lines of Code:** ~541
- **Role:** Central repository and business logic engine.
- **Key Responsibilities:**
  - Exposes reactive `Flow<List<T>>` streams for all entities.
  - Enforces cross-module event generation (creating `LifeEventEntity` on note creation, task completion, habit check-in, expense logging, and diary writing).
  - Handles initial seed generation (`checkAndSeedInitialData()`).
  - Implements global search across all tables (`performGlobalSearch()`).
  - Executes full database JSON export and import (`exportDataToJson()`, `importDataFromJson()`).

---

## 3. Domain Models

### `app/src/main/java/com/example/domain/model/Models.kt`
- **Role:** Core domain enums and data contracts.
- **Enums Defined:**
  - `Priority`: LOW, MEDIUM, HIGH, URGENT.
  - `Mood`: GREAT, HAPPY, CALM, NORMAL, SAD, ANGRY, STRESSED, LOVED, EXCITED.
  - `ExpenseCategory`: FOOD, CAFE, SHOPPING, BILLS, TRANSPORT, HEALTH, ENTERTAINMENT, EDUCATION, OTHER.
  - `CaptureType`: THOUGHT, PHOTO, VIDEO, AUDIO.
  - `LifeEventType`: NOTE_CREATED, TASK_COMPLETED, HABIT_COMPLETED, EXPENSE_LOGGED, DIARY_WRITTEN, CAPTURE_SAVED, MILESTONE.
- **Data Classes:**
  - `AIPermissions`: Granular boolean access flags for AI queries.
  - `DailyOverview`: Aggregated model for dashboard rendering.

---

## 4. Intelligence & AI Layer

### `app/src/main/java/com/example/ai/LifeOSAI.kt`
- **Lines of Code:** ~388
- **Role:** Dual-mode AI engine.
- **Key Functions:**
  - `answerQuestion(question, permissions, repository)`: Permission-gated conversational life engine.
  - `extractTasksFromText(text)`: Heuristic regex and intent-based to-do extractor.
  - `summarizeNote(title, content)`: Structural note summarizer.
  - `refineDiaryFromPoints(rawPoints)`: Converts disjointed thoughts into reflective diary entries.
  - `generateLifeSummary(overview)`: High-level day synthesis.
  - `callGeminiApi(prompt)`: OkHttp client for optional cloud fallback.

---

## 5. UI Presentation & ViewModels

### `app/src/main/java/com/example/ui/navigation/Navigation.kt`
- **Role:** Sealed routes (`Screen`) and `LifeOSBottomBar` with 5 navigation destinations.

### `app/src/main/java/com/example/ui/components/Components.kt`
- **Role:** Reusable UI elements (`GlassCard`, `GradientBannerCard`, `SectionHeader`, `StatPill`, `AppLockDialog`).

### `app/src/main/java/com/example/ui/screens/home/HomeViewModel.kt` & `HomeScreen.kt`
- **Role:** Daily productivity dashboard, progress circle, quick task add, habit checklist, global search results.

### `app/src/main/java/com/example/ui/screens/notes/NotesViewModel.kt`, `NotesScreen.kt` & `NoteEditorScreen.kt`
- **Role:** Note management, folder filtering, search, note creation/editing, and AI smart note dialogue.

### `app/src/main/java/com/example/ui/screens/capture/CaptureSheet.kt`
- **Role:** Modal bottom sheet for sub-3-second fleeting thought capture.

### `app/src/main/java/com/example/ui/screens/insights/InsightsViewModel.kt` & `InsightsScreen.kt`
- **Role:** Productivity rates, expense category breakdown with ranking, mood distribution charts, AI life queries.

### `app/src/main/java/com/example/ui/screens/life/LifeViewModel.kt` & `LifeHubScreen.kt`
- **Role:** 7-in-1 central hub: Timeline, Calendar, Diary, Expenses, Habits, AI Assistant, and Backup & Privacy.

---

## 6. Theme & Styling

### `app/src/main/java/com/example/ui/theme/Color.kt`, `Theme.kt`, `Type.kt`
- **Role:** Centralized Material 3 styling implementing the **Vibrant Palette** (Deep Purple `#6750A4` / Lavender `#EADDFF`).
