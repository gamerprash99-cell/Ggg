package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.entity.CaptureEntity
import com.example.data.local.entity.DiaryEntity
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.HabitLogEntity
import com.example.data.local.entity.LifeEventEntity
import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.TaskEntity
import com.example.domain.model.CaptureType
import com.example.domain.model.ExpenseCategory
import com.example.domain.model.LifeEventType
import com.example.domain.model.Mood
import com.example.domain.model.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LifeOSRepository(private val database: AppDatabase) {

    private val noteDao = database.noteDao()
    private val taskDao = database.taskDao()
    private val habitDao = database.habitDao()
    private val habitLogDao = database.habitLogDao()
    private val expenseDao = database.expenseDao()
    private val diaryDao = database.diaryDao()
    private val captureDao = database.captureDao()
    private val lifeEventDao = database.lifeEventDao()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    fun getTodayDate(): String = dateFormat.format(Date())
    fun getCurrentTime(): String = timeFormat.format(Date())

    // --- NOTES ---
    fun getAllNotes(): Flow<List<NoteEntity>> = noteDao.getAllActiveNotes()
    fun getFolders(): Flow<List<String>> = noteDao.getFolders()
    fun getNotesByFolder(folder: String): Flow<List<NoteEntity>> = noteDao.getNotesByFolder(folder)
    suspend fun getNoteById(id: Long): NoteEntity? = noteDao.getNoteById(id)

    suspend fun saveNote(title: String, content: String, folder: String, tags: String, isPinned: Boolean = false, existingId: Long = 0): Long {
        val note = if (existingId > 0) {
            val existing = noteDao.getNoteById(existingId)
            existing?.copy(
                title = title,
                content = content,
                folder = folder,
                tags = tags,
                isPinned = isPinned,
                updatedAt = System.currentTimeMillis()
            ) ?: NoteEntity(
                id = existingId,
                title = title,
                content = content,
                folder = folder,
                tags = tags,
                isPinned = isPinned
            )
        } else {
            NoteEntity(
                title = title,
                content = content,
                folder = folder,
                tags = tags,
                isPinned = isPinned
            )
        }
        val id = noteDao.insertNote(note)
        if (existingId == 0L) {
            lifeEventDao.insertEvent(
                LifeEventEntity(
                    date = getTodayDate(),
                    type = LifeEventType.NOTE_CREATED,
                    title = "Note: $title",
                    description = content.take(60),
                    sourceId = id,
                    tags = tags
                )
            )
        }
        return id
    }

    suspend fun togglePinNote(note: NoteEntity) {
        noteDao.updateNote(note.copy(isPinned = !note.isPinned, updatedAt = System.currentTimeMillis()))
    }

    suspend fun toggleFavoriteNote(note: NoteEntity) {
        noteDao.updateNote(note.copy(isFavorite = !note.isFavorite, updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteNote(note: NoteEntity) {
        noteDao.deleteNote(note)
    }

    // --- TASKS ---
    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()
    fun getTasksForToday(): Flow<List<TaskEntity>> = taskDao.getTasksForDate(getTodayDate())
    fun getPendingTasks(): Flow<List<TaskEntity>> = taskDao.getPendingTasks()
    suspend fun getTaskById(id: Long): TaskEntity? = taskDao.getTaskById(id)

    suspend fun addTask(
        title: String,
        description: String = "",
        dueDate: String = getTodayDate(),
        dueTime: String = "",
        priority: Priority = Priority.MEDIUM,
        category: String = "Personal"
    ): Long {
        val task = TaskEntity(
            title = title,
            description = description,
            dueDate = dueDate.ifEmpty { getTodayDate() },
            dueTime = dueTime,
            priority = priority,
            category = category
        )
        return taskDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun toggleTaskCompletion(task: TaskEntity) {
        val newStatus = !task.isCompleted
        val updated = task.copy(
            isCompleted = newStatus,
            completedAt = if (newStatus) System.currentTimeMillis() else null
        )
        taskDao.updateTask(updated)
        if (newStatus) {
            lifeEventDao.insertEvent(
                LifeEventEntity(
                    date = getTodayDate(),
                    type = LifeEventType.TASK_COMPLETED,
                    title = "Completed: ${task.title}",
                    description = "Category: ${task.category}",
                    sourceId = task.id
                )
            )
        }
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    // --- HABITS ---
    fun getAllHabits(): Flow<List<HabitEntity>> = habitDao.getAllHabits()
    fun getHabitLogsForToday(): Flow<List<HabitLogEntity>> = habitLogDao.getLogsForDate(getTodayDate())
    fun getAllHabitLogs(): Flow<List<HabitLogEntity>> = habitLogDao.getAllLogs()
    suspend fun getHabitById(id: Long): HabitEntity? = habitDao.getHabitById(id)

    suspend fun addHabit(
        title: String,
        icon: String = "🔥",
        category: String = "Daily",
        frequency: String = "Daily",
        targetDays: Int = 7,
        reminderTime: String = "",
        reminderDays: String = "Every day"
    ): Long {
        return habitDao.insertHabit(
            HabitEntity(
                title = title,
                icon = icon,
                category = category,
                frequency = frequency,
                targetDaysPerWeek = targetDays,
                reminderTime = reminderTime,
                reminderDays = reminderDays
            )
        )
    }

    suspend fun updateHabit(habit: HabitEntity) {
        habitDao.updateHabit(habit)
    }

    suspend fun toggleHabitForDate(habit: HabitEntity, date: String = getTodayDate()) {
        val existing = habitLogDao.getLog(habit.id, date)
        if (existing != null) {
            habitLogDao.deleteLog(habit.id, date)
        } else {
            habitLogDao.insertLog(HabitLogEntity(habitId = habit.id, date = date))
            lifeEventDao.insertEvent(
                LifeEventEntity(
                    date = date,
                    type = LifeEventType.HABIT_COMPLETED,
                    title = "${habit.icon} Habit: ${habit.title}",
                    description = "Completed on $date",
                    sourceId = habit.id
                )
            )
        }
        recalculateAndSaveStreaks(habit.id)
    }

    suspend fun recalculateAndSaveStreaks(habitId: Long) {
        val habit = habitDao.getHabitById(habitId) ?: return
        val logs = habitLogDao.getAllLogs().first().filter { it.habitId == habitId }
        val (currentStreak, bestStreak) = calculateStreaksFromLogs(logs.map { it.date })
        habitDao.updateHabit(
            habit.copy(
                currentStreak = currentStreak,
                bestStreak = maxOf(habit.bestStreak, bestStreak)
            )
        )
    }

    fun calculateStreaksFromLogs(completedDateStrings: List<String>): Pair<Int, Int> {
        if (completedDateStrings.isEmpty()) return Pair(0, 0)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateSet = completedDateStrings.toSet()

        // Calculate current streak
        val cal = java.util.Calendar.getInstance()
        val todayStr = format.format(cal.time)
        cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = format.format(cal.time)

        var currentStreak = 0
        val checkCal = java.util.Calendar.getInstance()

        if (dateSet.contains(todayStr)) {
            // Started today
            while (true) {
                val dStr = format.format(checkCal.time)
                if (dateSet.contains(dStr)) {
                    currentStreak++
                    checkCal.add(java.util.Calendar.DAY_OF_YEAR, -1)
                } else {
                    break
                }
            }
        } else if (dateSet.contains(yesterdayStr)) {
            // Still active from yesterday
            checkCal.add(java.util.Calendar.DAY_OF_YEAR, -1)
            while (true) {
                val dStr = format.format(checkCal.time)
                if (dateSet.contains(dStr)) {
                    currentStreak++
                    checkCal.add(java.util.Calendar.DAY_OF_YEAR, -1)
                } else {
                    break
                }
            }
        }

        // Calculate all-time best streak
        val sortedDates = completedDateStrings.distinct().sorted()
        var bestStreak = 0
        var tempStreak = 0
        var prevDate: Date? = null

        for (dStr in sortedDates) {
            val d = try { format.parse(dStr) } catch (e: Exception) { null } ?: continue
            if (prevDate == null) {
                tempStreak = 1
            } else {
                val diffDays = ((d.time - prevDate.time) / (1000 * 60 * 60 * 24)).toInt()
                if (diffDays == 1) {
                    tempStreak++
                } else if (diffDays > 1) {
                    tempStreak = 1
                }
            }
            prevDate = d
            if (tempStreak > bestStreak) {
                bestStreak = tempStreak
            }
        }

        return Pair(currentStreak, maxOf(bestStreak, currentStreak))
    }

    suspend fun deleteHabit(habit: HabitEntity) {
        habitDao.deleteHabit(habit)
    }

    // --- EXPENSES ---
    fun getAllExpenses(): Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
    fun getExpensesForToday(): Flow<List<ExpenseEntity>> = expenseDao.getExpensesForDate(getTodayDate())
    suspend fun getExpenseById(id: Long): ExpenseEntity? = expenseDao.getExpenseById(id)

    suspend fun addExpense(
        amount: Double,
        category: ExpenseCategory,
        note: String = "",
        paymentMethod: String = "UPI",
        tags: String = "",
        date: String = getTodayDate()
    ): Long {
        val id = expenseDao.insertExpense(
            ExpenseEntity(
                amount = amount,
                category = category,
                date = date,
                time = getCurrentTime(),
                paymentMethod = paymentMethod,
                note = note,
                tags = tags
            )
        )
        lifeEventDao.insertEvent(
            LifeEventEntity(
                date = date,
                type = LifeEventType.EXPENSE_RECORDED,
                title = "Spent ₹${String.format(Locale.getDefault(), "%.0f", amount)} on ${category.displayName}",
                description = note.ifEmpty { "${category.icon} ${category.displayName}" },
                sourceId = id,
                tags = tags
            )
        )
        return id
    }

    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    // --- DIARY ---
    fun getAllDiaries(): Flow<List<DiaryEntity>> = diaryDao.getAllDiaries()
    suspend fun getDiaryForToday(): DiaryEntity? = diaryDao.getDiaryForDate(getTodayDate())
    suspend fun getDiaryForDate(date: String): DiaryEntity? = diaryDao.getDiaryForDate(date)
    suspend fun getDiaryById(id: Long): DiaryEntity? = diaryDao.getDiaryById(id)

    suspend fun saveDiary(
        title: String,
        content: String,
        mood: Mood,
        tags: String = "",
        date: String = getTodayDate(),
        mediaUri: String = ""
    ): Long {
        val existing = diaryDao.getDiaryForDate(date)
        val id = if (existing != null) {
            val updated = existing.copy(
                title = title,
                content = content,
                mood = mood,
                tags = tags,
                mediaUri = mediaUri
            )
            diaryDao.updateDiary(updated)
            existing.id
        } else {
            diaryDao.insertDiary(
                DiaryEntity(
                    title = title,
                    content = content,
                    mood = mood,
                    tags = tags,
                    date = date,
                    mediaUri = mediaUri
                )
            )
        }
        lifeEventDao.insertEvent(
            LifeEventEntity(
                date = date,
                type = LifeEventType.DIARY_CREATED,
                title = "Diary: $title",
                description = content.take(80),
                sourceId = id,
                tags = tags,
                mood = mood
            )
        )
        return id
    }

    suspend fun updateDiary(diary: DiaryEntity) {
        diaryDao.updateDiary(diary)
    }

    suspend fun deleteDiary(diary: DiaryEntity) {
        diaryDao.deleteDiary(diary)
    }

    // --- CAPTURES ---
    fun getAllCaptures(): Flow<List<CaptureEntity>> = captureDao.getAllCaptures()
    suspend fun getCaptureById(id: Long): CaptureEntity? = captureDao.getCaptureById(id)

    suspend fun saveCapture(
        type: CaptureType,
        title: String,
        note: String = "",
        mediaUri: String = "",
        mood: Mood? = null,
        tags: String = "",
        date: String = getTodayDate()
    ): Long {
        val id = captureDao.insertCapture(
            CaptureEntity(
                type = type,
                title = title,
                note = note,
                mediaUri = mediaUri,
                mood = mood,
                tags = tags,
                date = date
            )
        )
        lifeEventDao.insertEvent(
            LifeEventEntity(
                date = date,
                type = LifeEventType.CAPTURE_SAVED,
                title = "${type.name.lowercase().replaceFirstChar { it.uppercase() }}: $title",
                description = note,
                sourceId = id,
                tags = tags,
                mood = mood
            )
        )
        return id
    }

    suspend fun updateCapture(capture: CaptureEntity) {
        captureDao.updateCapture(capture)
    }

    suspend fun deleteCapture(capture: CaptureEntity) {
        if (capture.mediaUri.isNotEmpty()) {
            com.example.util.MediaStorageHelper.deleteFile(capture.mediaUri)
        }
        captureDao.deleteCapture(capture)
    }

    // --- TIMELINE & CALENDAR ---
    fun getAllLifeEvents(): Flow<List<LifeEventEntity>> = lifeEventDao.getAllEvents()
    fun getLifeEventsForDate(date: String): Flow<List<LifeEventEntity>> = lifeEventDao.getEventsForDate(date)
    suspend fun deleteLifeEvent(event: LifeEventEntity) {
        lifeEventDao.deleteEvent(event)
    }

    // --- GLOBAL SEARCH ---
    suspend fun performGlobalSearch(query: String): GlobalSearchResult {
        val q = query.trim()
        if (q.isEmpty()) return GlobalSearchResult()
        val notes = noteDao.searchNotes(q).first()
        val tasks = taskDao.searchTasks(q).first()
        val expenses = expenseDao.searchExpenses(q).first()
        val diaries = diaryDao.searchDiaries(q).first()
        val captures = captureDao.searchCaptures(q).first()
        val events = lifeEventDao.searchEvents(q).first()
        return GlobalSearchResult(notes, tasks, expenses, diaries, captures, events)
    }

    // --- BACKUP & EXPORT ---
    suspend fun exportDataToJson(): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("appName", "LifeOS")
        root.put("exportTime", System.currentTimeMillis())

        val notesArray = JSONArray()
        noteDao.getAllActiveNotes().first().forEach {
            notesArray.put(JSONObject().apply {
                put("title", it.title)
                put("content", it.content)
                put("folder", it.folder)
                put("tags", it.tags)
                put("isPinned", it.isPinned)
            })
        }
        root.put("notes", notesArray)

        val tasksArray = JSONArray()
        taskDao.getAllTasks().first().forEach {
            tasksArray.put(JSONObject().apply {
                put("title", it.title)
                put("description", it.description)
                put("dueDate", it.dueDate)
                put("priority", it.priority.name)
                put("category", it.category)
                put("isCompleted", it.isCompleted)
            })
        }
        root.put("tasks", tasksArray)

        val habitsArray = JSONArray()
        habitDao.getAllHabits().first().forEach {
            habitsArray.put(JSONObject().apply {
                put("title", it.title)
                put("icon", it.icon)
                put("category", it.category)
                put("currentStreak", it.currentStreak)
                put("bestStreak", it.bestStreak)
            })
        }
        root.put("habits", habitsArray)

        val expensesArray = JSONArray()
        expenseDao.getAllExpenses().first().forEach {
            expensesArray.put(JSONObject().apply {
                put("amount", it.amount)
                put("category", it.category.name)
                put("date", it.date)
                put("note", it.note)
                put("paymentMethod", it.paymentMethod)
            })
        }
        root.put("expenses", expensesArray)

        val diariesArray = JSONArray()
        diaryDao.getAllDiaries().first().forEach {
            diariesArray.put(JSONObject().apply {
                put("title", it.title)
                put("content", it.content)
                put("mood", it.mood.name)
                put("date", it.date)
                put("tags", it.tags)
            })
        }
        root.put("diaries", diariesArray)

        return root.toString(2)
    }

    suspend fun importDataFromJson(jsonStr: String): Boolean {
        return try {
            val root = JSONObject(jsonStr)
            if (root.has("notes")) {
                val array = root.getJSONArray("notes")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    saveNote(
                        title = obj.optString("title", "Imported Note"),
                        content = obj.optString("content", ""),
                        folder = obj.optString("folder", "General"),
                        tags = obj.optString("tags", ""),
                        isPinned = obj.optBoolean("isPinned", false)
                    )
                }
            }
            if (root.has("tasks")) {
                val array = root.getJSONArray("tasks")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    addTask(
                        title = obj.optString("title", "Imported Task"),
                        description = obj.optString("description", ""),
                        dueDate = obj.optString("dueDate", getTodayDate()),
                        category = obj.optString("category", "Personal")
                    )
                }
            }
            if (root.has("habits")) {
                val array = root.getJSONArray("habits")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    addHabit(
                        title = obj.optString("title", "Imported Habit"),
                        icon = obj.optString("icon", "🔥"),
                        category = obj.optString("category", "Daily")
                    )
                }
            }
            if (root.has("expenses")) {
                val array = root.getJSONArray("expenses")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val catName = obj.optString("category", "OTHER")
                    val cat = try { ExpenseCategory.valueOf(catName) } catch (e: Exception) { ExpenseCategory.OTHER }
                    addExpense(
                        amount = obj.optDouble("amount", 0.0),
                        category = cat,
                        note = obj.optString("note", ""),
                        date = obj.optString("date", getTodayDate())
                    )
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    // --- SEED INITIAL DATA IF FIRST RUN ---
    suspend fun checkAndSeedInitialData() {
        val existingHabits = habitDao.getAllHabits().first()
        if (existingHabits.isEmpty()) {
            addHabit("Workout", "🔥", "Fitness", "Daily", 5)
            addHabit("Reading", "📚", "Mind", "Daily", 7)
            addHabit("Drink Water", "💧", "Health", "Daily", 7)
            addHabit("Meditation", "🧘", "Mindfulness", "Daily", 7)
        }

        val existingTasks = taskDao.getAllTasks().first()
        if (existingTasks.isEmpty()) {
            addTask("Welcome to LifeOS", "Explore Notes, Tasks, Habits, Timeline, and central AI Assistant", getTodayDate(), "09:00 AM", Priority.HIGH, "LifeOS")
            addTask("Review today's goals", "Check daily progress and capture a quick memory", getTodayDate(), "06:00 PM", Priority.MEDIUM, "Personal")
        }

        val existingNotes = noteDao.getAllActiveNotes().first()
        if (existingNotes.isEmpty()) {
            saveNote(
                title = "Welcome to your Second Brain 🧠",
                content = "LifeOS is designed to connect your life through Notes, Tasks, Habits, Expenses, and Private Memories.\n\nUse ✨ 'Ask LifeOS AI' inside any note to summarize, extract tasks directly into your task list, or polish your writing.",
                folder = "Ideas",
                tags = "#lifeos, #welcome",
                isPinned = true
            )
        }

        val existingExpenses = expenseDao.getAllExpenses().first()
        if (existingExpenses.isEmpty()) {
            addExpense(120.0, ExpenseCategory.CAFE, "Morning Espresso", "UPI", "#morning")
        }

        val existingDiaries = diaryDao.getAllDiaries().first()
        if (existingDiaries.isEmpty()) {
            saveDiary(
                title = "Starting My LifeOS Journey",
                content = "Set up LifeOS today. Everything is private, offline, and stored locally on my device. Ready to capture and organize my days.",
                mood = Mood.GREAT,
                tags = "#freshstart, #productivity"
            )
        }
    }
}

data class GlobalSearchResult(
    val notes: List<NoteEntity> = emptyList(),
    val tasks: List<TaskEntity> = emptyList(),
    val expenses: List<ExpenseEntity> = emptyList(),
    val diaries: List<DiaryEntity> = emptyList(),
    val captures: List<CaptureEntity> = emptyList(),
    val events: List<LifeEventEntity> = emptyList()
) {
    val totalCount: Int get() = notes.size + tasks.size + expenses.size + diaries.size + captures.size
}
