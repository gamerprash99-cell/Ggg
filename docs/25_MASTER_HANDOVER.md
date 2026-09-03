# LifeOS — Master Handover & Knowledge Package

> **Document Classification:** Comprehensive Master Handover Package  
> **App:** LifeOS (`com.aistudio.lifeos.kztuvq`)  
> **Target Version:** 1.0 (Android SDK 36, Kotlin 2.2.10, Jetpack Compose M3)  
> **Date:** September 2026

---

## 1. Executive Summary

LifeOS is an offline-first, native Android personal operating system and second brain. It unifies five historically fragmented productivity and life management pillars—**Tasks & Goals**, **Notes & Knowledge**, **Habits & Routines**, **Personal Expenses**, and **Diary & Mood Memories**—into a single, high-speed mobile application powered by on-device AI intelligence.

### Key Metrics & Architectural Facts
- **Architecture:** Clean Architecture MVVM with Unidirectional Data Flow (UDF).
- **UI Framework:** Jetpack Compose with Material Design 3 and the custom **Vibrant Palette** theme system.
- **Local Persistence:** Room Database 2.7.0 backed by SQLite (`lifeos_database`, schema v1) containing 8 entity tables and reactive Flow DAOs.
- **Intelligence Engine:** Dual-mode AI (`LifeOSAI.kt`). 100% private on-device heuristic query parser, regex note task extractor, and summarizer, plus an optional user-keyed cloud Gemini REST fallback.
- **Privacy & Security:** Zero cloud telemetry, zero advertising SDKs, 100% offline core execution, and granular AI category permission switches (`AIPermissions`).
- **CI/CD:** Automated GitHub Actions pipeline (`.github/workflows/android.yml`) executing tests and building debug APKs.

---

## 2. Complete File Directory Structure

```
LifeOS/
├── .github/workflows/android.yml       # GitHub Actions CI/CD Pipeline
├── app/
│   ├── build.gradle.kts              # Application build script & dependencies
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml   # Permissions & application entry
│   │   │   ├── java/com/example/
│   │   │   │   ├── LifeOSApp.kt      # Application root, database singleton, data seeding
│   │   │   │   ├── MainActivity.kt   # Single activity, Scaffold, NavHost container
│   │   │   │   ├── ai/LifeOSAI.kt    # Dual-mode AI system
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── AppDatabase.kt (8 entities, Converters)
│   │   │   │   │   │   ├── Converters.kt
│   │   │   │   │   │   ├── dao/Daos.kt (8 Room DAOs)
│   │   │   │   │   │   └── entity/Entities.kt (8 Room Entities)
│   │   │   │   │   └── repository/LifeOSRepository.kt (Central Hub & JSON Backup)
│   │   │   │   ├── domain/model/Models.kt (Enums, AIPermissions)
│   │   │   │   └── ui/
│   │   │   │       ├── components/Components.kt (GlassCard, Banner, LockDialog)
│   │   │   │       ├── navigation/Navigation.kt (Screen routes, LifeOSBottomBar)
│   │   │   │       ├── screens/
│   │   │   │       │   ├── capture/CaptureSheet.kt (Sub-3s modal capture)
│   │   │   │       │   ├── home/ (HomeScreen.kt, HomeViewModel.kt)
│   │   │   │       │   ├── insights/ (InsightsScreen.kt, InsightsViewModel.kt)
│   │   │   │       │   ├── life/ (LifeHubScreen.kt, LifeViewModel.kt)
│   │   │   │       │   └── notes/ (NotesScreen.kt, NoteEditorScreen.kt, NotesViewModel.kt)
│   │   │   │       └── theme/ (Color.kt, Theme.kt, Type.kt)
│   │   │   └── res/ (strings.xml, colors.xml, mipmap launcher icons)
│   │   └── test/java/com/example/ (Local JVM unit & screenshot tests)
│   └── proguard-rules.pro
├── docs/ (25-File Complete Handover Suite)
├── gradle/libs.versions.toml (Version Catalog)
└── metadata.json
```

---

## 3. Pillar-by-Pillar Capability Matrix

| Pillar | Key Capabilities | Entities Involved | Storage & Offline Status |
| :--- | :--- | :--- | :--- |
| **1. Productivity (Home)** | Uppercase date greeting, overall daily progress circle, task checkboxes, inline quick add, habit checks, multi-table global search. | `TaskEntity`, `HabitEntity`, `HabitLogEntity`, `ExpenseEntity` | 100% Offline via Room SQLite |
| **2. Notes (Second Brain)** | Folder filtering (Ideas, College, Projects, etc.), search, pin/favorite, rich fullscreen editor, AI note summarization, AI task extraction. | `NoteEntity` | 100% Offline via Room SQLite |
| **3. Quick Capture** | Persistent elevated center button, sub-3-second modal sheet, thought/photo/video/audio categorization, mood tagger. | `CaptureEntity`, `LifeEventEntity` | 100% Offline via Room SQLite |
| **4. Insights & Analytics** | Task completion percentage, category expense distribution with ranked bars, diary mood distribution, direct AI query bar. | `TaskEntity`, `ExpenseEntity`, `DiaryEntity` | 100% Offline reactive Flow computations |
| **5. Life Hub** | 7-in-1 central hub: Chronological Timeline, Calendar date filter, Diary reflections, Expense ledger, Habit streaks, AI Chat, Backup & Privacy. | `LifeEventEntity`, `DiaryEntity`, `ExpenseEntity`, `HabitEntity` | 100% Offline via Room SQLite |
| **6. Intelligence (AI)** | Permission-gated conversational life engine, regex action item extractor from notes, thought-to-journal refinement, optional Gemini REST fallback. | All Entities via `LifeOSRepository` | Dual-mode: Local NLP engine runs 100% offline |
| **7. Backup & Privacy** | One-tap JSON export and import of all tables, in-app PIN lock interface, zero telemetry. | All Entities | 100% Offline serialization |

---

## 4. Key Architectural Invariants

1. **Dependency Direction:** Composables → ViewModels → `LifeOSRepository` → Room DAOs → SQLite. No Composable may call Room DAOs directly.
2. **Platform & Strings Invariant:** The name in `metadata.json` (`"LifeOS"`) must always equal `app_name` in `res/values/strings.xml`.
3. **No `local.properties` for Secrets:** Secrets are read by the Secrets Gradle Plugin from `.env` and injected into `BuildConfig`.
4. **Offline Sovereignty:** Core features must never be blocked by network latency, timeouts, or remote server downtimes.

---

## 5. Immediate Remediation Action Plan (Next Sprints)

1. **Sprint 1 (Test Fixes):**
   - In `app/src/test/java/com/example/ExampleRobolectricTest.kt` (line 19), change expected string from `"My Application"` to `"LifeOS"`.
   - In `app/src/test/java/com/example/GreetingScreenshotTest.kt`, replace references to legacy `MyApplicationTheme` with `LifeOSTheme`.
2. **Sprint 2 (Hardware Media & Capture):**
   - Wire `CaptureSheet.kt` to the Android Photo Picker (`ActivityResultContracts.PickVisualMedia`) and audio recorder.
3. **Sprint 3 (Security & Biometrics):**
   - Back the PIN lock with Android Keystore and `EncryptedSharedPreferences` for biometric fingerprint authentication.
4. **Sprint 4 (Production Release):**
   - Configure release keystore signing in GitHub Actions and publish to Google Play internal track.

---

## 6. Document Sign-Off

The LifeOS project is thoroughly documented, architecturally sound, and ready for immediate handover to developers, founders, QA engineers, and automated AI agents. Consult the individual documents in `/docs/` for specific deep dives.
