package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.CaptureType
import com.example.domain.model.ExpenseCategory
import com.example.domain.model.LifeEventType
import com.example.domain.model.Mood
import com.example.domain.model.Priority

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val folder: String = "General",
    val tags: String = "", // Comma-separated
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: String = "", // YYYY-MM-DD
    val dueTime: String = "",
    val priority: Priority = Priority.MEDIUM,
    val category: String = "Personal",
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val isRecurring: Boolean = false,
    val repeatPattern: String = "None",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val icon: String = "🔥",
    val category: String = "Health",
    val frequency: String = "Daily",
    val targetDaysPerWeek: Int = 7,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habit_logs")
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val date: String, // YYYY-MM-DD
    val isCompleted: Boolean = true,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val category: ExpenseCategory = ExpenseCategory.OTHER,
    val date: String, // YYYY-MM-DD
    val time: String = "",
    val paymentMethod: String = "UPI",
    val note: String = "",
    val tags: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "diaries")
data class DiaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val mood: Mood = Mood.NORMAL,
    val date: String, // YYYY-MM-DD
    val tags: String = "",
    val mediaUri: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "captures")
data class CaptureEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: CaptureType = CaptureType.THOUGHT,
    val title: String,
    val note: String = "",
    val mediaUri: String = "",
    val mood: Mood? = null,
    val tags: String = "",
    val date: String, // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "life_events")
data class LifeEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val date: String, // YYYY-MM-DD
    val type: LifeEventType,
    val title: String,
    val description: String = "",
    val sourceId: Long = 0,
    val tags: String = "",
    val mood: Mood? = null
)
