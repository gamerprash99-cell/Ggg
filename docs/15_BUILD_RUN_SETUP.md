# LifeOS — Build, Run & Development Setup Guide

This guide details how to clone, configure, build, and run LifeOS on a developer workstation or CI environment.

---

## 1. System Requirements

- **Operating System:** macOS, Linux, or Windows 10/11 (with WSL2 recommended for Windows).
- **Java Development Kit (JDK):** JDK 17 (Azul Zulu, OpenJDK, or Android Studio bundled JetBrains Runtime). Note: Gradle compiles bytecode targeting Java 11 (`sourceCompatibility = JavaVersion.VERSION_11`).
- **Android Studio:** Android Studio Koala (2024.1.1) or Ladybug (2024.2.1)+ with Kotlin 2.2.x plugin support.
- **Android SDK:**
  - Build Tools: `36.0.0`
  - Platforms: Android API 36 (Android 16)
  - Minimum Device / Emulator: Android API 24 (Android 7.0 Nougat)

---

## 2. Environment Variables & API Key Configuration

> [!CAUTION]
> **Do NOT use or instruct developers to use `local.properties` for API keys.**
> LifeOS configures the Secrets Gradle Plugin to read from `.env` and `.env.example`.

1. **Local `.env` File:**
   Copy the example environment template or create a `.env` file at the root of the project:
   ```bash
   touch .env
   ```
2. **Configure Secrets (Optional):**
   ```properties
   GEMINI_API_KEY=your_google_ai_studio_api_key_here
   ```
   *Note:* If left blank or absent, LifeOS will still build and operate at 100% capacity using its on-device heuristic engine.

---

## 3. Command-Line Build Instructions

> [!IMPORTANT]
> In the cloud container environment, invoke `gradle` directly. In a local workstation terminal, use `./gradlew` (or `gradlew.bat` on Windows).

### 3.1 Assemble Debug APK
To compile and assemble the debug APK:
```bash
gradle :app:assembleDebug
```
The compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

### 3.2 Run Unit & Robolectric Tests
```bash
gradle :app:testDebugUnitTest
```

### 3.3 Verify Screenshot Golden Images (Roborazzi)
```bash
gradle :app:verifyRoborazziDebug
```

---

## 4. Keystore & Signing Configuration

- **Debug Builds:** Uses the pre-configured `debug.keystore` at the root of the project with default alias `androiddebugkey` and password `android`.
- **Release Builds:** Looks for environment variables `KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD`, or falls back to `my-upload-key.jks`.

---

## 5. Troubleshooting Common Issues

### Issue 1: "KSP / Kotlin Version Mismatch"
- **Cause:** KSP versions are strictly tied to specific Kotlin releases.
- **Resolution:** In `gradle/libs.versions.toml`, verify that `kotlin = "2.2.10"` is paired with compatible `googleDevtoolsKsp = "2.3.5"`.

### Issue 2: "Android SDK 36 not found"
- **Cause:** Local Android Studio has not downloaded the latest Android API 36 platform.
- **Resolution:** Open Android Studio > SDK Manager > SDK Platforms > Check "Android 16 (API Level 36)" > Apply.
