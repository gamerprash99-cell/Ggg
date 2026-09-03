package com.example.ui.screens.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.LifeOSApp
import com.example.ai.AIResponse
import com.example.ai.LifeOSAI
import com.example.data.local.entity.DiaryEntity
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.HabitLogEntity
import com.example.data.local.entity.TaskEntity
import com.example.data.repository.LifeOSRepository
import com.example.domain.model.AIPermissions
import com.example.domain.model.ExpenseCategory
import com.example.domain.model.Mood
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CategorySpend(
    val category: ExpenseCategory,
    val amount: Double,
    val percentage: Int
)

data class InsightsUiState(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val taskCompletionRate: Int = 0,
    val habits: List<HabitEntity> = emptyList(),
    val habitLogs: List<HabitLogEntity> = emptyList(),
    val totalSpending: Double = 0.0,
    val categorySpends: List<CategorySpend> = emptyList(),
    val moodDistribution: Map<Mood, Int> = emptyMap(),
    val aiInsightResponse: AIResponse? = null,
    val isAILoading: Boolean = false
)

private data class BaseStats(
    val totalTasks: Int,
    val completedTasks: Int,
    val taskCompletionRate: Int,
    val habits: List<HabitEntity>,
    val habitLogs: List<HabitLogEntity>,
    val totalSpending: Double,
    val categorySpends: List<CategorySpend>,
    val moodDistribution: Map<Mood, Int>
)

class InsightsViewModel(
    private val repository: LifeOSRepository = LifeOSApp.repo
) : ViewModel() {

    private val _aiInsightResponse = MutableStateFlow<AIResponse?>(null)
    private val _isAILoading = MutableStateFlow(false)

    private val baseStatsFlow = combine(
        repository.getAllTasks(),
        repository.getAllHabits(),
        repository.getAllHabitLogs(),
        repository.getAllExpenses(),
        repository.getAllDiaries()
    ) { tasks: List<TaskEntity>, habits: List<HabitEntity>, logs: List<HabitLogEntity>, expenses: List<ExpenseEntity>, diaries: List<DiaryEntity> ->
        val completed = tasks.count { it.isCompleted }
        val rate = if (tasks.isNotEmpty()) (completed * 100 / tasks.size) else 100

        val totalSpent = expenses.sumOf { it.amount }
        val groupedExpenses = expenses.groupBy { it.category }
        val categorySpends = groupedExpenses.map { (cat, list) ->
            val sum = list.sumOf { it.amount }
            val pct = if (totalSpent > 0) (sum / totalSpent * 100).toInt() else 0
            CategorySpend(cat, sum, pct)
        }.sortedByDescending { it.amount }

        val moodCounts = diaries.groupingBy { it.mood }.eachCount()

        BaseStats(
            totalTasks = tasks.size,
            completedTasks = completed,
            taskCompletionRate = rate,
            habits = habits,
            habitLogs = logs,
            totalSpending = totalSpent,
            categorySpends = categorySpends,
            moodDistribution = moodCounts
        )
    }

    val uiState: StateFlow<InsightsUiState> = combine(
        baseStatsFlow,
        _aiInsightResponse,
        _isAILoading
    ) { base: BaseStats, aiResp: AIResponse?, loading: Boolean ->
        InsightsUiState(
            totalTasks = base.totalTasks,
            completedTasks = base.completedTasks,
            taskCompletionRate = base.taskCompletionRate,
            habits = base.habits,
            habitLogs = base.habitLogs,
            totalSpending = base.totalSpending,
            categorySpends = base.categorySpends,
            moodDistribution = base.moodDistribution,
            aiInsightResponse = aiResp,
            isAILoading = loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InsightsUiState()
    )

    fun askLifeOSAI(question: String) {
        _isAILoading.value = true
        viewModelScope.launch {
            val resp = LifeOSAI.answerQuestion(question, AIPermissions(), repository)
            _aiInsightResponse.value = resp
            _isAILoading.value = false
        }
    }

    fun clearAIResponse() {
        _aiInsightResponse.value = null
    }
}
