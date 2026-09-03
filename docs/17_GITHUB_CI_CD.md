# LifeOS — GitHub Actions CI/CD Pipeline Reference

This document provides a technical specification of the automated Continuous Integration and Continuous Deployment (CI/CD) pipeline implemented for LifeOS.

---

## 1. Workflow Architecture & Configuration

- **Workflow File:** `.github/workflows/android.yml`
- **Workflow Name:** `Android CI`
- **Runner Environment:** `ubuntu-latest`

```
┌─────────────────────────────────────────────────────────────┐
│                 GitHub Actions Event Trigger                │
│       (Push or Pull Request to `main` or `master`)          │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                  Step 1: Checkout Repository                │
│                    actions/checkout@v4                      │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                  Step 2: Setup JDK 17                       │
│           actions/setup-java@v4 (distribution: 'zulu')      │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                  Step 3: Setup Gradle Cache                 │
│                 gradle/actions/setup-gradle@v4              │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│             Step 4: Restore Debug Keystore                  │
│       Decodes debug.keystore.base64 into debug.keystore     │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│              Step 5: Run Unit & JVM Tests                   │
│               ./gradlew testDebugUnitTest                   │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│              Step 6: Build Debug Application                │
│                  ./gradlew assembleDebug                    │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│             Step 7: Upload Build & Test Artifacts           │
│     - Debug APK (app/build/outputs/apk/debug/*.apk)         │
│     - Test Reports (app/build/reports/tests/testDebugUnitTest)
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Trigger Conditions

The pipeline is triggered automatically on:
- Any `push` targeting branch `main` or `master`.
- Any `pull_request` targeting branch `main` or `master`.
- Manual on-demand execution via `workflow_dispatch`.

---

## 3. Detailed Step Breakdown

### Step 1: Checkout Repository
Uses `actions/checkout@v4` with full commit history to ensure Git metadata is available for versioning.

### Step 2: Set up JDK 17
Configures Azul Zulu JDK 17, which satisfies Gradle 8.11.1 requirements and compiles Java 11 bytecode targets.

### Step 3: Setup Gradle & Build Cache
Leverages `gradle/actions/setup-gradle@v4` to cache Gradle dependencies, wrappers, and build cache across pipeline runs, speeding up build execution times by 50–70%.

### Step 4: Keystore Recovery
If `debug.keystore.base64` is stored in the repository, this step decodes it into `debug.keystore` at the root of the project to ensure signing succeeds without missing keystore warnings:
```bash
if [ -f "debug.keystore.base64" ]; then
  base64 -d debug.keystore.base64 > debug.keystore
fi
```

### Step 5: Execute Unit Tests
Runs all JVM and Robolectric unit tests via `./gradlew testDebugUnitTest`. 

### Step 6: Assemble Debug APK
Executes `./gradlew assembleDebug` to compile resources, run KSP symbol processing, assemble Compose bytecode, and generate the final debug APK.

### Step 7: Artifact Archival
Uploads the compiled APK and HTML test execution reports as downloadable GitHub artifacts with a 14-day retention policy:
- **`lifeos-debug-apk`**: Contains `app/build/outputs/apk/debug/app-debug.apk`.
- **`test-reports`**: Contains detailed HTML reports showing test pass/fail metrics.

---

## 4. Production Release Recommendations

For future production releases:
1. **GitHub Secrets:** Add `KEYSTORE_BASE64`, `STORE_PASSWORD`, `KEY_ALIAS`, and `KEY_PASSWORD` to repository secrets.
2. **Release Job:** Add a conditional release job triggered on Git tags (`v*.*.*`) that runs `./gradlew bundleRelease` to generate an Android App Bundle (`.aab`) ready for Google Play upload.
