# LifeOS — Known Issues, Defects & Technical Debt

This document tracks all identified technical debt, legacy template residue, test failures, and functional gaps, prioritized by severity.

---

## 1. Severity Definitions

- **P0 (Critical / Blocker):** Fails builds, causes test suite failures, or breaks core application functionality.
- **P1 (High):** Missing hardware integrations, security hardening gaps, or incomplete primary user journeys.
- **P2 (Medium):** Architectural debt, missing automated migration tests, or unencrypted export formats.
- **P3 (Low / Polish):** Code hygiene, commented-out template residue, or minor cosmetic inconsistencies.

---

## 2. Prioritized Issue Log

### P0 Issues (Immediate Attention Required)

#### Issue 1: Legacy Robolectric Test String Assertion Mismatch
- **Location:** `app/src/test/java/com/example/ExampleRobolectricTest.kt` (line 19)
- **Description:** The test asserts that `R.string.app_name` equals `"My Application"`. However, the app name was updated to `"LifeOS"` in `res/values/strings.xml`. Running `./gradlew testDebugUnitTest` will fail this test with an `AssertionError`.
- **Root Cause:** Legacy test artifact from project creation template was not synchronized during renaming.
- **Fix:** Update assertion to `assertEquals("LifeOS", appName)`.

#### Issue 2: Legacy Screenshot Test References Non-Existent Composables
- **Location:** `app/src/test/java/com/example/GreetingScreenshotTest.kt`
- **Description:** Imports `com.example.ui.theme.MyApplicationTheme` and renders `Greeting("Robolectric")`. Both symbols were deleted when the app was refactored to `LifeOSTheme`.
- **Root Cause:** Template screenshot test was left behind after UI overhaul.
- **Fix:** Update to use `LifeOSTheme` and capture a live UI component such as `GlassCard` or `LifeOSBottomBar`.

---

### P1 Issues (High Priority)

#### Issue 3: Hardware Media Capture Not Wired to Camera/Microphone
- **Location:** `app/src/main/java/com/example/ui/screens/capture/CaptureSheet.kt`
- **Description:** The Quick Capture sheet allows users to select `PHOTO`, `VIDEO`, and `AUDIO` capture types, and stores a `mediaUri` string in Room. However, tapping the type does not currently launch the Android Photo Picker (`ActivityResultContracts.PickVisualMedia`), CameraX, or the audio recorder.
- **Impact:** Users can log text thoughts with media tags, but cannot yet attach actual media files directly from the sheet.

#### Issue 4: PIN Lock Stored in Transient ViewModel State
- **Location:** `app/src/main/java/com/example/ui/screens/life/LifeViewModel.kt`
- **Description:** The PIN lock toggle (`isPinLockEnabled`) and PIN string (`"1234"`) are held in memory within `LifeViewModel`. If the application process is killed by the OS, the lock state resets to false.
- **Remediation:** Persist the lock state and hash using `EncryptedSharedPreferences` or AndroidX DataStore with Android Keystore encryption.

---

### P2 Issues (Medium Priority)

#### Issue 5: Plaintext JSON Backup Export
- **Location:** `app/src/main/java/com/example/data/repository/LifeOSRepository.kt` (`exportDataToJson`)
- **Description:** Backups are serialized to unencrypted JSON. If a user copies this text to an insecure location, sensitive diary and financial records could be exposed.
- **Remediation:** Add an optional AES-256 password protection layer for exported backup strings.

#### Issue 6: Room Schema Migration Testing
- **Location:** `app/src/main/java/com/example/data/local/AppDatabase.kt`
- **Description:** Currently configured with `exportSchema = false` and `fallbackToDestructiveMigration(false)`. Before releasing Database Version 2, schema export must be enabled and an automated Room migration test should be added.

---

### P3 Issues (Low Priority & Polish)

#### Issue 7: Pre-commented Template Dependencies
- **Location:** `app/build.gradle.kts`
- **Description:** Several unused template dependencies (Firestore, Camera2, Firebase Auth, Accompanist Permissions) are commented out in the build file. While harmless to APK size, they should be cleaned up once feature requirements are finalized.
