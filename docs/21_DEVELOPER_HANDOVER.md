# LifeOS — Developer Handover & Onboarding Guide

Welcome to the LifeOS engineering team. This guide will get you productive on day one.

---

## 1. Day-One Developer Checklist

- [ ] **Clone the Repository:** Clone the codebase to your local workstation.
- [ ] **Install Prerequisites:**
  - Android Studio Ladybug (2024.2.1+) or Koala (2024.1.1+)
  - JDK 17 (Zulu or JetBrains Runtime)
  - Android SDK Platform 36 (Android 16)
- [ ] **Open Project:** Open the project root in Android Studio. Allow Gradle to perform its initial sync.
- [ ] **Verify Build via CLI:**
  ```bash
  ./gradlew assembleDebug
  ```
  Ensure the build completes successfully and produces an APK at `app/build/outputs/apk/debug/app-debug.apk`.
- [ ] **Review Key Architectural Documents:** Read `05_ARCHITECTURE.md`, `06_DATA_FLOW.md`, and `07_DATABASE.md`.

---

## 2. Five Golden Architectural Rules

When contributing to LifeOS, you must strictly follow these five rules:

1. **Rule 1: Never Call Room DAOs Directly from Composables**
   - Composables must only observe `StateFlow` exposed by ViewModels and dispatch user actions to ViewModel functions.
2. **Rule 2: LifeOSRepository is the Single Source of Truth**
   - All database mutations and cross-module side effects (e.g., auto-creating timeline events when tasks are marked complete) belong in `LifeOSRepository.kt`.
3. **Rule 3: Respect the Vibrant Palette Design System**
   - Never hardcode arbitrary hex colors in Composables. Always use `MaterialTheme.colorScheme` or tokens from `ui/theme/Color.kt`.
4. **Rule 4: Offline-First Always**
   - Every core feature must work seamlessly with zero internet connection. Never introduce a mandatory network call for core productivity features.
5. **Rule 5: No Secrets in Source Code or `local.properties`**
   - API keys are managed exclusively via the Secrets Gradle Plugin through `.env` and `.env.example` files and injected into `BuildConfig`.

---

## 3. How-To: Adding a New Feature or Entity

Follow this exact six-step pattern when adding a new capability to LifeOS:

### Step 1: Define the Entity
Open `app/src/main/java/com/example/data/local/entity/Entities.kt`:
```kotlin
@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val time: Long
)
```

### Step 2: Create the DAO Interface
Open `app/src/main/java/com/example/data/local/dao/Daos.kt`:
```kotlin
@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY time ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long
}
```

### Step 3: Register in `AppDatabase.kt`
Add the entity class to the `@Database` annotation and declare the abstract DAO getter:
```kotlin
@Database(
    entities = [..., ReminderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
}
```

### Step 4: Expose in `LifeOSRepository.kt`
Wire the DAO queries and mutations into the central repository:
```kotlin
fun getAllReminders(): Flow<List<ReminderEntity>> = database.reminderDao().getAllReminders()

suspend fun addReminder(title: String, time: Long) {
    database.reminderDao().insertReminder(ReminderEntity(title = title, time = time))
}
```

### Step 5: Expose in the ViewModel
Merge the flow into the ViewModel's `StateFlow` via `combine()` or `stateIn()`.

### Step 6: Build the Compose UI
Render the state using Material 3 components (`GlassCard`, `Text`, `Button`) adhering to the 8.dp grid spacing system.

---

## 4. Running Tests & Quality Verification

To verify that your changes have not broken existing functionality:
```bash
# Run local JVM unit tests
./gradlew testDebugUnitTest

# Assemble APK
./gradlew assembleDebug
```
*Note on Known Test Issue:* Remember that `ExampleRobolectricTest.kt` needs an updated string assertion to match `"LifeOS"` (see `19_KNOWN_ISSUES_TECHNICAL_DEBT.md`).
