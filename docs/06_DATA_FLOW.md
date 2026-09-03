# LifeOS — Data Flow & State Lifecycle

This document traces the exact path of data as it travels between user actions, ViewModels, repository logic, database tables, and UI updates.

---

## 1. Unidirectional Data Flow (UDF) Loop

All state in LifeOS obeys the unidirectional data flow cycle:

```
                  ┌────────────────────────────────────────┐
                  │              User Gesture              │
                  │ (Tap Checkbox, Type Note, Submit Form) │
                  └───────────────────┬────────────────────┘
                                      │
                                      ▼
                  ┌────────────────────────────────────────┐
                  │             ViewModel Call             │
                  │   viewModel.toggleTask(task)           │
                  └───────────────────┬────────────────────┘
                                      │
                                      ▼
                  ┌────────────────────────────────────────┐
                  │      LifeOSRepository Mutation         │
                  │   suspend fun toggleTaskCompletion()   │
                  └───────────────────┬────────────────────┘
                                      │
                                      ▼
                  ┌────────────────────────────────────────┐
                  │           Room Database DAO            │
                  │   taskDao.updateTask(updatedTask)      │
                  │   lifeEventDao.insertEvent(...)        │
                  └───────────────────┬────────────────────┘
                                      │
                                      ▼
                  ┌────────────────────────────────────────┐
                  │         SQLite Storage Write           │
                  │   Record updated on device flash       │
                  └───────────────────┬────────────────────┘
                                      │
                                      ▼
                  ┌────────────────────────────────────────┐
                  │         Reactive DAO Flow Emitter      │
                  │   Room emits updated List<TaskEntity>  │
                  └───────────────────┬────────────────────┘
                                      │
                                      ▼
                  ┌────────────────────────────────────────┐
                  │         ViewModel State Combine        │
                  │   StateFlow<HomeUiState> recalculates  │
                  └───────────────────┬────────────────────┘
                                      │
                                      ▼
                  ┌────────────────────────────────────────┐
                  │         Compose Recomposition          │
                  │   UI renders updated state cleanly     │
                  └────────────────────────────────────────┘
```

---

## 2. Step-by-Step Flow: Specific User Workflows

### 2.1 Workflow: Completing a Task
1. **User Action:** User taps a task checkbox on `HomeScreen` or in `LifeHubScreen`.
2. **Presentation:** Composable invokes `viewModel.toggleTask(task)`.
3. **ViewModel:** `HomeViewModel` launches a coroutine in `viewModelScope` calling `repository.toggleTaskCompletion(task)`.
4. **Repository (`LifeOSRepository.kt` lines 131–150):**
   - Inverts boolean `isCompleted = !task.isCompleted`.
   - Sets `completedAt` timestamp if true, or null if uncompleted.
   - Executes `taskDao.updateTask(updated)`.
   - If now completed, automatically constructs a `LifeEventEntity(type = LifeEventType.TASK_COMPLETED)` and executes `lifeEventDao.insertEvent()`.
5. **Database:** SQLite updates the `tasks` row and writes a new row to `life_events`.
6. **Reactive Stream:** Room's query dispatcher invalidates the active `Flow<List<TaskEntity>>` stream and pushes the new list.
7. **UI Update:** `combine()` in `HomeViewModel` recalculates `taskCompletionPercentage` and `overallProgressPercentage`. Compose triggers a localized recomposition, visually checking the item and updating the circular progress indicator.

---

### 2.2 Workflow: Saving a Note
1. **User Action:** User writes content in `NoteEditorScreen` and taps "Save".
2. **Presentation:** Calls `notesViewModel.saveNote(title, content, folder, tags, isPinned, noteId)`.
3. **ViewModel:** Dispatches `repository.saveNote(...)`.
4. **Repository (`LifeOSRepository.kt` lines 52–93):**
   - Checks if `existingId > 0`:
     - If yes, fetches existing note, copies values, updates `updatedAt = System.currentTimeMillis()`, and updates Room.
     - If no, generates new `NoteEntity` and inserts.
   - If new note, automatically writes a `LifeEventEntity(type = LifeEventType.NOTE_CREATED)` to the timeline.
5. **Database:** Persists in `notes` table.
6. **UI Update:** `NoteDao.getAllActiveNotes()` emits new list sorted by `isPinned DESC, updatedAt DESC`. Navigation pops backstack to `NotesScreen`.

---

### 2.3 Workflow: Asking LifeOS AI a Life Query
1. **User Action:** User types "How much did I spend on food?" in the AI Assistant tab or Insights screen.
2. **ViewModel:** Invokes `LifeViewModel.sendAIMessage(question)`. Immediately appends the user's message to `_aiChatMessages` with `isUser = true` and sets `isAILoading = true`.
3. **Intelligence Processing (`LifeOSAI.kt` lines 59–191):**
   - Dispatches work to `Dispatchers.IO`.
   - Normalizes text: `val q = question.lowercase().trim()`.
   - Checks `permissions.accessExpenses`.
   - Filters `q` for keywords: `contains("food")` or `contains("cafe")`.
   - Fetches real SQLite records from `repository.getAllExpenses().first()`.
   - Aggregates expenses where `category == ExpenseCategory.FOOD || category == ExpenseCategory.CAFE`.
   - Formats a localized response string (e.g., `"You have spent a total of ₹1,450 on Food & Cafe across 8 entries."`) and builds an `AICard("Food Spending", "₹1,450", "Food & Cafe total", "EXPENSE")`.
4. **ViewModel Delivery:** `sendAIMessage` receives the `AIResponse`, appends a new `ChatMessage` with `isUser = false`, and resets `isAILoading = false`.
5. **UI Update:** Compose lazy column in `LifeHubScreen` scrolls smoothly to the newly rendered AI card and bubble. Zero internet data was sent.

---

### 2.4 Workflow: JSON Database Backup & Export
1. **User Action:** User taps "Generate Local Backup" in `LifeHubScreen` (Backup & Privacy tab).
2. **Repository (`LifeOSRepository.kt` lines 358–426):**
   - Constructs a root `JSONObject` with version `1` and export timestamp.
   - Concurrently collects snapshots using `.first()` across `noteDao`, `taskDao`, `habitDao`, `expenseDao`, and `diaryDao`.
   - Formats each entity list into a `JSONArray`.
   - Serializes to an indented string: `root.toString(2)`.
3. **ViewModel Delivery:** `LifeViewModel` sets `_backupJsonString` and `_backupMessage`.
4. **UI Display:** Renders the payload length and displays the structured backup string with a "Copy to Clipboard" affordance.
