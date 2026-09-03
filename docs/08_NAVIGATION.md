# LifeOS — Navigation & Routing Architecture

This document describes the routing structure, screen destinations, backstack lifecycle, and bottom bar behavior of LifeOS.

---

## 1. Navigation Architecture Overview

LifeOS uses **Jetpack Navigation Compose** (`androidx.navigation:navigation-compose:2.8.9`) within a single-activity architecture (`MainActivity.kt`).

```
                              ┌────────────────────┐
                              │    MainActivity    │
                              │ (LifeOSMainApp UI) │
                              └─────────┬──────────┘
                                        │
           ┌────────────────────────────┼────────────────────────────┐
           ▼                            ▼                            ▼
┌─────────────────────┐      ┌─────────────────────┐      ┌─────────────────────┐
│   Top Bar Header    │      │  NavHost Container  │      │   LifeOSBottomBar   │
│ (Dynamic Context)   │      │ (Active Composable) │      │ (5 Nav Destinations)│
└─────────────────────┘      └──────────┬──────────┘      └─────────────────────┘
                                        │
     ┌──────────────┬───────────────────┼───────────────────┬──────────────┐
     ▼              ▼                   ▼                   ▼              ▼
┌─────────┐   ┌───────────┐    ┌─────────────────┐    ┌───────────┐   ┌─────────┐
│  Home   │   │   Notes   │    │   Note Editor   │    │ Insights  │   │  Life   │
│ Screen  │   │  Screen   │    │ Screen (param)  │    │  Screen   │   │ Screen  │
└─────────┘   └───────────┘    └─────────────────┘    └───────────┘   └─────────┘
                                        ▲
                                        │ (Opens as Modal Sheet)
                               ┌─────────────────┐
                               │  CaptureSheet   │
                               │  (Bottom Modal) │
                               └─────────────────┘
```

---

## 2. Route Definitions (`Screen.kt`)

Routes are declared as a sealed class in `com.example.ui.navigation.Navigation.kt`:

| Route String | Composable Destination | Purpose | Arguments |
| :--- | :--- | :--- | :--- |
| `"home"` | `HomeScreen` | Dashboard, to-dos, streaks, quick search | None |
| `"notes"` | `NotesScreen` | Folder notes list, search, AI note actions | None |
| `"capture"` | `CaptureSheet` (Modal) | Fast sub-3-second capture sheet | None |
| `"insights"` | `InsightsScreen` | Productivity, expense, and mood analytics | None |
| `"life"` | `LifeHubScreen` | Timeline, Calendar, Diary, Expenses, Habits, AI, Backup | None |
| `"note_editor/{noteId}"`| `NoteEditorScreen` | Fullscreen note viewing & editing | `noteId: LongType` (Default: `0L`) |

---

## 3. Bottom Bar Architecture (`LifeOSBottomBar`)

The persistent bottom bar is rendered via `LifeOSBottomBar` in `Navigation.kt`:
1. **Home:** Icon: `Icons.Filled.Home` / `Icons.Outlined.Home`.
2. **Notes:** Icon: `Icons.Filled.Description` / `Icons.Outlined.Description`.
3. **+ Capture (Center Highlight):**
   - Renders an elevated circular floating action button (FAB) offset upwards by `-10.dp`.
   - Instead of navigating to a full screen, tapping the center button triggers the `onOpenCapture` callback, displaying the `CaptureSheet` as an interactive `ModalBottomSheet`.
4. **Insights:** Icon: `Icons.Filled.Analytics` / `Icons.Outlined.Analytics`.
5. **Life:** Icon: `Icons.Filled.Hub` / `Icons.Outlined.Hub`.

---

## 4. Backstack & State Preservation

When navigating between top-level bottom-bar destinations:
```kotlin
onNavigate = { screen ->
    navController.navigate(screen.route) {
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
```
- `popUpTo(...startDestinationId)`: Ensures pressing the Android system back button returns the user to the Home dashboard before exiting the app.
- `saveState = true` / `restoreState = true`: Preserves scroll positions, text field inputs, and tab indices across screen switches.
- `launchSingleTop = true`: Prevents duplicate copies of the same destination from stacking if tapped repeatedly.

---

## 5. Argument Passing Contract (`NoteEditorScreen`)

When tapping a note card or the "Add Note" button:
- **New Note Creation:** Navigates to `Screen.NoteEditor.createRoute(0L)` (`"note_editor/0"`). The editor detects `noteId == 0L` and enters blank creation mode.
- **Editing Existing Note:** Navigates to `Screen.NoteEditor.createRoute(note.id)` (`"note_editor/42"`).
- **Argument Handling:**
  ```kotlin
  composable(
      route = Screen.NoteEditor.route,
      arguments = listOf(navArgument("noteId") { type = NavType.LongType; defaultValue = 0L })
  ) { backStackEntry ->
      val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
      NoteEditorScreen(...)
  }
  ```
