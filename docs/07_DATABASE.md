# LifeOS — Database Architecture & Schema Reference

This document provides a complete technical specification of the Room SQLite database engine powering LifeOS.

---

## 1. Database Configuration

- **Database Class:** `com.example.data.local.AppDatabase`
- **Database File Name:** `"lifeos_database"`
- **Room Engine Version:** `2.7.0` (with KSP `2.3.5`)
- **Database Schema Version:** `1`
- **Schema Export:** `exportSchema = false`
- **Migration Strategy:** `.fallbackToDestructiveMigration(false)`
  - *Architectural Safety:* Setting destructive migration to `false` prevents Room from silently dropping tables and wiping user data if the version number is incremented without an explicit migration script.

---

## 2. Relational Entity Tables (8 Entities)

### 2.1 Table: `notes` (`NoteEntity`)
Stores rich personal notes, lecture records, and ideas.
```sql
CREATE TABLE IF NOT EXISTS notes (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    folder TEXT NOT NULL DEFAULT 'General',
    tags TEXT NOT NULL DEFAULT '',
    isPinned INTEGER NOT NULL DEFAULT 0,
    isFavorite INTEGER NOT NULL DEFAULT 0,
    isArchived INTEGER NOT NULL DEFAULT 0,
    createdAt INTEGER NOT NULL,
    updatedAt INTEGER NOT NULL
);
```

### 2.2 Table: `tasks` (`TaskEntity`)
Stores to-dos, deadlines, and goal milestones.
```sql
CREATE TABLE IF NOT EXISTS tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title TEXT NOT NULL,
    description TEXT NOT NULL DEFAULT '',
    isCompleted INTEGER NOT NULL DEFAULT 0,
    priority TEXT NOT NULL, -- Enum: LOW, MEDIUM, HIGH, URGENT
    dueDate TEXT,           -- Format: YYYY-MM-DD
    dueTime TEXT,           -- Format: HH:mm
    category TEXT NOT NULL DEFAULT 'General',
    createdAt INTEGER NOT NULL,
    completedAt INTEGER
);
```

### 2.3 Table: `habits` (`HabitEntity`)
Stores daily and recurring habits to track.
```sql
CREATE TABLE IF NOT EXISTS habits (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title TEXT NOT NULL,
    icon TEXT NOT NULL DEFAULT '⚡',
    category TEXT NOT NULL DEFAULT 'General',
    targetFrequencyDaysPerWeek INTEGER NOT NULL DEFAULT 7,
    currentStreak INTEGER NOT NULL DEFAULT 0,
    bestStreak INTEGER NOT NULL DEFAULT 0,
    createdAt INTEGER NOT NULL
);
```

### 2.4 Table: `habit_logs` (`HabitLogEntity`)
Stores discrete completion records for habits per date.
```sql
CREATE TABLE IF NOT EXISTS habit_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    habitId INTEGER NOT NULL,
    date TEXT NOT NULL,     -- Format: YYYY-MM-DD
    isCompleted INTEGER NOT NULL DEFAULT 1,
    notes TEXT NOT NULL DEFAULT ''
);
```

### 2.5 Table: `expenses` (`ExpenseEntity`)
Stores financial transactions, costs, and payment methods.
```sql
CREATE TABLE IF NOT EXISTS expenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    amount REAL NOT NULL,
    category TEXT NOT NULL, -- Enum: FOOD, CAFE, SHOPPING, BILLS, TRANSPORT, HEALTH, ENTERTAINMENT, EDUCATION, OTHER
    note TEXT NOT NULL DEFAULT '',
    date TEXT NOT NULL,     -- Format: YYYY-MM-DD
    paymentMethod TEXT NOT NULL DEFAULT 'UPI',
    createdAt INTEGER NOT NULL
);
```

