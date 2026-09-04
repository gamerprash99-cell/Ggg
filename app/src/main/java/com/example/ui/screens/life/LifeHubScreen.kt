package com.example.ui.screens.life

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.DiaryEntity
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.HabitLogEntity
import com.example.data.local.entity.LifeEventEntity
import com.example.domain.model.ExpenseCategory
import com.example.domain.model.LifeEventType
import com.example.domain.model.Mood
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.screens.home.HabitItemCard
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentPink
import com.example.ui.theme.AccentViolet
import com.example.ui.theme.PrimaryIndigo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeHubScreen(
    viewModel: LifeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initPinLock(context)
    }

    val tabs = listOf(
        "⏱️ Timeline",
        "📅 Calendar",
        "📔 Diary",
        "💸 Expenses",
        "🔥 Habits",
        "🤖 AI Assistant",
        "⚙️ Backup & Privacy"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "LIFE HUB",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Personal Operating Center",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "OFFLINE FIRST 🛡️",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Scrollable Tab Row
        ScrollableTabRow(
            selectedTabIndex = state.selectedTab,
            edgePadding = 16.dp,
            containerColor = Color.Transparent,
            divider = {}
        ) {
            tabs.forEachIndexed { index, tabTitle ->
                val isSelected = state.selectedTab == index
                Tab(
                    selected = isSelected,
                    onClick = { viewModel.selectTab(index) },
                    text = {
                        Text(
                            text = tabTitle,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            softWrap = false,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Visible
                        )
                    },
                    modifier = Modifier.testTag("life_tab_$index")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Content per selected tab
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when (state.selectedTab) {
                0 -> TimelineTabContent(
                    events = state.timelineEvents,
                    onDeleteEvent = { viewModel.deleteTimelineEvent(it) }
                )
                1 -> CalendarTabContent(
                    selectedDate = state.selectedCalendarDate,
                    events = state.calendarEvents,
                    onSelectDate = { viewModel.selectCalendarDate(it) },
                    onDeleteEvent = { viewModel.deleteTimelineEvent(it) }
                )
                2 -> DiaryTabContent(
                    diaries = state.diaries,
                    onSaveDiary = { title, content, mood, tags, date ->
                        viewModel.saveDiary(title, content, mood, tags, date)
                    },
                    onUpdateDiary = { viewModel.updateDiary(it) },
                    onDeleteDiary = { viewModel.deleteDiary(it) },
                    onRefineWithAI = { raw, callback ->
                        viewModel.refineDiaryWithAI(raw, callback)
                    }
                )
                3 -> ExpensesTabContent(
                    expenses = state.expenses,
                    onAddExpense = { amt, cat, note, method, date ->
                        viewModel.addExpense(amt, cat, note, method, date)
                    },
                    onUpdateExpense = { viewModel.updateExpense(it) },
                    onDelete = { viewModel.deleteExpense(it) }
                )
                4 -> HabitsTabContent(
                    habits = state.habits,
                    habitLogs = state.habitLogs,
                    onToggle = { viewModel.toggleHabit(it) },
                    onAdd = { title, icon, cat, freq, targetDays, remTime, remDays ->
                        viewModel.addHabit(context, title, icon, cat, freq, targetDays, remTime, remDays)
                    },
                    onUpdate = { viewModel.updateHabit(context, it) },
                    onDelete = { viewModel.deleteHabit(context, it) }
                )
                5 -> AIAssistantTabContent(
                    messages = state.aiChatMessages,
                    permissions = state.aiPermissions,
                    isLoading = state.isAILoading,
                    onSendMessage = { viewModel.sendAIMessage(it) },
                    onTogglePermission = { type, allowed -> viewModel.updateAIPermission(type, allowed) },
                    onClearChat = { viewModel.clearAIChat() }
                )
                6 -> BackupPrivacyTabContent(
                    backupJson = state.backupJsonString,
                    backupMessage = state.backupMessage,
                    isPinEnabled = state.isPinLockEnabled,
                    onGenerateBackup = { viewModel.generateBackup() },
                    onRestore = { viewModel.restoreBackup(it) },
                    onTogglePin = { viewModel.togglePinLock(context) },
                    onSetPin = { pin -> viewModel.setPin(context, pin) }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 1. TIMELINE TAB
// -------------------------------------------------------------
@Composable
fun TimelineTabContent(
    events: List<LifeEventEntity>,
    onDeleteEvent: (LifeEventEntity) -> Unit
) {
    var selectedFilter by remember { mutableStateOf<LifeEventType?>(null) }
    var viewingEvent by remember { mutableStateOf<LifeEventEntity?>(null) }

    val filterOptions = listOf(
        Pair(null, "All"),
        Pair(LifeEventType.NOTE_CREATED, "📝 Notes"),
        Pair(LifeEventType.TASK_COMPLETED, "✅ Tasks"),
        Pair(LifeEventType.HABIT_COMPLETED, "🔥 Habits"),
        Pair(LifeEventType.EXPENSE_RECORDED, "💸 Expenses"),
        Pair(LifeEventType.DIARY_CREATED, "📔 Diary"),
        Pair(LifeEventType.CAPTURE_SAVED, "📸 Captures")
    )

    val filteredEvents = remember(events, selectedFilter) {
        if (selectedFilter == null) events else events.filter { it.type == selectedFilter }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Chronological record of your notes, habits, expenses, and diary memories.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(filterOptions) { (type, label) ->
                    val isSelected = selectedFilter == type
                    Surface(
                        color = if (isSelected) PrimaryIndigo.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.outlineVariant
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.clickable { selectedFilter = type }
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        if (filteredEvents.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                    Text("No timeline events found for this filter.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(filteredEvents) { event ->
                LifeTimelineCard(
                    event = event,
                    onClick = { viewingEvent = event }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // Detail / Delete Dialog
    if (viewingEvent != null) {
        val ev = viewingEvent!!
        AlertDialog(
            onDismissRequest = { viewingEvent = null },
            title = { Text(ev.title, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column {
                    Text(
                        text = "Date: ${ev.date} • Type: ${ev.type.name.lowercase().replaceFirstChar { it.uppercase() }}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (ev.mood != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Mood: ${ev.mood.emoji} ${ev.mood.label}", fontSize = 12.sp)
                    }
                    if (ev.tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Tags: ${ev.tags}", fontSize = 12.sp, color = AccentCyan)
                    }
                    if (ev.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = ev.description, fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewingEvent = null }) {
                    Text("Close")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDeleteEvent(ev)
                        viewingEvent = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = AccentPink)
                ) {
                    Text("Delete Event")
                }
            }
        )
    }
}

@Composable
fun LifeTimelineCard(
    event: LifeEventEntity,
    onClick: (() -> Unit)? = null
) {
    val (icon, color) = when (event.type) {
        LifeEventType.NOTE_CREATED -> Pair("📝", AccentCyan)
        LifeEventType.TASK_COMPLETED -> Pair("✅", AccentEmerald)
        LifeEventType.HABIT_COMPLETED -> Pair("🔥", Color(0xFFF97316))
        LifeEventType.EXPENSE_RECORDED -> Pair("💸", AccentPink)
        LifeEventType.DIARY_CREATED -> Pair("📔", AccentViolet)
        LifeEventType.CAPTURE_SAVED -> Pair("📸", PrimaryIndigo)
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick ?: {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = event.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = event.date,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (event.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = event.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. CALENDAR TAB
// -------------------------------------------------------------
@Composable
fun CalendarTabContent(
    selectedDate: String,
    events: List<LifeEventEntity>,
    onSelectDate: (String) -> Unit,
    onDeleteEvent: (LifeEventEntity) -> Unit
) {
    val cal = Calendar.getInstance()
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val dateNumFormat = SimpleDateFormat("d", Locale.getDefault())

    val days = remember {
        val list = mutableListOf<Triple<String, String, String>>()
        cal.add(Calendar.DAY_OF_MONTH, -7)
        for (i in 0..21) {
            val d = cal.time
            list.add(Triple(format.format(d), dayFormat.format(d), dateNumFormat.format(d)))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        list
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Select a day to view everything that happened on that date:",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Day Selector Carousel
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(days) { (fullDate, dayName, dayNum) ->
                    val isSelected = fullDate == selectedDate
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { onSelectDate(fullDate) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = dayName.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dayNum,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }

        item {
            SectionHeader(title = "ACTIVITY ON $selectedDate")
        }

        if (events.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No activities logged on $selectedDate.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(events) { event ->
                LifeTimelineCard(event = event)
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// -------------------------------------------------------------
// 3. DIARY TAB
// -------------------------------------------------------------
@Composable
fun DiaryTabContent(
    diaries: List<DiaryEntity>,
    onSaveDiary: (String, String, Mood, String, String) -> Unit,
    onUpdateDiary: (DiaryEntity) -> Unit,
    onDeleteDiary: (DiaryEntity) -> Unit,
    onRefineWithAI: (String, (String, String, Mood, String) -> Unit) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf(Mood.GREAT) }
    var tags by remember { mutableStateOf("#daily") }
    var showAIHelper by remember { mutableStateOf(false) }
    var rawPoints by remember { mutableStateOf("") }

    var editingDiary by remember { mutableStateOf<DiaryEntity?>(null) }
    var deletingDiary by remember { mutableStateOf<DiaryEntity?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // New Entry Box
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Write Today's Diary 📔", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Button(
                            onClick = { showAIHelper = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("✨ AI Helper", color = PrimaryIndigo, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Title (e.g. A Meaningful Day)...") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("diary_title_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        placeholder = { Text("What happened today? How did you feel? What are you grateful for?...") },
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("diary_content_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    // Mood Selector
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(Mood.values()) { mood ->
                            val isSelected = selectedMood == mood
                            Surface(
                                color = if (isSelected) PrimaryIndigo.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.clickable { selectedMood = mood }
                            ) {
                                Text(
                                    text = "${mood.emoji} ${mood.label}",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        placeholder = { Text("Tags (e.g. #daily, #reflection)...") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (content.isNotBlank() || title.isNotBlank()) {
                                onSaveDiary(
                                    title.ifBlank { "Daily Reflection" },
                                    content,
                                    selectedMood,
                                    tags,
                                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                )
                                title = ""
                                content = ""
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("save_diary_button")
                    ) {
                        Text("Save Reflection to Life Timeline")
                    }
                }
            }
        }

        item {
            SectionHeader(title = "PAST REFLECTIONS (${diaries.size})")
        }

        items(diaries) { diary ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${diary.mood.emoji} ${diary.title}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(diary.date, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            IconButton(onClick = { editingDiary = diary }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp), tint = PrimaryIndigo)
                            }
                            IconButton(onClick = { deletingDiary = diary }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp), tint = AccentPink)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(diary.content, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (diary.tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(diary.tags, fontSize = 11.sp, color = AccentCyan)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // AI Helper Dialog
    if (showAIHelper) {
        AlertDialog(
            onDismissRequest = { showAIHelper = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = PrimaryIndigo)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Diary Assistant")
                }
            },
            text = {
                Column {
                    Text(
                        "Write simple bullets of what you did. LifeOS AI will transform it into an organized narrative with mood & tags!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = rawPoints,
                        onValueChange = { rawPoints = it },
                        placeholder = { Text("• went to gym\n• finished project\n• relaxed in evening...") },
                        maxLines = 4,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRefineWithAI(rawPoints) { t, c, m, tg ->
                            title = t
                            content = c
                            selectedMood = m
                            tags = tg
                            showAIHelper = false
                        }
                    }
                ) {
                    Text("Transform with AI ✨")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAIHelper = false }) { Text("Cancel") }
            }
        )
    }

    // Edit Diary Dialog
    if (editingDiary != null) {
        val d = editingDiary!!
        var editTitle by remember(d) { mutableStateOf(d.title) }
        var editContent by remember(d) { mutableStateOf(d.content) }
        var editMood by remember(d) { mutableStateOf(d.mood) }
        var editTags by remember(d) { mutableStateOf(d.tags) }

        AlertDialog(
            onDismissRequest = { editingDiary = null },
            title = { Text("Edit Reflection") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editContent,
                        onValueChange = { editContent = it },
                        label = { Text("Content") },
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editTags,
                        onValueChange = { editTags = it },
                        label = { Text("Tags") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateDiary(
                            d.copy(
                                title = editTitle,
                                content = editContent,
                                mood = editMood,
                                tags = editTags
                            )
                        )
                        editingDiary = null
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingDiary = null }) { Text("Cancel") }
            }
        )
    }

    // Delete Diary Dialog
    if (deletingDiary != null) {
        AlertDialog(
            onDismissRequest = { deletingDiary = null },
            title = { Text("Delete Reflection?") },
            text = { Text("Are you sure you want to delete '${deletingDiary!!.title}'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteDiary(deletingDiary!!)
                        deletingDiary = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPink)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingDiary = null }) { Text("Cancel") }
            }
        )
    }
}

// -------------------------------------------------------------
// 4. EXPENSES TAB
// -------------------------------------------------------------
@Composable
fun ExpensesTabContent(
    expenses: List<ExpenseEntity>,
    onAddExpense: (Double, ExpenseCategory, String, String, String) -> Unit,
    onUpdateExpense: (ExpenseEntity) -> Unit,
    onDelete: (ExpenseEntity) -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf(ExpenseCategory.FOOD) }
    var note by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("UPI") }

    var editingExpense by remember { mutableStateOf<ExpenseEntity?>(null) }
    var deletingExpense by remember { mutableStateOf<ExpenseEntity?>(null) }

    val quickAmounts = listOf(50.0, 100.0, 200.0, 500.0, 1000.0)
    val totalSpending = remember(expenses) { expenses.sumOf { it.amount } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Log Expense (Under 3 Seconds ⚡)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        placeholder = { Text("Amount (₹)...", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("expense_amount_input")
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(quickAmounts) { amt ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { amountStr = amt.toInt().toString() }
                            ) {
                                Text("₹${amt.toInt()}", fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Category:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(ExpenseCategory.values()) { cat ->
                            val isSelected = selectedCat == cat
                            Surface(
                                color = if (isSelected) PrimaryIndigo.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.clickable { selectedCat = cat }
                            ) {
                                Text(
                                    text = "${cat.icon} ${cat.displayName}",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = { Text("Note (e.g. lunch with friends, grocery)...") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val amt = amountStr.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                onAddExpense(
                                    amt,
                                    selectedCat,
                                    note,
                                    paymentMethod,
                                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                )
                                amountStr = ""
                                note = ""
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("add_expense_button")
                    ) {
                        Text("Add Expense")
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(title = "EXPENSE LOG (${expenses.size})")
                Text(
                    text = "Total: ₹${String.format(Locale.getDefault(), "%.0f", totalSpending)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = AccentPink
                )
            }
        }

        items(expenses) { exp ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(exp.category.icon, fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (exp.note.isNotEmpty()) exp.note else exp.category.displayName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${exp.date} • ${exp.paymentMethod}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "₹${String.format(Locale.getDefault(), "%.0f", exp.amount)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = AccentPink
                        )
                        IconButton(onClick = { editingExpense = exp }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryIndigo, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = { deletingExpense = exp }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // Edit Expense Dialog
    if (editingExpense != null) {
        val exp = editingExpense!!
        var editAmt by remember(exp) { mutableStateOf(exp.amount.toString()) }
        var editNote by remember(exp) { mutableStateOf(exp.note) }
        var editCat by remember(exp) { mutableStateOf(exp.category) }

        AlertDialog(
            onDismissRequest = { editingExpense = null },
            title = { Text("Edit Expense") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editAmt,
                        onValueChange = { editAmt = it },
                        label = { Text("Amount (₹)") },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editNote,
                        onValueChange = { editNote = it },
                        label = { Text("Note") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = editAmt.toDoubleOrNull() ?: exp.amount
                        onUpdateExpense(exp.copy(amount = parsed, note = editNote, category = editCat))
                        editingExpense = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingExpense = null }) { Text("Cancel") }
            }
        )
    }

    // Delete Expense Dialog
    if (deletingExpense != null) {
        AlertDialog(
            onDismissRequest = { deletingExpense = null },
            title = { Text("Delete Expense?") },
            text = { Text("Are you sure you want to delete this ₹${deletingExpense!!.amount} expense?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(deletingExpense!!)
                        deletingExpense = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPink)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingExpense = null }) { Text("Cancel") }
            }
        )
    }
}

// -------------------------------------------------------------
// 5. HABITS TAB
// -------------------------------------------------------------
@Composable
fun HabitsTabContent(
    habits: List<HabitEntity>,
    habitLogs: List<HabitLogEntity>,
    onToggle: (HabitEntity) -> Unit,
    onAdd: (String, String, String, String, Int, String, String) -> Unit,
    onUpdate: (HabitEntity) -> Unit,
    onDelete: (HabitEntity) -> Unit
) {
    val context = LocalContext.current
    var newTitle by remember { mutableStateOf("") }
    var newIcon by remember { mutableStateOf("🔥") }
    var newReminder by remember { mutableStateOf("") }
    val availableIcons = listOf("🔥", "📚", "💧", "🧘", "🏃", "💻", "🥗", "💤", "🌿")

    var editingHabit by remember { mutableStateOf<HabitEntity?>(null) }
    var deletingHabit by remember { mutableStateOf<HabitEntity?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Add New Habit 🔥", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(availableIcons) { icon ->
                            val isSelected = newIcon == icon
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) PrimaryIndigo.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .border(1.dp, if (isSelected) PrimaryIndigo else Color.Transparent, CircleShape)
                                    .clickable { newIcon = icon },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(icon, fontSize = 18.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            placeholder = { Text("Habit name (e.g. Read 20 pages)...") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("habit_title_input")
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is androidx.compose.foundation.interaction.PressInteraction.Release) {
                                val calendar = java.util.Calendar.getInstance()
                                android.app.TimePickerDialog(
                                    context,
                                    { _, hourOfDay, minute ->
                                        newReminder = String.format("%02d:%02d", hourOfDay, minute)
                                    },
                                    calendar.get(java.util.Calendar.HOUR_OF_DAY),
                                    calendar.get(java.util.Calendar.MINUTE),
                                    false
                                ).show()
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newReminder,
                            onValueChange = { },
                            readOnly = true,
                            placeholder = { Text("Set Time (e.g. 08:00)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            interactionSource = interactionSource
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newTitle.isNotBlank()) {
                                    onAdd(newTitle.trim(), newIcon, "Daily", "Daily", 7, newReminder.trim(), "Every day")
                                    newTitle = ""
                                    newReminder = ""
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("create_habit_button")
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(title = "ACTIVE HABITS (${habits.size})")
        }

        items(habits) { habit ->
            val isDoneToday = habitLogs.any { it.habitId == habit.id }
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onToggle(habit) },
                borderColor = if (isDoneToday) AccentEmerald.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(text = habit.icon, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = habit.title,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Streak: 🔥 ${habit.currentStreak} ${if (habit.currentStreak == 1) "day" else "days"}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { editingHabit = habit }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryIndigo, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = { deletingHabit = habit }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AccentPink, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isDoneToday) AccentEmerald else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { onToggle(habit) }
                                .testTag("habit_toggle_${habit.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDoneToday) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // Edit Habit Dialog
    if (editingHabit != null) {
        val h = editingHabit!!
        var editTitle by remember(h) { mutableStateOf(h.title) }
        var editIcon by remember(h) { mutableStateOf(h.icon) }
        var editReminder by remember(h) { mutableStateOf(h.reminderTime) }

        AlertDialog(
            onDismissRequest = { editingHabit = null },
            title = { Text("Edit Habit") },
            text = {
                Column {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(availableIcons) { icon ->
                            val isSelected = editIcon == icon
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) PrimaryIndigo.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .border(1.dp, if (isSelected) PrimaryIndigo else Color.Transparent, CircleShape)
                                    .clickable { editIcon = icon },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(icon, fontSize = 18.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Habit Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is androidx.compose.foundation.interaction.PressInteraction.Release) {
                                val calendar = java.util.Calendar.getInstance()
                                android.app.TimePickerDialog(
                                    context,
                                    { _, hourOfDay, minute ->
                                        editReminder = String.format("%02d:%02d", hourOfDay, minute)
                                    },
                                    calendar.get(java.util.Calendar.HOUR_OF_DAY),
                                    calendar.get(java.util.Calendar.MINUTE),
                                    false
                                ).show()
                            }
                        }
                    }
                    OutlinedTextField(
                        value = editReminder,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Reminder HH:MM") },
                        placeholder = { Text("e.g. 09:00") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        interactionSource = interactionSource
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdate(h.copy(title = editTitle, icon = editIcon, reminderTime = editReminder.trim()))
                        editingHabit = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingHabit = null }) { Text("Cancel") }
            }
        )
    }

    // Delete Habit Dialog
    if (deletingHabit != null) {
        AlertDialog(
            onDismissRequest = { deletingHabit = null },
            title = { Text("Delete Habit?") },
            text = { Text("Are you sure you want to delete '${deletingHabit!!.title}' and its logs?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(deletingHabit!!)
                        deletingHabit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPink)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingHabit = null }) { Text("Cancel") }
            }
        )
    }
}

// -------------------------------------------------------------
// 6. CENTRAL LIFEOS AI ASSISTANT TAB
// -------------------------------------------------------------
@Composable
fun AIAssistantTabContent(
    messages: List<ChatMessage>,
    permissions: com.example.domain.model.AIPermissions,
    isLoading: Boolean,
    onSendMessage: (String) -> Unit,
    onTogglePermission: (String, Boolean) -> Unit,
    onClearChat: () -> Unit
) {
    var prompt by remember { mutableStateOf("") }
    var showPermissions by remember { mutableStateOf(false) }

    val suggestedQuestions = listOf(
        "What are my priorities today?",
        "How much did I spend this week?",
        "Which habits need attention?",
        "Summarize my recent thoughts"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().clickable { showPermissions = !showPermissions }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = PrimaryIndigo, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI Context Permissions (Privacy Controls)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onClearChat) {
                        Text("Clear", fontSize = 11.sp, color = AccentPink)
                    }
                    Text(if (showPermissions) "Hide" else "Edit", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        AnimatedVisibility(visible = showPermissions) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text("Select which local data LifeOS AI can inspect:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = permissions.accessNotes, onCheckedChange = { onTogglePermission("notes", it) })
                        Text("Notes", fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = permissions.accessTasks, onCheckedChange = { onTogglePermission("tasks", it) })
                        Text("Tasks", fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = permissions.accessHabits, onCheckedChange = { onTogglePermission("habits", it) })
                        Text("Habits", fontSize = 12.sp)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = permissions.accessExpenses, onCheckedChange = { onTogglePermission("expenses", it) })
                        Text("Expenses", fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = permissions.accessDiary, onCheckedChange = { onTogglePermission("diary", it) })
                        Text("Diary", fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = permissions.accessCaptures, onCheckedChange = { onTogglePermission("captures", it) })
                        Text("Captures", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Suggested prompts
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(suggestedQuestions) { q ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.clickable { onSendMessage(q) }
                ) {
                    Text(
                        text = q,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chat messages list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        color = if (msg.isUser) PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.widthIn(max = 300.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = msg.sender,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (msg.isUser) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.message,
                                fontSize = 13.sp,
                                color = if (msg.isUser) Color.White else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = PrimaryIndigo)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("LifeOS AI thinking locally...", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Message input bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                placeholder = { Text("Ask LifeOS AI anything...") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f).testTag("ai_chat_input")
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (prompt.isNotBlank()) {
                        onSendMessage(prompt)
                        prompt = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PrimaryIndigo)
                    .testTag("ai_chat_send_button")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

// -------------------------------------------------------------
// 7. BACKUP & PRIVACY TAB
// -------------------------------------------------------------
@Composable
fun BackupPrivacyTabContent(
    backupJson: String?,
    backupMessage: String?,
    isPinEnabled: Boolean,
    onGenerateBackup: () -> Unit,
    onRestore: (String) -> Unit,
    onTogglePin: () -> Unit,
    onSetPin: (String) -> Unit
) {
    val context = LocalContext.current
    var restoreInput by remember { mutableStateOf("") }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var newPinInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Privacy & Security 🛡️", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• 100% Offline-First Architecture\n• Zero Analytics / Telemetry\n• No Account or Cloud Logins\n• Your data stays on your device at all times.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(if (isPinEnabled) Icons.Default.Lock else Icons.Default.LockOpen, contentDescription = "Lock", tint = PrimaryIndigo)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("App Lock Protection", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Require PIN to open LifeOS", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = isPinEnabled,
                            onCheckedChange = { onTogglePin() },
                            modifier = Modifier.testTag("app_lock_switch")
                        )
                    }

                    if (isPinEnabled) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { showChangePinDialog = true },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "PIN", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Change 4-Digit PIN", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Local Backup & Restore 💾", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Export a structured JSON backup of your notes, tasks, habits, expenses, and diary so you never lose your data.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onGenerateBackup,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("generate_backup_button")
                        ) {
                            Text("Create Backup")
                        }
                        Button(
                            onClick = { showRestoreDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.weight(1f).testTag("restore_backup_button")
                        ) {
                            Text("Restore Data", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    if (backupMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(backupMessage, color = AccentEmerald, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }

                    if (!backupJson.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("LifeOS Backup", backupJson)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Backup JSON copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Backup JSON to Clipboard")
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // Change PIN Dialog
    if (showChangePinDialog) {
        AlertDialog(
            onDismissRequest = { showChangePinDialog = false },
            title = { Text("Set 4-Digit Security PIN") },
            text = {
                Column {
                    Text("Enter a new 4-digit PIN for protecting your second brain:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { if (it.length <= 4) newPinInput = it.filter { c -> c.isDigit() } },
                        placeholder = { Text("e.g. 1234") },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPinInput.length == 4) {
                            onSetPin(newPinInput)
                            showChangePinDialog = false
                            newPinInput = ""
                            Toast.makeText(context, "New PIN saved!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "PIN must be exactly 4 digits", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePinDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Restore Dialog
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Restore from Backup JSON") },
            text = {
                OutlinedTextField(
                    value = restoreInput,
                    onValueChange = { restoreInput = it },
                    placeholder = { Text("Paste JSON content here...") },
                    maxLines = 6,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRestore(restoreInput)
                        showRestoreDialog = false
                    }
                ) {
                    Text("Confirm Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) { Text("Cancel") }
            }
        )
    }
}
