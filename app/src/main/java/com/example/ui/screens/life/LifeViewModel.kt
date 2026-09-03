package com.example.ui.screens.life

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.LifeOSApp
import com.example.ai.LifeOSAI
import com.example.data.local.entity.DiaryEntity
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.HabitLogEntity
import com.example.data.local.entity.LifeEventEntity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LifeUiState(
    val selectedTab: Int = 0, // 0: Timeline, 1: Calendar, 2: Diary, 3: Expenses, 4: Habits, 5: AI Assistant, 6: Backup & Settings
    val timelineEvents: List<LifeEventEntity> = emptyList(),
    val selectedCalendarDate: String = "",
    val calendarEvents: List<LifeEventEntity> = emptyList(),
    val diaries: List<DiaryEntity> = emptyList(),
    val expenses: List<ExpenseEntity> = emptyList(),
    val habits: List<HabitEntity> = emptyList(),
    val habitLogs: List<HabitLogEntity> = emptyList(),
    val aiPermissions: AIPermissions = AIPermissions(),
    val aiChatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            sender = "LifeOS AI",
            message = "Hello! I am your central LifeOS AI. I run 100% locally and privately on your device. Ask me about your tasks, habits, expenses, notes, or diary.",
            isUser = false
        )
    ),
    val isAILoading: Boolean = false,
    val backupJsonString: String? = null,
    val backupMessage: String? = null,
    val isPinLockEnabled: Boolean = false,
    val currentPin: String = "1234"
)

data class ChatMessage(
    val sender: String,
    val message: String,
    val isUser: Boolean,
    val cards: List<com.example.ai.AICard> = emptyList(),
    val timestamp: String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
)

private data class TabAndEvents(
    val tab: Int,
    val events: List<LifeEventEntity>,
    val calDate: String,
    val calEvents: List<LifeEventEntity>
)

private data class LifeModules(
    val diaries: List<DiaryEntity>,
    val expenses: List<ExpenseEntity>,
    val habits: List<HabitEntity>,
    val habitLogs: List<HabitLogEntity>
)

class LifeViewModel(
    private val repository: LifeOSRepository = LifeOSApp.repo
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(0)
    private val _selectedCalendarDate = MutableStateFlow(repository.getTodayDate())
    private val _aiPermissions = MutableStateFlow(AIPermissions())
    private val _aiChatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "LifeOS AI",
                message = "Hello! I am your central LifeOS AI. I run 100% locally and privately on your device. Ask me anything about your tasks, habits, spending, notes, or memories.",
                isUser = false
            )
        )
    )
    private val _isAILoading = MutableStateFlow(false)
    private val _backupJsonString = MutableStateFlow<String?>(null)
    private val _backupMessage = MutableStateFlow<String?>(null)
    private val _isPinLockEnabled = MutableStateFlow(false)

    private val tabAndEventsFlow = combine(
        _selectedTab,
        repository.getAllLifeEvents(),
        _selectedCalendarDate
    ) { tab, events, calDate ->
        val calEvents = events.filter { it.date == calDate }
        TabAndEvents(tab, events, calDate, calEvents)
    }

    private val modulesFlow = combine(
        repository.getAllDiaries(),
        repository.getAllExpenses(),
        repository.getAllHabits(),
        repository.getAllHabitLogs()
    ) { diaries, expenses, habits, habitLogs ->
        LifeModules(diaries, expenses, habits, habitLogs)
    }

    val uiState: StateFlow<LifeUiState> = combine(
        tabAndEventsFlow,
        modulesFlow,
        _backupMessage,
        _isPinLockEnabled,
        _isAILoading
    ) { tabEvents, modules, backupMsg, pinEnabled, loading ->
        LifeUiState(
            selectedTab = tabEvents.tab,
            timelineEvents = tabEvents.events,
            selectedCalendarDate = tabEvents.calDate,
            calendarEvents = tabEvents.calEvents,
            diaries = modules.diaries,
            expenses = modules.expenses,
            habits = modules.habits,
            habitLogs = modules.habitLogs,
            aiPermissions = _aiPermissions.value,
            aiChatMessages = _aiChatMessages.value,
            isAILoading = loading,
            backupJsonString = _backupJsonString.value,
            backupMessage = backupMsg,
            isPinLockEnabled = pinEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LifeUiState(selectedCalendarDate = repository.getTodayDate())
    )

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun selectCalendarDate(date: String) {
        _selectedCalendarDate.value = date
    }

    // --- DIARY ACTIONS ---
    fun saveDiary(title: String, content: String, mood: Mood, tags: String) {
        viewModelScope.launch {
            repository.saveDiary(title, content, mood, tags)
        }
    }

    fun refineDiaryWithAI(rawPoints: String, onRefined: (String, String, Mood, String) -> Unit) {
        val processed = LifeOSAI.refineDiaryFromPoints(rawPoints)
        onRefined(processed.suggestedTitle, processed.refinedContent, processed.suggestedMood, processed.suggestedTags)
    }

    // --- EXPENSES ACTIONS ---
    fun addExpense(amount: Double, category: ExpenseCategory, note: String, paymentMethod: String) {
        viewModelScope.launch {
            repository.addExpense(amount, category, note, paymentMethod)
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    // --- HABIT ACTIONS ---
    fun toggleHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.toggleHabitForDate(habit)
        }
    }

    fun addHabit(title: String, icon: String, category: String) {
        viewModelScope.launch {
            repository.addHabit(title, icon, category)
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    // --- CENTRAL AI ASSISTANT ---
    fun updateAIPermission(permissionType: String, isAllowed: Boolean) {
        val current = _aiPermissions.value
        _aiPermissions.value = when (permissionType) {
            "notes" -> current.copy(accessNotes = isAllowed)
            "tasks" -> current.copy(accessTasks = isAllowed)
            "habits" -> current.copy(accessHabits = isAllowed)
            "expenses" -> current.copy(accessExpenses = isAllowed)
            "diary" -> current.copy(accessDiary = isAllowed)
            "captures" -> current.copy(accessCaptures = isAllowed)
            else -> current
        }
    }

    fun sendAIMessage(question: String) {
        if (question.isBlank()) return
        val userMsg = ChatMessage(sender = "You", message = question, isUser = true)
        _aiChatMessages.value = _aiChatMessages.value + userMsg
        _isAILoading.value = true

        viewModelScope.launch {
            val response = LifeOSAI.answerQuestion(question, _aiPermissions.value, repository)
            val aiMsg = ChatMessage(
                sender = "LifeOS AI",
                message = response.answer,
                isUser = false,
                cards = response.cards
            )
            _aiChatMessages.value = _aiChatMessages.value + aiMsg
            _isAILoading.value = false
        }
    }

    // --- BACKUP & RESTORE ---
    fun generateBackup() {
        viewModelScope.launch {
            val json = repository.exportDataToJson()
            _backupJsonString.value = json
            _backupMessage.value = "Backup created! Contains ${json.length} bytes of encrypted/structured local data."
        }
    }

    fun restoreBackup(jsonStr: String) {
        viewModelScope.launch {
            val success = repository.importDataFromJson(jsonStr)
            _backupMessage.value = if (success) "Backup restored successfully!" else "Failed to restore backup format."
        }
    }

    fun togglePinLock() {
        _isPinLockEnabled.value = !_isPinLockEnabled.value
    }
}
