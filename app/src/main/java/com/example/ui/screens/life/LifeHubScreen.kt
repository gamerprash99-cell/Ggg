package com.example.ui.screens.life

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.DiaryEntity
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.HabitEntity
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
    val state by viewModel.uiState.collectAsStateWithLifecycle()

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
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
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
                    text = "Offline First 🛡️",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
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
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
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
                0 -> TimelineTabContent(events = state.timelineEvents)
                1 -> CalendarTabContent(
                    selectedDate = state.selectedCalendarDate,
                    events = state.calendarEvents,
                    onSelectDate = { viewModel.selectCalendarDate(it) }
                )
                2 -> DiaryTabContent(
                    diaries = state.diaries,
                    onSaveDiary = { title, content, mood, tags ->
                        viewModel.saveDiary(title, content, mood, tags)
                    },
                    onRefineWithAI = { raw, callback ->
                        viewModel.refineDiaryWithAI(raw, callback)
                    }
                )
                3 -> ExpensesTabContent(
                    expenses = state.expenses,
                    onAddExpense = { amt, cat, note, method ->
                        viewModel.addExpense(amt, cat, note, method)
                    },
                    onDelete = { viewModel.deleteExpense(it) }
                )
                4 -> HabitsTabContent(
                    habits = state.habits,
                    habitLogs = state.habitLogs,
                    onToggle = { viewModel.toggleHabit(it) },
                    onAdd = { title, icon, cat -> viewModel.addHabit(title, icon, cat) }
                )
                5 -> AIAssistantTabContent(
                    messages = state.aiChatMessages,
                    permissions = state.aiPermissions,
                    isLoading = state.isAILoading,
                    onSendMessage = { viewModel.sendAIMessage(it) },
                    onTogglePermission = { type, allowed -> viewModel.updateAIPermission(type, allowed) }
                )
                6 -> BackupPrivacyTabContent(
                    backupJson = state.backupJsonString,
                    backupMessage = state.backupMessage,
                    isPinEnabled = state.isPinLockEnabled,
                    onGenerateBackup = { viewModel.generateBackup() },
                    onRestore = { viewModel.restoreBackup(it) },
                    onTogglePin = { viewModel.togglePinLock() }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 1. TIMELINE TAB
// -------------------------------------------------------------
@Composable
fun TimelineTabContent(events: List<LifeEventEntity>) {
    if (events.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No timeline events yet. Add notes, habits, or expenses!", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "A continuous chronological record of your notes, completed tasks, habits, expenses, and diary memories.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            items(events) { event ->
                LifeTimelineCard(event = event)
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun LifeTimelineCard(event: LifeEventEntity) {
    val (icon, color) = when (event.type) {
        LifeEventType.NOTE_CREATED -> Pair("📝", AccentCyan)
        LifeEventType.TASK_COMPLETED -> Pair("✅", AccentEmerald)
        LifeEventType.HABIT_COMPLETED -> Pair("🔥", Color(0xFFF97316))
        LifeEventType.EXPENSE_RECORDED -> Pair("💸", AccentPink)
        LifeEventType.DIARY_CREATED -> Pair("📔", AccentViolet)
        LifeEventType.CAPTURE_SAVED -> Pair("📸", PrimaryIndigo)
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
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
    onSelectDate: (String) -> Unit
) {
    // Generate dates for the current week / month
    val cal = Calendar.getInstance()
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val dateNumFormat = SimpleDateFormat("d", Locale.getDefault())

    val days = remember {
        val list = mutableListOf<Triple<String, String, String>>()
        cal.add(Calendar.DAY_OF_MONTH, -3)
        for (i in 0..14) {
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
// 3. DIARY TAB (with AI Diary transformation)
// -------------------------------------------------------------
@Composable
fun DiaryTabContent(
    diaries: List<DiaryEntity>,
    onSaveDiary: (String, String, Mood, String) -> Unit,
    onRefineWithAI: (String, (String, String, Mood, String) -> Unit) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf(Mood.GREAT) }
    var tags by remember { mutableStateOf("#daily") }
    var showAIHelper by remember { mutableStateOf(false) }
    var rawPoints by remember { mutableStateOf("") }

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
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PrimaryIndigo.copy(alpha = 0.2f)),
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

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (content.isNotBlank() || title.isNotBlank()) {
                                onSaveDiary(
                                    title.ifBlank { "Daily Reflection" },
                                    content,
                                    selectedMood,
                                    tags
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
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${diary.mood.emoji} ${diary.title}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(diary.date, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

    // AI Diary Assistant Dialog (Transforms simple points to rich reflection)
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
                        "Write simple bullets of what you did (e.g. 'went to college, met friends, finished project, felt relaxed'). LifeOS AI will transform it into an organized narrative with mood & tags!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = rawPoints,
                        onValueChange = { rawPoints = it },
                        placeholder = { Text("• went to college\n• met friends\n• finished project...") },
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
                TextButton(onClick = { showAIHelper = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// 4. EXPENSES TAB (Track in Seconds)
// -------------------------------------------------------------
@Composable
fun ExpensesTabContent(
    expenses: List<ExpenseEntity>,
    onAddExpense: (Double, ExpenseCategory, String, String) -> Unit,
    onDelete: (ExpenseEntity) -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf(ExpenseCategory.FOOD) }
    var note by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("UPI") }

    val quickAmounts = listOf(50.0, 100.0, 200.0, 500.0, 1000.0)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Log Expense (Under 3 Seconds ⚡)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Amount input
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        placeholder = { Text("Amount (₹)...", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("expense_amount_input")
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    // Quick amount chips
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
                    // Category selector
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
                        placeholder = { Text("Note (e.g. lunch with Rahul, grocery shopping)...") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val amt = amountStr.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                onAddExpense(amt, selectedCat, note, paymentMethod)
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
            SectionHeader(title = "EXPENSE LOG (${expenses.size})")
        }

        items(expenses) { exp ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                        IconButton(onClick = { onDelete(exp) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// -------------------------------------------------------------
// 5. HABITS TAB
// -------------------------------------------------------------
@Composable
fun HabitsTabContent(
    habits: List<HabitEntity>,
    habitLogs: List<com.example.data.local.entity.HabitLogEntity>,
    onToggle: (HabitEntity) -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var newTitle by remember { mutableStateOf("") }
    var newIcon by remember { mutableStateOf("🔥") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Add New Habit 🔥", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newTitle.isNotBlank()) {
                                    onAdd(newTitle, newIcon, "Daily")
                                    newTitle = ""
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
            HabitItemCard(
                habit = habit,
                isDone = isDoneToday,
                onToggle = { onToggle(habit) }
            )
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
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
    onTogglePermission: (String, Boolean) -> Unit
) {
    var prompt by remember { mutableStateOf("") }
    var showPermissions by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // AI Permissions Toggle Bar
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
                Text(if (showPermissions) "Hide" else "Edit", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
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

        Spacer(modifier = Modifier.height(10.dp))

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
                placeholder = { Text("Ask LifeOS AI anything about your life...") },
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
    onTogglePin: () -> Unit
) {
    var restoreInput by remember { mutableStateOf("") }
    var showRestoreDialog by remember { mutableStateOf(false) }

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
                }
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Local Backup & Restore 💾", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Export a structured encrypted JSON backup of your notes, tasks, habits, expenses, and diary so you never lose your data.",
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
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.weight(1f).testTag("restore_backup_button")
                        ) {
                            Text("Restore Data", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    if (backupMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(backupMessage, color = AccentEmerald, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

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
