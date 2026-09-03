package com.example.domain.model

enum class Priority {
    HIGH, MEDIUM, LOW
}

enum class Mood(val emoji: String, val label: String) {
    GREAT("😄", "Great"),
    HAPPY("😊", "Happy"),
    CALM("😌", "Calm"),
    NORMAL("😐", "Normal"),
    SAD("😔", "Sad"),
    ANGRY("😤", "Angry"),
    STRESSED("😰", "Stressed"),
    LOVED("❤️", "Loved"),
    EXCITED("🔥", "Excited")
}

enum class CaptureType(val iconName: String) {
    PHOTO("Photo"),
    VIDEO("Video"),
    AUDIO("Audio"),
    THOUGHT("Thought")
}

enum class ExpenseCategory(val icon: String, val displayName: String) {
    FOOD("🍔", "Food"),
    CAFE("☕", "Cafe"),
    SHOPPING("🛍️", "Shopping"),
    TRAVEL("🚕", "Travel"),
    ENTERTAINMENT("🎮", "Entertainment"),
    EDUCATION("📚", "Education"),
    BILLS("🏠", "Bills"),
    HEALTH("💊", "Health"),
    SUBSCRIPTIONS("📱", "Subscriptions"),
    OTHER("❤️", "Other")
}

enum class LifeEventType {
    NOTE_CREATED,
    TASK_COMPLETED,
    HABIT_COMPLETED,
    EXPENSE_RECORDED,
    DIARY_CREATED,
    CAPTURE_SAVED
}

data class AIPermissions(
    val accessNotes: Boolean = true,
    val accessTasks: Boolean = true,
    val accessHabits: Boolean = true,
    val accessExpenses: Boolean = true,
    val accessDiary: Boolean = true,
    val accessCaptures: Boolean = true
)
