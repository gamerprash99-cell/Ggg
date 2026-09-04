package com.example.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.LifeOSApp
import com.example.data.local.entity.CaptureEntity
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.HabitLogEntity
import com.example.data.local.entity.TaskEntity
import com.example.data.repository.GlobalSearchResult
import com.example.data.repository.LifeOSRepository
import com.example.domain.model.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class HomeUiState(
    val dateString: String = "",
    val dayOfWeek: String = "",
    val isEvening: Boolean = false,
    val tasks: List<TaskEntity> = emptyList(),
    val habits: List<HabitEntity> = emptyList(),
    val todayHabitLogs: List<HabitLogEntity> = emptyList(),
    val todayExpenses: List<ExpenseEntity> = emptyList(),
    val latestCapture: CaptureEntity? = null,
    val totalTodaySpending: Double = 0.0,
    val taskCompletionPercentage: Int = 0,
    val overallProgressPercentage: Int = 0
)

class HomeViewModel(
    private val repository: LifeOSRepository = LifeOSApp.repo
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResult = MutableStateFlow<GlobalSearchResult?>(null)
    val searchResult = _searchResult.asStateFlow()

    private val calendar = Calendar.getInstance()
    private val isEvening = calendar.get(Calendar.HOUR_OF_DAY) >= 17

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getTasksForToday(),
        repository.getAllHabits(),
        repository.getHabitLogsForToday(),
        repository.getExpensesForToday(),
        repository.getAllCaptures()
    ) { tasks, habits, habitLogs, expenses, captures ->
        val completedTasks = tasks.count { it.isCompleted }
        val completedHabits = habits.count { h -> habitLogs.any { it.habitId == h.id } }

        val totalItems = tasks.size + habits.size
        val completedItems = completedTasks + completedHabits
        val overallProgress = if (totalItems > 0) (completedItems * 100 / totalItems) else 100
        val taskPct = if (tasks.isNotEmpty()) (completedTasks * 100 / tasks.size) else 100

        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

        HomeUiState(
            dateString = dateFormat.format(Date()).uppercase(),
            dayOfWeek = dayFormat.format(Date()).uppercase(),
            isEvening = isEvening,
            tasks = tasks,
            habits = habits,
            todayHabitLogs = habitLogs,
            todayExpenses = expenses,
            latestCapture = captures.firstOrNull(),
            totalTodaySpending = expenses.sumOf { it.amount },
            taskCompletionPercentage = taskPct,
            overallProgressPercentage = overallProgress
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
        }
    }

    fun toggleHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.toggleHabitForDate(habit)
        }
    }

    fun quickAddTask(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addTask(
                title = title.trim(),
                priority = Priority.MEDIUM,
                category = "General"
            )
        }
    }

    fun updateTask(context: android.content.Context, task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
            if (task.reminderTime.isNotBlank() && !task.isCompleted) {
                val parts = task.reminderTime.split(":")
                if (parts.size == 2) {
                    val h = parts[0].toIntOrNull() ?: 0
                    val m = parts[1].toIntOrNull() ?: 0
                    com.example.util.NotificationHelper.scheduleReminder(
                        context, h, m, "LifeOS Task: ${task.title}", task.description.ifEmpty { "Time to focus on this task." }, task.id.toInt()
                    )
                }
            } else {
                 com.example.util.NotificationHelper.cancelReminder(context, task.id.toInt())
            }
        }
    }

    fun deleteTask(context: android.content.Context, task: TaskEntity) {
        viewModelScope.launch {
            com.example.util.NotificationHelper.cancelReminder(context, task.id.toInt())
            repository.deleteTask(task)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResult.value = null
        } else {
            viewModelScope.launch {
                _searchResult.value = repository.performGlobalSearch(query)
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchResult.value = null
    }
}
