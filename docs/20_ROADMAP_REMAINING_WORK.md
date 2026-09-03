# LifeOS — Product & Engineering Roadmap

This document outlines the recommended engineering roadmap to take LifeOS from its current feature-complete foundation to a hardened, production-ready release on Google Play.

---

## 1. Roadmap Overview by Phase

```
┌─────────────────────────────────────────────────────────────┐
│             PHASE 1: TEST & STABILIZATION (Week 1)          │
│  - Resolve legacy test failures (Robolectric & Roborazzi)   │
│  - Validate green CI/CD build in GitHub Actions             │
│  - Code cleanup of unused template comments                 │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│          PHASE 2: HARDWARE MEDIA INTEGRATION (Week 2-3)     │
│  - Android Photo Picker for zero-permission image attachment│
│  - Audio memo voice recorder for Quick Capture              │
│  - Media playback in Diary and Capture views                │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│           PHASE 3: SECURITY & BIOMETRIC HARDENING (Week 4)  │
│  - Android BiometricPrompt API (Fingerprint / Face Unlock)  │
│  - Keystore-backed PIN storage with EncryptedDataStore      │
│  - App lifecycle foreground/background auto-lock            │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│         PHASE 4: ENCRYPTED BACKUP & SHARING (Week 5)        │
│  - Password-protected AES-256 encrypted JSON exports        │
│  - Android Storage Access Framework (SAF) file picker export│
│  - Automated local scheduled backup to device storage       │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│            PHASE 5: ADVANCED AI & ON-DEVICE LLM (Week 6-7)  │
│  - Integration of Gemini Nano via MediaPipe GenAI SDK       │
│  - Local vector embeddings for semantic note search         │
│  - Multi-turn conversational history with context memory    │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│             PHASE 6: PRODUCTION & GOOGLE PLAY (Week 8)      │
│  - Production keystore signing and ProGuard R8 verification │
│  - Play Store Data Safety section declarations              │
│  - Release Android App Bundle (AAB) generation              │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Detailed Task Breakdown

### Phase 1: Test & Stabilization (Immediate)
1. **Fix `ExampleRobolectricTest.kt`:** Update expected string assertion from `"My Application"` to `"LifeOS"`.
2. **Fix `GreetingScreenshotTest.kt`:** Re-target screenshot capture to `LifeOSBottomBar` inside `LifeOSTheme`.
3. **Verify CI/CD:** Ensure `./gradlew testDebugUnitTest` and `./gradlew assembleDebug` run clean and green.

### Phase 2: Hardware Media Integration
1. **Photo Picker Integration:** Use `ActivityResultContracts.PickVisualMedia` in `CaptureSheet.kt` to allow users to attach gallery photos without requiring dangerous broad storage permissions.
2. **Voice Recorder Integration:** Wire `android.permission.RECORD_AUDIO` to record 30-second quick voice notes directly into the app's private files directory.

### Phase 3: Security Hardening
1. **Biometric Authentication:** Implement `androidx.biometric:biometric` for one-touch fingerprint authentication.
2. **Encrypted Storage:** Migrate PIN lock state from memory to `androidx.security:security-crypto`.

### Phase 4: Encrypted Backup & Sharing
1. **Password-Protected Export:** Add a password input field when generating backups, using PBKDF2 key derivation and AES-GCM encryption.
2. **Storage Access Framework (SAF):** Enable direct saving to Downloads or an SD card via `CreateDocument` contract.

### Phase 5: On-Device Generative AI
1. **Gemini Nano:** Explore integrating Google's on-device Gemini Nano model on supported Android 14+ devices, bringing generative AI capabilities to LifeOS with zero internet connection.
