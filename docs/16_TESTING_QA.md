# LifeOS — Testing Strategy, QA Audit & Verification Matrix

This document provides a thorough audit of existing automated tests, local JVM testing patterns, known test defects, and a comprehensive manual QA verification matrix.

---

## 1. Automated Testing Strategy

LifeOS adopts an **offline, local JVM-first testing methodology**:
- **Robolectric:** Enables fast, robust unit and integration testing of Android framework components (Context, SharedPreferences, Resources, SQLite Room) without needing an emulator or physical device.
- **Roborazzi:** Visual regression testing that captures rendered Compose composables and compares them against reference PNG golden images directly on the host JVM.
- **Instrumented Test Policy:** In this cloud development environment, running emulators (`adb`) is unsupported. All Critical User Journeys (CUJs) must be validated via Robolectric and Roborazzi.

---

## 2. Audit of Existing Test Files & Defects

A strict line-by-line inspection of `app/src/test/java/com/example/` reveals the following status:

| Test File | Current Status | Findings & Exact Defect |
| :--- | :--- | :--- |
| `ExampleUnitTest.kt` | ✅ Passes | Basic JVM math assertion (`assertEquals(4, 2 + 2)`). |
| `ExampleRobolectricTest.kt` | ⚠️ **Failing** | **Defect:** Line 19 asserts `assertEquals("My Application", appName)`. The app name was updated to `"LifeOS"` in `res/values/strings.xml`, causing this test to fail with an `AssertionError`. |
| `GreetingScreenshotTest.kt` | ⚠️ **Failing** | **Defect:** Imports `com.example.ui.theme.MyApplicationTheme` and renders `Greeting("Robolectric")`. Both `MyApplicationTheme` and `Greeting` were legacy template placeholders that no longer exist in the refactored codebase. |

### Recommended Remediation
1. Update `ExampleRobolectricTest.kt` line 19 to:
   ```kotlin
   assertEquals("LifeOS", appName)
   ```
2. Refactor `GreetingScreenshotTest.kt` to import `com.example.ui.theme.LifeOSTheme` and render a live component, such as `GlassCard` or `LifeOSBottomBar`.

---

## 3. Manual QA Verification Matrix (Step-by-Step)

### 3.1 Pillar 1: Productivity & Dashboard (Home)
- [ ] **TC-01: Header & Greeting:** Verify header displays formatted uppercase date, day of week, and dynamic greeting based on device time.
- [ ] **TC-02: Quick Task Creation:** Type "Buy groceries" in the quick-add field on Home and tap "+". Verify task immediately appears in the list.
- [ ] **TC-03: Task Checkbox & Progress:** Tap the checkbox next to a task. Verify strike-through styling applies, task completion percentage increments, and a `TASK_COMPLETED` event appears on the Life Timeline.
- [ ] **TC-04: Habit Quick Check-in:** Tap a habit circle on the Home screen. Verify streak increments and overall daily progress circle updates.
- [ ] **TC-05: Global Search:** Type a query into the Home search bar. Verify matching notes, tasks, expenses, and diary entries appear below the bar.

### 3.2 Pillar 2: Notes & Second Brain
- [ ] **TC-06: Create Note:** Navigate to Notes > Tap "+ Add Note". Enter title, body, select "College" folder, and add tags. Tap Save. Verify note appears under "College" and "All".
- [ ] **TC-07: Pin & Favorite:** Tap the Pin icon on a note. Verify it moves to the top of the list.
- [ ] **TC-08: AI Note Summarizer:** Open a note > Tap "✨ Ask LifeOS AI" > Tap "Summarize Note". Verify structured bullet points appear.
- [ ] **TC-09: AI Task Extraction:** In a note with checklist items or action phrases ("need to finish report"), tap "Extract Actionable Tasks". Verify candidate tasks are parsed and tapping "Approve" converts them into active tasks.

### 3.3 Pillar 3: Quick Capture
- [ ] **TC-10: Sub-3-Second Capture:** Tap the central elevated `+ Capture` FAB from any screen. Verify `CaptureSheet` animates from the bottom. Select type (Thought/Photo/Video/Audio), enter text, select mood, and tap "Quick Save". Verify it closes smoothly and persists in Room.

### 3.4 Pillar 4: Life Hub (Timeline, Diary, Expenses, Habits)
- [ ] **TC-11: Timeline Event Feed:** Open Life tab > Tab 0 ("⏱️ Timeline"). Verify chronological events for notes created, tasks completed, and expenses logged appear with timestamps.
- [ ] **TC-12: Expense Logging & Total:** Tab 3 ("💸 Expenses") > Add expense of ₹250 (Category: Food, Note: "Lunch"). Verify list updates and total daily spending recalculates.
- [ ] **TC-13: Diary Reflection:** Tab 2 ("📔 Diary") > Enter entry, select mood (Happy), tap Save. Verify entry is saved with mood icon.

### 3.5 Pillar 5: Central AI Assistant & Permissions
- [ ] **TC-14: Local Expense Query:** Tab 5 ("🤖 AI Assistant") > Send "How much did I spend on food?". Verify AI returns exact calculated spending without making a network call.
- [ ] **TC-15: AI Permission Toggle:** Uncheck "Expenses" under AI Permissions. Re-send "How much did I spend on food?". Verify AI refuses access due to missing permissions.

### 3.6 Pillar 6: Backup & Privacy
- [ ] **TC-16: Generate JSON Backup:** Tab 6 ("⚙️ Backup & Privacy") > Tap "Generate Local Backup". Verify JSON payload is generated and displayed.
- [ ] **TC-17: Restore Backup:** Copy the JSON string > Tap "Restore from JSON". Verify database restores existing entities successfully.
