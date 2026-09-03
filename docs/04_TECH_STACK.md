# LifeOS — Technical Stack & Configuration

This document specifies the exact, verified technical stack discovered from the build configuration, Version Catalog (`gradle/libs.versions.toml`), and project source code.

---

## 1. Core Platform & Runtime Environments

| Category | Specified Setting / Tool | Version | Configuration File |
| :--- | :--- | :--- | :--- |
| **Operating System Target** | Android OS | Android 7.0+ (API 24 to 36) | `app/build.gradle.kts` |
| **Compile SDK** | Android SDK 36 (minor API 1) | `36` (extension 1) | `app/build.gradle.kts` |
| **Target SDK** | Android SDK 36 | `36` | `app/build.gradle.kts` |
| **Minimum SDK** | Android SDK 24 (Nougat) | `24` | `app/build.gradle.kts` |
| **Application ID** | Unique Application Identifier | `com.aistudio.lifeos.kztuvq` | `app/build.gradle.kts` |
| **Namespace** | Java / R Resource Package | `com.example` | `app/build.gradle.kts` |
| **Version Code** | Build Incremental Version | `1` | `app/build.gradle.kts` |
| **Version Name** | Semantic App Release Version | `1.0` | `app/build.gradle.kts` |
| **Programming Language** | Kotlin | `2.2.10` | `gradle/libs.versions.toml` |
| **Java Compatibility** | Java Virtual Machine Bytecode | Java 11 (`VERSION_11`) | `app/build.gradle.kts` |
| **Build Tooling** | Gradle with Kotlin DSL | Gradle 8.11.1 | `gradle/wrapper/gradle-wrapper.properties` |
| **Android Gradle Plugin (AGP)** | Build System Plugin | `9.1.1` | `gradle/libs.versions.toml` |

---

## 2. Build Plugins Catalog

| Plugin ID | Version | Purpose | Declared In |
| :--- | :--- | :--- | :--- |
| `com.android.application` | `9.1.1` | Core AGP application compilation plugin | `app/build.gradle.kts` |
| `org.jetbrains.kotlin.plugin.compose` | `2.2.10` | Kotlin Compose compiler plugin | `app/build.gradle.kts` |
| `com.google.devtools.ksp` | `2.3.5` | Kotlin Symbol Processing for Room compiler | `app/build.gradle.kts` |
| `io.github.takahirom.roborazzi` | `1.59.0` | Screenshot verification plugin | `app/build.gradle.kts` |
| `com.google.android.libraries.mapsplatform.secrets-gradle-plugin` | `2.0.1` | Injecting API keys from `.env` to `BuildConfig` | `app/build.gradle.kts` |
| `com.google.gms.google-services` | `4.5.0` | Google Services / Firebase configuration | `app/build.gradle.kts` |

---

## 3. Production Libraries & Dependencies

| Library / Module | Group & Artifact | Version | Where Used | Purpose in LifeOS |
| :--- | :--- | :--- | :--- | :--- |
| **Compose BOM** | `androidx.compose:compose-bom` | `2024.09.00` | Entire UI Layer | Manages compatible versions for all Jetpack Compose libraries. |
| **Compose Material 3** | `androidx.compose.material3:material3` | BOM-managed | All Screens | Modern Material 3 cards, bottom bars, text fields, chips, and buttons. |
| **Compose UI & Graphics**| `androidx.compose.ui:ui` | BOM-managed | All Screens | Declarative UI canvas, rendering primitives, layouts, and brush gradients. |
| **Compose Extended Icons**| `androidx.compose.material:material-icons-extended` | BOM-managed | Navigation, LifeHub, Capture | Extended icon library for category symbols, timeline indicators, and actions. |
| **Navigation Compose** | `androidx.navigation:navigation-compose` | `2.8.9` | `MainActivity.kt`, `Navigation.kt` | Single-activity screen routing, backstack management, and route argument passing. |
| **Lifecycle ViewModel** | `androidx.lifecycle:lifecycle-viewmodel-compose` | `2.8.7` | All ViewModels | Ties ViewModel lifecycle to Composable navigation destinations. |
| **Lifecycle Runtime** | `androidx.lifecycle:lifecycle-runtime-compose` | `2.8.7` | UI Screens | `collectAsStateWithLifecycle()` for lifecycle-aware Flow collection. |
| **Activity Compose** | `androidx.activity:activity-compose` | `1.10.1` | `MainActivity.kt` | Integrates Compose root with Android `ComponentActivity` and edge-to-edge layout. |
| **AndroidX Core KTX** | `androidx.core:core-ktx` | `1.18.0` | System-wide | Kotlin extensions for Android framework APIs. |
| **Room Runtime** | `androidx.room:room-runtime` | `2.7.0` | `AppDatabase.kt` | SQLite database abstraction engine for local-first persistence. |
| **Room KTX** | `androidx.room:room-ktx` | `2.7.0` | DAOs & Repositories | Coroutine and reactive Kotlin Flow support for Room database queries. |
| **Room Compiler (KSP)** | `androidx.room:room-compiler` | `2.7.0` | Build phase | Generates DAO implementations and SQLite table schemas. |
| **Kotlinx Coroutines** | `org.jetbrains.kotlinx:kotlinx-coroutines-android` | `1.10.2` | Repositories, ViewModels, AI | Asynchronous background execution and reactive Flow streams. |
| **OkHttp** | `com.squareup.okhttp3:okhttp` | `4.10.0` | `LifeOSAI.kt` | Asynchronous HTTP client for optional cloud Gemini REST fallback. |
| **Retrofit & Moshi** | `com.squareup.retrofit2:retrofit`, `moshi-kotlin` | `2.12.0` / `1.15.2`| Declared in dependencies | REST serialization tooling available in the runtime environment. |
| **Coil Compose** | `io.coil-kt:coil-compose` | `2.7.0` | Declared in dependencies | Asynchronous image loading for Android and web URIs. |

---

## 4. Testing & Verification Tooling

| Library / Tool | Group & Artifact | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **JUnit 4** | `junit:junit` | `4.13.2` | Local JVM unit test execution framework. |
| **Robolectric** | `org.robolectric:robolectric` | `4.16.1` | Android framework simulation for rapid local JVM testing without emulators. |
| **Roborazzi** | `io.github.takahirom.roborazzi:roborazzi` | `1.59.0` | Local JVM automated screenshot verification and visual regression testing. |
| **Compose UI Test Rule**| `androidx.compose.ui:ui-test-junit4` | BOM-managed | Compose UI testing and component interaction assertions. |
| **Coroutines Test** | `org.jetbrains.kotlinx:kotlinx-coroutines-test` | `1.10.2` | Test dispatchers and coroutine flow testing utilities. |
