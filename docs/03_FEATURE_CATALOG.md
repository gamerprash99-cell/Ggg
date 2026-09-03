# LifeOS — Complete Feature Catalog

This document provides an exhaustive inventory of all functional capabilities found within LifeOS.

---

## 1. Feature Status Legend

- ✅ **Implemented:** Fully functional in code, connected to UI, ViewModel, Repository, and Database.
- 🟡 **Partially Implemented:** Functional in core logic or UI, but missing peripheral integrations (e.g., hardware sensor access, persistence of UI flags).
- 🔴 **Not Implemented:** Specified in product vision or common mental model, but completely absent from code.
- ⚠️ **Implemented but Needs Verification:** Functional in code but has known discrepancies or unverified edge cases.

---

## 2. Feature Breakdown by Pillar

### 2.1 Daily Dashboard & Productivity (Home)
- **Feature Name:** Daily Progress Card & Morning/Evening Overview
  - **Status:** ✅ Implemented
  - **Purpose:** Gives the user an immediate status check of their day.
  - **User Value:** Encourages daily momentum with visual progress percentages and contextual greetings.
  - **Current Implementation:** `HomeScreen.kt` displays uppercase formatted date, day of week, day/evening header, overall progress percentage circular badge, task completion rate, total today's spending in ₹, and active habit streaks.
  - **Relevant Files:** `ui/screens/home/HomeScreen.kt`, `ui/screens/home/HomeViewModel.kt`, `data/repository/LifeOSRepository.kt`.
  - **Entities Involved:** `TaskEntity`, `HabitEntity`, `HabitLogEntity`, `ExpenseEntity`.
  - **Offline Behavior:** 100% offline. Computes dynamically via Kotlin `combine` flow.
  - **AI Involvement:** None in standard rendering.

- **Feature Name:** Quick Task Completion & Inline Task Creation
  - **Status:** ✅ Implemented
  - **Purpose:** Rapid checking off of daily to-dos.
  - **User Value:** Users can add a task with one tap or mark items complete without leaving the dashboard.
  - **Current Implementation:** Checkbox toggles task completion, which updates Room and automatically logs a `LifeEventEntity` of type `TASK_COMPLETED`.
  - **Relevant Files:** `HomeScreen.kt`, `HomeViewModel.kt`.

- **Feature Name:** Global In-App Search
  - **Status:** ✅ Implemented
  - **Purpose:** Search across all notes, tasks, expenses, diaries, and life events simultaneously.
  - **User Value:** Single unified query box on the home screen finds anything stored in the second brain.
  - **Current Implementation:** `HomeViewModel.updateSearchQuery()` calls `LifeOSRepository.performGlobalSearch()` which executes SQL `LIKE` queries concurrently across 6 DAOs.
  - **Entities Involved:** `NoteEntity`, `TaskEntity`, `ExpenseEntity`, `DiaryEntity`, `CaptureEntity`, `LifeEventEntity`.

---

### 2.2 Second Brain & Notes
- **Feature Name:** Folder-Based Note Organization
  - **Status:** ✅ Implemented
  - **Purpose:** Categorize personal notes into folders (Ideas, College, Projects, Personal, Finance, etc.).
  - **User Value:** Prevents cognitive overload and keeps long-form thoughts structured.
  - **Current Implementation:** `NotesScreen.kt` displays horizontal scrollable folder chips with active counts.
  - **Entities Involved:** `NoteEntity` (fields: `id`, `title`, `content`, `folder`, `tags`, `isPinned`, `isFavorite`, `isArchived`, `createdAt`, `updatedAt`).
  - **Relevant Files:** `ui/screens/notes/NotesScreen.kt`, `ui/screens/notes/NotesViewModel.kt`.

- **Feature Name:** Pinned & Favorite Notes
  - **Status:** ✅ Implemented
  - **Purpose:** Keep high-priority notes at the top of the list.
  - **Current Implementation:** `togglePinNote` and `toggleFavoriteNote` update the database, sorted via `NoteDao.getAllActiveNotes()` (`ORDER BY isPinned DESC, updatedAt DESC`).

- **Feature Name:** Rich Note Editor
  - **Status:** ✅ Implemented
  - **Purpose:** Fullscreen editing interface with title, body, tag inputs, and folder selection.
  - **Current Implementation:** `NoteEditorScreen.kt` supporting create mode (`noteId == 0L`) and edit mode (`noteId > 0L`).

- **Feature Name:** AI Note Summarizer
  - **Status:** ✅ Implemented
  - **Purpose:** Generate structured executive summaries of note contents.
  - **Current Implementation:** Triggered via ✨ "Ask LifeOS AI" dialog inside `NotesScreen.kt`. Calls `LifeOSAI.summarizeNote()` which parses text length, key themes, and actionable next steps. Runs locally.

