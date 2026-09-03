# LifeOS — Offline Architecture, Privacy & Security

This document details the security posture, privacy guarantees, data storage locations, and offline-first boundaries of LifeOS.

---

## 1. Offline-First Architectural Guarantees

LifeOS is engineered to be **100% functional without an active network connection**.

| Module / Action | Offline Guarantee | Network Dependency |
| :--- | :--- | :--- |
| **Note Taking & Editing** | ✅ 100% Offline | None |
| **Task Management** | ✅ 100% Offline | None |
| **Habit Tracking & Streaks**| ✅ 100% Offline | None |
| **Expense & Finance Tracking**| ✅ 100% Offline | None |
| **Diary & Mood Journaling** | ✅ 100% Offline | None |
| **Quick Capture** | ✅ 100% Offline | None |
| **Life Timeline & Events** | ✅ 100% Offline | None |
| **Global Database Search** | ✅ 100% Offline | None |
| **AI Task Extraction** | ✅ 100% Offline | None |
| **AI Note Summarization** | ✅ 100% Offline | None |
| **AI Personal Life Queries** | ✅ 100% Offline | None |
| **Database JSON Backup** | ✅ 100% Offline | None |
| **General Knowledge AI** | ❌ Requires Internet | Requires network connection & valid Gemini API key |

---

## 2. Data Storage & File Locations

All persistent application data resides exclusively within Android's private app sandbox:
- **Sandbox Root:** `/data/data/com.aistudio.lifeos.kztuvq/`
- **Room Database Files:**
  - Primary Database: `/data/data/com.aistudio.lifeos.kztuvq/databases/lifeos_database`
  - Write-Ahead Log (WAL): `/data/data/com.aistudio.lifeos.kztuvq/databases/lifeos_database-wal`
  - Shared Memory File: `/data/data/com.aistudio.lifeos.kztuvq/databases/lifeos_database-shm`
- **Security Boundaries:**
  - Android Linux UID separation blocks all other applications on the device from accessing or inspecting these files without root access.
  - Zero cloud synchronization servers or background data upload daemons are bundled in the application.

---

## 3. Privacy & Zero-Telemetry Audit

- **Third-Party Analytics:** None. No Google Analytics, Firebase Analytics, Mixpanel, Amplitude, or Datadog SDKs are active.
- **Advertising SDKs:** None. No Google AdMob, Unity Ads, or Meta Audience Network SDKs exist.
- **Crash Reporting:** No third-party crash telemetry (e.g., Sentry, Bugsnag, Crashlytics) transmitting stack traces or user logs to external endpoints.
- **Data Collection:** Zero personal identity information (PII), device IDs, IMEI numbers, or location coordinates are collected or transmitted.

---

## 4. Android Manifest Permissions Audit

Inspecting `app/src/main/AndroidManifest.xml`:

| Declared Permission | Purpose in Codebase | Security & Privacy Assessment |
| :--- | :--- | :--- |
| `android.permission.INTERNET` | Optional cloud Gemini fallback (`LifeOSAI.kt`) | Required if users configure Gemini API. Does not auto-transmit background data. |
| `android.permission.CAMERA` | Declared for future photo capture in Quick Capture | Not actively bound to CameraX in the current build; zero background camera access. |
| `android.permission.RECORD_AUDIO`| Declared for future voice journaling | Not actively bound to AudioRecord in the current build; zero background microphone access. |
| `android.permission.POST_NOTIFICATIONS`| Declared for task/habit reminder notifications | Standard Android 13+ permission for local notifications. |

---

## 5. App PIN Lock & Data Protection

- **Implementation:** `AppLockDialog.kt` provides a 4-digit PIN verification modal.
- **Privacy Setting:** Located in `LifeHubScreen.kt` under Tab 6 ("⚙️ Backup & Privacy").
- **Current State:** The PIN toggle state is managed in `LifeViewModel`. In a future release, this should be hardened using Android Keystore and `EncryptedSharedPreferences` for biometric fingerprint authentication.

---

## 6. Backup Security

- **Exported Format:** JSON document generated in `LifeOSRepository.exportDataToJson()`.
- **Content:** Full table dumps of notes, tasks, habits, expenses, and diary entries.
- **Security Recommendation:** Users should store exported backup strings in password managers or encrypted storage drives, as the exported JSON is in plaintext format.
