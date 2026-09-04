package com.example.ui.screens.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.TaskEntity
import com.example.domain.model.Priority
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientBannerCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentPink
import com.example.ui.theme.PrimaryIndigo
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToNotes: () -> Unit,
    onNavigateToCapture: () -> Unit,
    onNavigateToLife: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResult by viewModel.searchResult.collectAsStateWithLifecycle()

    var quickTaskTitle by remember { mutableStateOf("") }
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var deletingTask by remember { mutableStateOf<TaskEntity?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Vibrant Palette Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${state.dayOfWeek}, ${state.dateString}".uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "LifeOS",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable { onNavigateToCapture() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚡", fontSize = 18.sp)
                    }
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD0BCFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "OS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF21005D)
                        )
                    }
                }
            }
        }

        // Quick Action Pills Row (Vibrant Palette style)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    onClick = onNavigateToNotes,
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD0BCFF)),
                    modifier = Modifier.testTag("quick_action_add_task")
                ) {
                    Text(
                        text = "+ Task",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                Surface(
                    onClick = onNavigateToLife,
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.testTag("quick_action_add_expense")
                ) {
                    Text(
                        text = "+ Expense",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                Surface(
                    onClick = onNavigateToCapture,
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.testTag("quick_action_add_entry")
                ) {
                    Text(
                        text = "+ Entry",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Universal Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search notes, diary, tasks, spending...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearSearch() }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_search_field")
            )
        }

        // Global Search Results View if Searching
        if (searchResult != null) {
            val res = searchResult!!
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Search Results (${res.totalCount} found)",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (res.totalCount == 0) {
                            Text(
                                text = "No matching items found across your second brain.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            if (res.notes.isNotEmpty()) {
                                Text(text = "Notes (${res.notes.size}):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                res.notes.take(3).forEach { Text("• ${it.title}", fontSize = 12.sp) }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                            if (res.tasks.isNotEmpty()) {
                                Text(text = "Tasks (${res.tasks.size}):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                res.tasks.take(3).forEach { Text("• ${it.title}", fontSize = 12.sp) }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                            if (res.expenses.isNotEmpty()) {
                                Text(text = "Expenses (${res.expenses.size}):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                res.expenses.take(3).forEach { Text("• ₹${it.amount} on ${it.category.displayName}", fontSize = 12.sp) }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                            if (res.diaries.isNotEmpty()) {
                                Text(text = "Diary (${res.diaries.size}):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                res.diaries.take(2).forEach { Text("• ${it.title} (${it.date})", fontSize = 12.sp) }
                            }
                        }
                    }
                }
            }
        }

        // Vibrant Daily Progress Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Daily Progress",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${state.todayHabitLogs.size}/${state.habits.size} Habits completed",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${state.overallProgressPercentage}%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    LinearProgressIndicator(
                        progress = { (state.overallProgressPercentage / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 3 Metric Boxes (Vibrant Palette grid pattern)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Tasks",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${state.tasks.count { it.isCompleted }}/${state.tasks.size}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Expenses",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "₹${String.format(Locale.getDefault(), "%.0f", state.totalTodaySpending)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Habits",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${state.todayHabitLogs.size}/${state.habits.size}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }
        }

        // Dynamic Morning vs Evening Focus Banner
        item {
            if (state.isEvening) {
                GradientBannerCard(
                    title = "Time for Reflection 📔",
                    subtitle = "Capture your evening thoughts, review today's spending, and log your mood.",
                    tag = "EVENING REVIEW",
                    actionText = "Open Life Diary",
                    onActionClick = onNavigateToLife
                )
            } else {
                GradientBannerCard(
                    title = "Win Your Morning ⚡",
                    subtitle = "Complete your high priority tasks and check off your healthy habits early.",
                    tag = "DAILY FOCUS",
                    actionText = "Quick Capture Moment",
                    onActionClick = onNavigateToCapture
                )
            }
        }

        // Today's Habits Section
        item {
            SectionHeader(
                title = "TODAY'S HABITS",
                actionText = "Manage Habits",
                onActionClick = onNavigateToLife
            )
        }

        item {
            if (state.habits.isEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No habits tracked yet. Add your first habit in Life Hub!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.habits.forEach { habit ->
                        val isDone = state.todayHabitLogs.any { it.habitId == habit.id }
                        HabitItemCard(
                            habit = habit,
                            isDone = isDone,
                            onToggle = { viewModel.toggleHabit(habit) }
                        )
                    }
                }
            }
        }

        // Today's Tasks Section
        item {
            SectionHeader(
                title = "TODAY'S TASKS",
                actionText = "All Notes & Tasks",
                onActionClick = onNavigateToNotes
            )
        }

        // Quick Add Task Input
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = quickTaskTitle,
                    onValueChange = { quickTaskTitle = it },
                    placeholder = { Text("Add a quick task...") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_task_input")
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (quickTaskTitle.isNotBlank()) {
                            viewModel.quickAddTask(quickTaskTitle)
                            quickTaskTitle = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .testTag("quick_task_add_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Task",
                        tint = Color.White
                    )
                }
            }
        }

        // Task Items
        if (state.tasks.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No tasks for today. Tap '+' above to add one!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(state.tasks) { task ->
                TaskItemCard(
                    task = task,
                    onToggle = { viewModel.toggleTask(task) },
                    onEdit = { editingTask = task },
                    onDelete = { deletingTask = task }
                )
            }
        }

        // Today's Spending Overview
        item {
            SectionHeader(
                title = "TODAY'S SPENDING",
                actionText = "Expense Log",
                onActionClick = onNavigateToLife
            )
        }

        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToLife
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "₹${String.format(Locale.getDefault(), "%.0f", state.totalTodaySpending)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${state.todayExpenses.size} transactions recorded today",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        color = AccentEmerald.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Track in Seconds ⚡",
                            color = AccentEmerald,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Latest Memory / Capture Section
        item {
            SectionHeader(
                title = "LATEST MEMORY",
                actionText = "+ Capture",
                onActionClick = onNavigateToCapture
            )
        }

        item {
            val latest = state.latestCapture
            if (latest != null) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToLife
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${latest.type.name} • ${latest.title}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = latest.date,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (latest.note.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = latest.note,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToCapture
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📸 No memories captured today. Tap to capture a photo, audio, or thought!",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Edit Task Dialog
    if (editingTask != null) {
        val t = editingTask!!
        var editTitle by remember(t) { mutableStateOf(t.title) }
        var editDesc by remember(t) { mutableStateOf(t.description) }
        var editPriority by remember(t) { mutableStateOf(t.priority) }
        var editReminder by remember(t) { mutableStateOf(t.reminderTime) }

        AlertDialog(
            onDismissRequest = { editingTask = null },
            title = { Text("Edit Task") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Task Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editDesc,
                        onValueChange = { editDesc = it },
                        label = { Text("Notes / Details") },
                        maxLines = 3,
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
                        label = { Text("Reminder Time (HH:MM)") },
                        placeholder = { Text("e.g. 09:30 or 14:00") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        interactionSource = interactionSource
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Priority:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Priority.values().forEach { prio ->
                            val isSel = editPriority == prio
                            Surface(
                                color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { editPriority = prio }
                            ) {
                                Text(
                                    text = prio.name,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editTitle.isNotBlank()) {
                            viewModel.updateTask(
                                context,
                                t.copy(
                                    title = editTitle.trim(),
                                    description = editDesc.trim(),
                                    priority = editPriority,
                                    reminderTime = editReminder.trim()
                                )
                            )
                            editingTask = null
                        }
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingTask = null }) { Text("Cancel") }
            }
        )
    }

    // Delete Task Dialog
    if (deletingTask != null) {
        AlertDialog(
            onDismissRequest = { deletingTask = null },
            title = { Text("Delete Task?") },
            text = { Text("Are you sure you want to delete '${deletingTask!!.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTask(context, deletingTask!!)
                        deletingTask = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPink)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingTask = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun HabitItemCard(
    habit: HabitEntity,
    isDone: Boolean,
    onToggle: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onToggle,
        borderColor = if (isDone) AccentEmerald.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDone) AccentEmerald else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .testTag("habit_toggle_${habit.id}"),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
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

@Composable
fun TaskItemCard(
    task: TaskEntity,
    onToggle: () -> Unit,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("task_checkbox_${task.id}")
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onBackground
                )
                if (task.description.isNotEmpty()) {
                    Text(
                        text = task.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (task.priority == Priority.HIGH) {
                Surface(
                    color = AccentPink.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "High",
                        color = AccentPink,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            if (onEdit != null) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Task", tint = PrimaryIndigo, modifier = Modifier.size(16.dp))
                }
            }
            if (onDelete != null) {
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