- **Feature Name:** AI Task Extraction from Notes
  - **Status:** ✅ Implemented
  - **Purpose:** Automatically scans note text for checklists, bullet points (`•`, `-`, `*`, `[ ]`), or intent keywords (`need to`, `have to`, `buy`, `call`, `finish`) and converts them directly into `TaskEntity` records in the user's task manager.
  - **Current Implementation:** `LifeOSAI.extractTasksFromText()`. Presents extracted candidates with approval buttons before insertion into Room.

---

### 2.3 Quick Capture
- **Feature Name:** Sub-3-Second Quick Capture Modal Sheet
  - **Status:** ✅ Implemented
  - **Purpose:** Instant capture from anywhere in the app via the central floating bottom bar button.
  - **User Value:** Removes navigation friction when capturing fleeting thoughts.
  - **Current Implementation:** `CaptureSheet.kt` modal bottom sheet. Supports 4 capture types (`THOUGHT`, `PHOTO`, `VIDEO`, `AUDIO`), title, note, mood chips, and tags.
  - **Entities Involved:** `CaptureEntity`, `LifeEventEntity`.
  - **Known Limitations:** Hardware camera capture and microphone audio recording intents are not directly wired to the OS media recorder; the system records the structured thought and metadata into Room with support for a media URI string.

---

### 2.4 Insights & Analytics
- **Feature Name:** Productivity Analytics
  - **Status:** ✅ Implemented
  - **Purpose:** Real-time visibility into task completion rates.
  - **Current Implementation:** `InsightsScreen.kt` and `InsightsViewModel.kt` calculate completion percentages dynamically from `getAllTasks()`.

- **Feature Name:** Expense Breakdown by Category
  - **Status:** ✅ Implemented
  - **Purpose:** Visual breakdown of spending by category (Food, Cafe, Shopping, Bills, etc.).
  - **Current Implementation:** Computes total spend, groups expenses by `ExpenseCategory`, calculates category percentages, and displays ranked progress bars with category icons.

- **Feature Name:** Mood Distribution & Well-being
  - **Status:** ✅ Implemented
  - **Purpose:** Aggregates recorded moods from diary entries.
  - **Current Implementation:** Summarizes dominant emotional trends across Great, Happy, Calm, Normal, Sad, Angry, Stressed, Loved, and Excited.

---

### 2.5 Life Hub (5-in-1 Central Hub)
- **Feature Name:** Chronological Life Timeline
  - **Status:** ✅ Implemented
  - **Purpose:** Unified stream of all life activities (notes saved, tasks finished, habits completed, expenses logged, diary reflections).
  - **Current Implementation:** `LifeHubScreen.kt` Tab 0 ("⏱️ Timeline"). Consumes `LifeEventDao.getAllEvents()`.

- **Feature Name:** Interactive Calendar View
  - **Status:** ✅ Implemented
  - **Purpose:** Date picker to view events for any specific calendar day.
  - **Current Implementation:** `LifeHubScreen.kt` Tab 1 ("📅 Calendar"). Filters life events by date string (`YYYY-MM-DD`).

- **Feature Name:** Daily Mood Diary & Journal
  - **Status:** ✅ Implemented
  - **Purpose:** Long-form journaling with mood selection and tag support.
  - **Current Implementation:** Tab 2 ("📔 Diary"). Also features AI Voice/Thought refinement (`refineDiaryWithAI`) which structures unstructured thoughts into formatted journal reflections.

- **Feature Name:** Expense Manager
  - **Status:** ✅ Implemented
  - **Purpose:** Dedicated expense tracking ledger with payment methods (UPI, Card, Cash) and delete actions.
  - **Current Implementation:** Tab 3 ("💸 Expenses").

- **Feature Name:** Habit Tracker & Streak Engine
  - **Status:** ✅ Implemented
  - **Purpose:** Habit consistency manager with streak counters and completion logs.
  - **Current Implementation:** Tab 4 ("🔥 Habits"). Manages `HabitEntity` and `HabitLogEntity`. Streak increments when completed and updates best streak.

- **Feature Name:** Central LifeOS AI Assistant Chat
  - **Status:** ✅ Implemented
  - **Purpose:** Interactive conversational assistant that knows the user's life context.
  - **Current Implementation:** Tab 5 ("🤖 AI Assistant"). Chat interface with permission switches (`AIPermissions`). Runs locally via `LifeOSAI.answerQuestion()`.

- **Feature Name:** JSON Backup & Restore System
  - **Status:** ✅ Implemented
  - **Purpose:** Complete export and import of user data without cloud reliance.
  - **Current Implementation:** Tab 6 ("⚙️ Backup & Privacy"). `LifeOSRepository.exportDataToJson()` generates a single JSON document containing all notes, tasks, habits, expenses, and diaries. `importDataFromJson()` parses and restores the database.

- **Feature Name:** App PIN Lock
  - **Status:** 🟡 Partially implemented
  - **Purpose:** Protect sensitive notes and diary with a numeric PIN.
  - **Current Implementation:** `AppLockDialog.kt` component exists with PIN entry logic and toggle in settings, but app-level lifecycle interceptor is currently initialized to `isAppUnlocked = true` by default in `MainActivity.kt`.
