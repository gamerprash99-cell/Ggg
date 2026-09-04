package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.PrimaryIndigo

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Notes : Screen("notes", "Notes")
    object Capture : Screen("capture", "Capture")
    object Insights : Screen("insights", "Insights")
    object Life : Screen("life", "Life")
    object NoteEditor : Screen("note_editor/{noteId}", "Note Editor") {
        fun createRoute(noteId: Long) = "note_editor/$noteId"
    }
    // Nested Life Hub Destinations
    object Timeline : Screen("life/timeline", "Timeline")
    object Calendar : Screen("life/calendar", "Calendar")
    object Diary : Screen("life/diary", "Diary")
    object Expenses : Screen("life/expenses", "Expenses")
    object Habits : Screen("life/habits", "Habits")
    object AIAssistant : Screen("life/ai", "AI Assistant")
    object Backup : Screen("life/backup", "Backup & Privacy")
}

data class NavItem(
    val screen: Screen,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String
)

@Composable
fun LifeOSBottomBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit,
    onOpenCapture: () -> Unit
) {
    val items = listOf(
        NavItem(Screen.Home, Icons.Filled.Home, Icons.Outlined.Home, "Home"),
        NavItem(Screen.Notes, Icons.Filled.Description, Icons.Outlined.Description, "Notes"),
        // Center + Capture item
        NavItem(Screen.Capture, Icons.Filled.Add, Icons.Filled.Add, "+ Capture"),
        NavItem(Screen.Insights, Icons.Filled.Analytics, Icons.Outlined.Analytics, "Insights"),
        NavItem(Screen.Life, Icons.Filled.Hub, Icons.Outlined.Hub, "Life")
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                if (item.screen == Screen.Capture) {
                    // Elevated Vibrant center Capture button
                    Box(
                        modifier = Modifier
                            .offset(y = (-6).dp)
                            .size(50.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onOpenCapture
                            )
                            .testTag("nav_capture_fab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Quick Capture",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                } else {
                    val isSelected = currentRoute == item.screen.route
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onNavigate(item.screen) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("nav_item_${item.label.lowercase()}"),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                )
                                .padding(horizontal = 14.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Text(
                            text = item.label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
