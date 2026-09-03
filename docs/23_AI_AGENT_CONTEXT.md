# LifeOS — AI Coding Agent Context & Rules

> [!CRITICAL]
> **Instructions for Future AI Coding Agents (Gemini, Claude, GPT, Cursor, Copilot):**
> Read this document completely before modifying any code in the LifeOS repository.

---

## 1. Core Platform & Invariant Rules

1. **Rule 1 (Metadata Synchronization):**
   - The application name in `metadata.json` (`"name": "LifeOS"`) MUST always match the string resource in `app/src/main/res/values/strings.xml` (`<string name="app_name">LifeOS</string>`).
   - You **MUST NOT** remove `"MAJOR_CAPABILITY_SERVER_SIDE_GEMINI_API"` from `majorCapabilities` in `metadata.json`.

2. **Rule 2 (No Local Properties):**
   - Never create, edit, or reference `local.properties`. Secrets are injected via the Secrets Gradle Plugin from `.env` or `.env.example` into `BuildConfig`.

3. **Rule 3 (Environment Constraints):**
   - There is **NO Android Emulator or ADB** available in this execution environment. Do not attempt to run instrumented tests (`androidTest/`) or `adb` commands.
   - Use `compile_applet` for build verification, or JVM unit tests via `./gradlew testDebugUnitTest`.

4. **Rule 4 (Read Before Editing):**
   - Always call `view_file` on target lines immediately prior to editing. Never assume template defaults.

---

## 2. Architectural Conventions

1. **Single Source of Truth:**
   - All mutations pass through `LifeOSRepository.kt`. Do not call Room DAOs directly from Composables.
2. **ViewModel Pattern:**
   - ViewModels expose UI state using `StateFlow` created via `stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = ...)`.
   - Composables collect state via `collectAsStateWithLifecycle()`.
3. **Database Guardrails:**
   - Database name: `"lifeos_database"`. Version: `1`.
   - `exportSchema = false`, `fallbackToDestructiveMigration(false)`.
   - If adding a new table, register the entity in `AppDatabase.kt` and create a corresponding DAO.
4. **Theming & Tokens:**
   - Primary: `VibrantPrimary` (`#6750A4`).
   - Scaffold Background: `VibrantBgLight` (`#F7F2FA`).
   - Surface Containers: `VibrantSurfaceLight` (`#FEF7FF`).
   - Borders: `VibrantOutlineLight` (`#CAC4D0`).
   - Never hardcode arbitrary hex strings in Composables.

---

## 3. Verified Dependency Catalog Quick Reference

- **AGP:** `9.1.1`
- **Kotlin:** `2.2.10`
- **Compose BOM:** `2024.09.00`
- **KSP:** `2.3.5`
- **Room:** `2.7.0`
- **Roborazzi:** `1.59.0`
- **Navigation Compose:** `2.8.9`
- **Target / Compile SDK:** `36`
- **Min SDK:** `24`
- **Java Bytecode Target:** `11` (`JavaVersion.VERSION_11`)
- **Gradle JVM Runtime:** `17`
