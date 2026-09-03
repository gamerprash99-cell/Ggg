# LifeOS Android CI & Build Diagnostics Documentation

## Problem

1. **Kotlin Compilation Error (Initial Failure)**:
   The GitHub Actions workflow (`Android CI`, task `testDebugUnitTest` / `compileDebugUnitTestKotlin`) originally failed during Kotlin compilation:

   ```text
   Caused by: org.jetbrains.kotlin.gradle.tasks.CompilationErrorException: Compilation error.
   ```

2. **Robolectric SDK Runtime Failure (CI Environment Mismatch)**:
   After fixing the compilation error, Robolectric tests failed in CI with:

   ```text
   java.lang.UnsupportedOperationException at DefaultSdkProvider.java:170
   ```

   This occurred because `@Config(sdk = [36])` specified SDK 36, which is not supported by Robolectric 4.16.1.

## Root Cause

1. **Unresolved References in Screenshot Test**:
   `app/src/test/java/com/example/GreetingScreenshotTest.kt` attempted to import and call `MyApplicationTheme` and `Greeting`. Neither symbol existed in the project codebase (the application theme is `LifeOSTheme`, and no `Greeting` composable was defined in the application).

2. **Robolectric Assertion Mismatch**:
   `app/src/test/java/com/example/ExampleRobolectricTest.kt` checked for `"My Application"` string resource value, while `res/values/strings.xml` defines `app_name` as `"LifeOS"`.

3. **Unsupported Robolectric SDK Target (SDK 36)**:
   `ExampleRobolectricTest.kt` and `GreetingScreenshotTest.kt` were configured with `@Config(sdk = [36])`. Robolectric 4.16.1 supports up to Android SDK 35 (VanillaIceCream). Attempting SDK 36 caused `UnsupportedOperationException` in environments without pre-cached SDK 36 jars.

4. **Keystore Requirement for Debug Builds**:
   `app/build.gradle.kts` configures `signingConfigs.debugConfig` expecting `debug.keystore` at the project root (`${rootDir}/debug.keystore`). CI creates `debug.keystore` prior to running `./gradlew assembleDebug`, but local builds without `debug.keystore` failed at `:app:validateSigningDebug`.

## Affected Files

- `app/src/test/java/com/example/GreetingScreenshotTest.kt`
- `app/src/test/java/com/example/ExampleRobolectricTest.kt`
- `docs/BUILD_AND_CI.md`

## Fix

1. **`GreetingScreenshotTest.kt`**:
   - Replaced invalid `MyApplicationTheme` import with `com.example.ui.theme.LifeOSTheme`.
   - Replaced invalid `Greeting("Robolectric")` reference with `SectionHeader(title = "LifeOS Overview")`.
   - Updated Robolectric SDK target to `@Config(sdk = [35])`.

2. **`ExampleRobolectricTest.kt`**:
   - Updated assertion to match actual resource value `"LifeOS"`.
   - Updated Robolectric SDK target to `@Config(sdk = [35])`.

3. **Debug Keystore**:
   - Local environment configured using `keytool` to generate a local `debug.keystore` consistent with CI instructions without committing binary keystore files to the repository.

## Verification

The following Gradle commands were executed and verified:

```bash
# 1. Clean and run unit tests + build debug APK
./gradlew clean testDebugUnitTest assembleDebug

# 2. Force re-running unit test suite without cache
./gradlew testDebugUnitTest --rerun-tasks
```

All 3 unit tests passed (`ExampleUnitTest`, `ExampleRobolectricTest`, `GreetingScreenshotTest`) and `:app:assembleDebug` produced the debug APK successfully.

## CI Compatibility

The GitHub Actions configuration (`.github/workflows/android.yml`) remains fully compatible:
- Uses JDK 17 (`temurin`)
- Generates `debug.keystore` if missing before assembly
- Sets up `.env` from `.env.example`
- Runs `./gradlew testDebugUnitTest --stacktrace`
- Runs `./gradlew assembleDebug --stacktrace`

## Prevention

1. Always verify Robolectric SDK target matches maximum supported version in configured Robolectric version (SDK 35 for Robolectric 4.16.1).
2. Always run `./gradlew testDebugUnitTest --rerun-tasks` locally before pushing changes or opening pull requests.
3. When renaming or refactoring core theme components, update or verify test sources under `src/test/java` to reflect changes.