### 2.6 Table: `diaries` (`DiaryEntity`)
Stores daily journal entries, reflections, and emotional states.
```sql
CREATE TABLE IF NOT EXISTS diaries (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    mood TEXT NOT NULL,     -- Enum: GREAT, HAPPY, CALM, NORMAL, SAD, ANGRY, STRESSED, LOVED, EXCITED
    date TEXT NOT NULL,     -- Format: YYYY-MM-DD
    tags TEXT NOT NULL DEFAULT '',
    createdAt INTEGER NOT NULL
);
```

### 2.7 Table: `captures` (`CaptureEntity`)
Stores instant thoughts, notes, and media pointers recorded via Quick Capture.
```sql
CREATE TABLE IF NOT EXISTS captures (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    type TEXT NOT NULL,     -- Enum: THOUGHT, PHOTO, VIDEO, AUDIO
    title TEXT NOT NULL,
    note TEXT NOT NULL DEFAULT '',
    mood TEXT,              -- Optional Mood Enum
    tags TEXT NOT NULL DEFAULT '',
    mediaUri TEXT,
    createdAt INTEGER NOT NULL
);
```

### 2.8 Table: `life_events` (`LifeEventEntity`)
Stores the unified chronological narrative of the user's life.
```sql
CREATE TABLE IF NOT EXISTS life_events (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    type TEXT NOT NULL,     -- Enum: NOTE_CREATED, TASK_COMPLETED, HABIT_COMPLETED, EXPENSE_LOGGED, DIARY_WRITTEN, CAPTURE_SAVED, MILESTONE
    title TEXT NOT NULL,
    description TEXT NOT NULL DEFAULT '',
    date TEXT NOT NULL,     -- Format: YYYY-MM-DD
    time TEXT NOT NULL,     -- Format: HH:mm
    timestamp INTEGER NOT NULL,
    category TEXT NOT NULL DEFAULT 'Life',
    referenceId INTEGER
);
```

---

## 3. Type Converters (`Converters.kt`)

Room requires TypeConverters to map Kotlin enums to SQLite string primitives:
1. `PriorityConverter`: Converts `Priority` (LOW, MEDIUM, HIGH, URGENT) ↔ `String`.
2. `MoodConverter`: Converts `Mood` (GREAT, HAPPY, CALM, NORMAL, SAD, ANGRY, STRESSED, LOVED, EXCITED) ↔ `String`.
3. `ExpenseCategoryConverter`: Converts `ExpenseCategory` (FOOD, CAFE, SHOPPING, etc.) ↔ `String`.
4. `CaptureTypeConverter`: Converts `CaptureType` (THOUGHT, PHOTO, VIDEO, AUDIO) ↔ `String`.
5. `LifeEventTypeConverter`: Converts `LifeEventType` (NOTE_CREATED, TASK_COMPLETED, etc.) ↔ `String`.

---

## 4. Initial Seed Data Logic

To prevent first-time users from landing on a barren, intimidating UI, `LifeOSRepository.checkAndSeedInitialData()` is invoked on first application launch from `LifeOSApp.onCreate()`:
- **Seed Check:** Checks if `noteDao.getCount() == 0`.
- **Pre-populated Entities:**
  - **Notes:** "Welcome to LifeOS — Your Unified Second Brain" (Folder: Ideas, Pinned: true) and "Deep Work Guidelines" (Folder: College).
  - **Tasks:** "Review LifeOS Quick Capture & Navigation" (Priority: HIGH), "Drink 2.5L Water Daily" (Priority: MEDIUM), "Setup monthly personal budget" (Priority: MEDIUM).
  - **Habits:** "Read 15 Pages" (Icon: 📖, Category: Growth), "Morning Workout" (Icon: 🏋️, Category: Health), "Deep Meditation" (Icon: 🧘, Category: Mindfulness).
  - **Habit Logs:** Today's completion records to demonstrate active streaks.
  - **Expenses:** Sample entry of ₹180 (Category: CAFE, Note: "Morning Matcha Latte").
  - **Diaries:** "A New Beginning" (Mood: EXCITED, Date: today).
  - **Timeline:** Seed events for welcome note, habit creation, and expense logging.
