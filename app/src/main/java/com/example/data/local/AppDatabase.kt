package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.CaptureDao
import com.example.data.local.dao.DiaryDao
import com.example.data.local.dao.ExpenseDao
import com.example.data.local.dao.HabitDao
import com.example.data.local.dao.HabitLogDao
import com.example.data.local.dao.LifeEventDao
import com.example.data.local.dao.NoteDao
import com.example.data.local.dao.TaskDao
import com.example.data.local.entity.CaptureEntity
import com.example.data.local.entity.DiaryEntity
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.HabitLogEntity
import com.example.data.local.entity.LifeEventEntity
import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.TaskEntity

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        NoteEntity::class,
        TaskEntity::class,
        HabitEntity::class,
        HabitLogEntity::class,
        ExpenseEntity::class,
        DiaryEntity::class,
        CaptureEntity::class,
        LifeEventEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun taskDao(): TaskDao
    abstract fun habitDao(): HabitDao
    abstract fun habitLogDao(): HabitLogDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun diaryDao(): DiaryDao
    abstract fun captureDao(): CaptureDao
    abstract fun lifeEventDao(): LifeEventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE tasks ADD COLUMN reminderTime TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE habits ADD COLUMN reminderTime TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE habits ADD COLUMN reminderDays TEXT NOT NULL DEFAULT 'Every day'")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lifeos_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
