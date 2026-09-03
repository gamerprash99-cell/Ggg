# LifeOS — Dependency Audit & Risk Analysis

This document catalogs every production, compiler, and testing dependency declared in `gradle/libs.versions.toml` and `app/build.gradle.kts`, detailing why it is included and the risks associated with modifying or removing it.

---

## 1. Production Dependencies

| Version Catalog Key | Artifact Name | Exact Version | Removal Risk & Impact |
| :--- | :--- | :--- | :--- |
| `libs.androidx.compose.bom` | `androidx.compose:compose-bom` | `2024.09.00` | **CRITICAL:** Removing or changing breaks alignment across all Jetpack Compose libraries, leading to runtime NoSuchMethodError crashes. |
| `libs.androidx.compose.material3` | `androidx.compose.material3:material3` | Managed by BOM | **CRITICAL:** UI foundation. Removing breaks all buttons, cards, scaffolds, text fields, and theme colors. |
| `libs.androidx.compose.ui` | `androidx.compose.ui:ui` | Managed by BOM | **CRITICAL:** Core rendering engine for Compose. |
| `libs.androidx.compose.ui.graphics` | `androidx.compose.ui:ui-graphics` | Managed by BOM | **HIGH:** Required for color models, canvas drawing, brushes, and gradient banners. |
| `libs.androidx.compose.material.icons.extended` | `androidx.compose.material:material-icons-extended` | Managed by BOM | **HIGH:** LifeOS uses dozens of extended icons (e.g., `Analytics`, `Hub`, `Audiotrack`, `LockOpen`). Removing causes compilation failures. |
| `libs.androidx.activity.compose` | `androidx.activity:activity-compose` | `1.10.1` | **CRITICAL:** Bridges Android `ComponentActivity` with Compose `setContent`. Removing breaks application launch. |
| `libs.androidx.core.ktx` | `androidx.core:core-ktx` | `1.18.0` | **HIGH:** Core Android Kotlin extensions used across the system. |
| `libs.androidx.lifecycle.runtime.compose` | `androidx.lifecycle:lifecycle-runtime-compose` | `2.8.7` | **CRITICAL:** Provides `collectAsStateWithLifecycle()`. Removing causes compilation failures in all screens. |
| `libs.androidx.lifecycle.viewmodel.compose` | `androidx.lifecycle:lifecycle-viewmodel-compose` | `2.8.7` | **CRITICAL:** Provides `viewModel()` Composable hook. |
| `libs.androidx.navigation.compose` | `androidx.navigation:navigation-compose` | `2.8.9` | **CRITICAL:** Required for single-activity navigation and backstack management. |
| `libs.androidx.room.runtime` | `androidx.room:room-runtime` | `2.7.0` | **CRITICAL:** Database runtime engine. Removing breaks all data storage. |
| `libs.androidx.room.ktx` | `androidx.room:room-ktx` | `2.7.0` | **CRITICAL:** Required for Room Coroutine and reactive Kotlin `Flow` support. |
| `libs.androidx.room.compiler` (KSP) | `androidx.room:room-compiler` | `2.7.0` | **CRITICAL:** Code generator for Room entities, DAOs, and SQLite queries. Must match Kotlin/KSP versions. |
| `libs.kotlinx.coroutines.android` | `org.jetbrains.kotlinx:kotlinx-coroutines-android` | `1.10.2` | **CRITICAL:** Manages main-thread and background coroutines (`Dispatchers.IO`, `Dispatchers.Main`). |
| `libs.okhttp` | `com.squareup.okhttp3:okhttp` | `4.10.0` | **MEDIUM:** Used in `LifeOSAI.kt` for optional Gemini cloud calls. If removed, `LifeOSAI` will fail compilation unless remote fallback is refactored. |

---

## 2. Test & Quality Dependencies

| Version Catalog Key | Artifact Name | Exact Version | Purpose |
| :--- | :--- | :--- | :--- |
| `libs.junit` | `junit:junit` | `4.13.2` | Core JVM test execution framework. |
| `libs.androidx.junit` | `androidx.test.ext:junit` | `1.3.0` | AndroidX test runners and assertions. |
| `libs.robolectric` | `org.robolectric:robolectric` | `4.16.1` | Local JVM Android simulation (enables fast testing without an emulator). |
| `libs.roborazzi` | `io.github.takahirom.roborazzi:roborazzi` | `1.59.0` | Automated screenshot test verification engine. |
| `libs.roborazzi.compose` | `io.github.takahirom.roborazzi:roborazzi-compose` | `1.59.0` | Roborazzi integration with Jetpack Compose previews. |
| `libs.kotlinx.coroutines.test` | `org.jetbrains.kotlinx:kotlinx-coroutines-test` | `1.10.2` | Coroutine test dispatchers and flow testing. |

---

## 3. Commented-Out Template Dependencies

In `app/build.gradle.kts`, several template libraries are deliberately commented out to optimize APK size and reduce build overhead:
- `libs.accompanist.permissions` (Replaced by native Compose runtime permission APIs)
- `libs.androidx.camera.*` (Reserved for future hardware CameraX integration)
- `libs.firebase.firestore` / `firebase.auth` (LifeOS is offline-first and does not use Firestore)
- `libs.play.services.location` (LifeOS does not track GPS location)
