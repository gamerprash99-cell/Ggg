# LifeOS — Product Vision & Strategy

## 1. Source Document Status

- **"Lifeos.txt":** «Not found in the inspected project.»
- **"Life Master PRD.txt":** «Not found in the inspected project.»

*Note on Source Traceability:* The PRD documents referenced in the mandate do not physically exist in the project repository root or subdirectories. The product vision, feature requirements, and domain boundaries documented below have been reverse-engineered with 100% fidelity from the active codebase, domain models (`Models.kt`), database schema (`Entities.kt`), application metadata (`metadata.json`), repository seed logic (`LifeOSRepository.checkAndSeedInitialData()`), and UI workflows.

---

## 2. Problem Statement

Modern individuals suffer from **Digital Life Fragmentation**:
- **Tool Sprawl:** Managing daily life requires separate apps for tasks (Todoist/TickTick), notes (Notion/Keep), habit tracking (Streaks), personal finance (Spendee/Wallet), and journaling (Day One).
- **Context Loss:** Actions in one app do not inform another. For example, spending money on study materials does not connect to academic notes or daily goals.
- **Privacy Vulnerability:** Most modern productivity tools require remote cloud databases, subjecting intimate thoughts and financial logs to data mining, security breaches, and subscription monetization.
- **Friction to Capture:** Opening heavy web-based apps with deep hierarchies takes 5–15 seconds, causing ephemeral thoughts, ideas, and expenses to be lost.

---

## 3. The LifeOS Solution

LifeOS offers a unified mobile operating layer with five foundational pillars:
1. **Pillar 1: Productivity (Tasks & Goals)** — Focused daily checklists, priority tagging, and automated progress metrics.
2. **Pillar 2: Knowledge (Second Brain Notes)** — Categorized markdown notes with AI task extraction and summarization.
3. **Pillar 3: Habits & Routines** — Daily streak trackers with logging and frequency targets.
4. **Pillar 4: Personal Finance** — Fast expense tracking categorized across daily life essentials.
5. **Pillar 5: Mind & Memory (Diary & Timeline)** — Emotion-aware reflections and a chronological life event ledger.

---

## 4. Product Principles

| Principle | Meaning in LifeOS |
| :--- | :--- |
| **Local Sovereignity** | All data writes directly to device storage (`lifeos_database`). The app requires zero network connection to function at 100% capacity. |
| **Sub-3-Second Capture** | The persistent, central elevated `+ Capture` button allows logging thoughts, media, tasks, or expenses immediately from any screen. |
| **Integrated Event Stream** | When an action occurs (a habit logged, a task completed, an expense recorded), a `LifeEventEntity` is automatically created, providing a unified chronological narrative. |
| **Privacy-Guarded AI** | The AI assistant queries local SQLite records. Users have granular toggle switches (`AIPermissions`) to grant or revoke AI access to specific life categories (e.g., allow Notes, deny Expenses). |
| **Export Freedom** | Users can export their entire database to structured JSON at any time, guaranteeing zero vendor lock-in. |

---

## 5. Vision vs. Implementation Gap Analysis

| Vision / PRD Dimension | Specification Expectation | Current Codebase Implementation | Status |
| :--- | :--- | :--- | :--- |
| **Offline Operation** | 100% offline core features | Room SQLite database handles all storage offline | ✅ Implemented |
| **Quick Capture** | Capture thoughts, audio, photos, video | UI supports capture modal; stores text, tags, and mediaUri | 🟡 Partially implemented (Media URI field exists, hardware camera/audio recorder not wired) |
| **Note AI Intelligence** | Summarize notes & extract tasks | Heuristic rules extract checkboxes and generate structured summaries | ✅ Implemented |
| **Central AI Assistant** | Conversational life assistant | Local query engine parses tasks, expenses, habits, notes + Gemini REST fallback | ✅ Implemented |
| **Life Timeline** | Unified chronological life log | Events auto-generated across note, task, habit, and expense DAOs | ✅ Implemented |
| **App Security** | PIN / Biometric lock | `AppLockDialog` UI component and PIN toggle exist in Settings | 🟡 Partially implemented (PIN toggle state stored in memory; persistent keystore lock not finalized) |
| **Full Backup & Restore** | Export / import database | Full JSON serialization/deserialization implemented in `LifeOSRepository` | ✅ Implemented |
| **Cloud Synchronization** | Multi-device synchronization | Not present; pure offline Room architecture | 🔴 Specified but not implemented (Out of scope for offline-first MVP) |
