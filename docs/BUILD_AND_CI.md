# LifeOS Android CI & Build Diagnostics Documentation

## Problem

The GitHub Actions workflow (`Android CI`, task `testDebugUnitTest` / `compileDebugUnitTestKotlin`) failed during Kotlin compilation:

```text
Caused by: org.jetbrains.kotlin.gradle.tasks.CompilationErrorException: Compilation error.
```

The underlying Kotlin compiler errors was hidden by Gradle's stack trace wrapper until running `./gradlew compileDebugUnitTestKotlin` directly.

## Root Cause

1. **Unresolved References in Screenshot Test**:
   `app/src/test/java/com/example/GreetingScreenshotTest.kt` attempted to import and call `MyApplicationTheme` and `Greeting`. Neither symbol existed in the project codebase (the application theme is `LifeOSTheme`, and no `Greeting` composable was defined in the application).

2. **Robolectric Assertion Mismatch**:
   `app/src/test/java/com/example/ExampleRobolectricTest.kt` checked for `"My Application"` string resource value, while `res/values/strings.xml` defines `app_name` as `"LifeOS"`.

3. **Keystore Requirement for Debug Builds**:
   `app/build.gradle.kts` configures `signingConfigs.debugConfig` expecting `debug.keystore` at the project root (`${rootDir}/debug.keystore`). CI creates `debug.keystore` prior to running `./gradlew assembleDebug`, but local builds without `debug.keystore` failed at `:app:validateSigningDebug`.

## Affected Files

- `app/src/test/java/com/example/GreetingScreenshotTest.kt`
- `app/src/test/java/com/example/ExampleRobolectricTest.kt`
- `docs/BUILD_AND_CI.md`

## Fix

1. **`GreetingScreenshotTest.kt`**:
   - Replaced invalid `MyApplicationTheme` import with `com.example.ui.theme.LifeOSTheme`.
   - Replaced invalid `Greeting("Robolectric")` reference with `SectionHeader(title = "LifeOS Overview")`.

2. **`ExampleRobolectricTest.kt`**:
   - Updated assertion to match actual resource value `"LifeOS"`.

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

1. Always run `./gradlew testDebugUnitTest` locally before pushing changes or opening pull requests.
2. When renaming or refactoring core theme components, update or verify test sources under `src/test/java` to reflect changes.
