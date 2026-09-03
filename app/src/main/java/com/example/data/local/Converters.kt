package com.example.data.local

import androidx.room.TypeConverter
import com.example.domain.model.CaptureType
import com.example.domain.model.ExpenseCategory
import com.example.domain.model.LifeEventType
import com.example.domain.model.Mood
import com.example.domain.model.Priority

class Converters {
    @TypeConverter
    fun fromPriority(value: Priority): String = value.name

    @TypeConverter
    fun toPriority(value: String): Priority = try {
        Priority.valueOf(value)
    } catch (e: Exception) {
        Priority.MEDIUM
    }

    @TypeConverter
    fun fromMood(value: Mood?): String? = value?.name

    @TypeConverter
    fun toMood(value: String?): Mood? = if (value == null) null else try {
        Mood.valueOf(value)
    } catch (e: Exception) {
        Mood.NORMAL
    }

    @TypeConverter
    fun fromExpenseCategory(value: ExpenseCategory): String = value.name

    @TypeConverter
    fun toExpenseCategory(value: String): ExpenseCategory = try {
        ExpenseCategory.valueOf(value)
    } catch (e: Exception) {
        ExpenseCategory.OTHER
    }

    @TypeConverter
    fun fromCaptureType(value: CaptureType): String = value.name

    @TypeConverter
    fun toCaptureType(value: String): CaptureType = try {
        CaptureType.valueOf(value)
    } catch (e: Exception) {
        CaptureType.THOUGHT
    }

    @TypeConverter
    fun fromLifeEventType(value: LifeEventType): String = value.name

    @TypeConverter
    fun toLifeEventType(value: String): LifeEventType = try {
        LifeEventType.valueOf(value)
    } catch (e: Exception) {
        LifeEventType.NOTE_CREATED
    }
}
