# LifeOS — AI Architecture & Intelligence Layer

This document details the dual-mode artificial intelligence architecture powering LifeOS, including on-device heuristic processing, permission-gated database querying, and optional cloud LLM fallback.

---

## 1. Dual-Mode Intelligence Architecture

LifeOS implements a **privacy-first, dual-mode AI engine** (`com.example.ai.LifeOSAI`):

```
                               ┌─────────────────────────┐
                               │   User Input / Prompt   │
                               └────────────┬────────────┘
                                            │
                                            ▼
                        ┌───────────────────────────────────────┐
                        │      LifeOSAI Central Dispatcher      │
                        └───────────────────┬───────────────────┘
                                            │
                  ┌─────────────────────────┴─────────────────────────┐
                  ▼                                                   ▼
     ┌─────────────────────────┐                         ┌─────────────────────────┐
     │  MODE 1: ON-DEVICE NLP  │                         │   MODE 2: CLOUD LLM     │
     │  (Always Active, 100%   │                         │  (Optional Fallback)    │
     │   Private & Offline)    │                         └────────────┬────────────┘
     └────────────┬────────────┘                                      │
                  │                                                   ▼
                  ├─► Rule-Based Query Parser            Checks BuildConfig.GEMINI_API_KEY
                  ├─► Granular AIPermissions Check                    │
                  ├─► Direct Room DAO Queries                         ▼
                  ├─► Regex Task Extraction              OkHttp 4.10.0 POST to Google
                  └─► Heuristic Summarizer               Gemini 3.5 Flash REST API
```

---

## 2. Mode 1: On-Device Heuristic Engine

The on-device engine operates with **zero latency, zero network traffic, and zero API costs**. It executes directly on `Dispatchers.IO` against Room database records.

### 2.1 Permission-Gated Life Assistant (`LifeOSAI.answerQuestion`)
When the user submits a natural language question in the AI Assistant or Insights tab, the engine:
1. **Checks Permissions:** Verifies `AIPermissions` (granular toggles for `accessNotes`, `accessTasks`, `accessHabits`, `accessExpenses`, `accessDiary`, `accessCaptures`). If a user has revoked permission for expenses, any financial query immediately returns:
   > *"I don't have permission to access your expense records. You can enable this in AI Permissions."*
2. **Intent & Category Matching:**
   - **Financial Inquiries (`contains("spent")`, `contains("expense")`, `contains("food")`):** Queries `repository.getAllExpenses()`, aggregates sums by category, and generates structured `AICard` metrics.
   - **Productivity & Tasks (`contains("task")`, `contains("pending")`, `contains("to-do")`):** Queries `repository.getAllTasks()`, filters by `!isCompleted`, and returns an itemized list of pending tasks and deadlines.
   - **Habits & Routines (`contains("habit")`, `contains("streak")`):** Queries `repository.getAllHabits()`, identifies top streaks and habits with zero completions today.
   - **Diary & Emotions (`contains("mood")`, `contains("feel")`, `contains("diary")`):** Analyzes recent diary entries and computes dominant mood distributions.
   - **Notes & Knowledge (`contains("note")`, `contains("idea")`):** Searches titles and tags across active notes.

### 2.2 Intelligent Task Extraction (`LifeOSAI.extractTasksFromText`)
- **Mechanism:** Scans unstructured note text for actionable items.
- **Rules:**
  1. Detects explicit checklist markers: `- [ ]`, `[ ]`, `TODO:`, `Task:`.
  2. Detects bullet points: `•`, `-`, `*`.
  3. Detects imperative action verbs and intent patterns: `need to`, `must`, `have to`, `finish`, `buy`, `call`, `send`, `schedule`, `submit`, `clean`, `read`.
- **Deduplication:** Strips markdown punctuation, removes short strings (< 3 chars), and filters duplicates.
- **User Confirmation:** Extracted tasks are displayed in an interactive review modal where the user can approve them before they are inserted into Room as `TaskEntity` records.

### 2.3 Note Summarization (`LifeOSAI.summarizeNote`)
- Analyzes note title, paragraph structure, and bullet points.
- Extracts central themes, computes word count, and generates structured bullet points highlighting key takeaways and action items.

### 2.4 Diary Voice & Thought Refinement (`LifeOSAI.refineDiaryFromPoints`)
- Takes raw, disjointed thought fragments or speech-to-text transcripts (e.g., *"worked out today felt great then finished project presentation tired"*).
- Cleans and formats into a coherent journal entry with a suggested reflective title, emotional mood categorization (`Mood.GREAT`), and relevant `#tags`.

---

## 3. Mode 2: Optional Cloud Fallback (Gemini REST)

### 3.1 Trigger Conditions
When a query does not match any local personal data keywords (e.g., *"Explain quantum entanglement"* or *"Write a workout plan for marathon training"*), the engine checks for a remote AI key:
1. Inspects `BuildConfig.GEMINI_API_KEY`.
2. If key is non-empty and does not equal `"mock_key"`, dispatches an asynchronous HTTPS POST request via OkHttp to the Gemini endpoint.

### 3.2 Request Specification
- **Endpoint:** `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key={API_KEY}`
- **Headers:** `Content-Type: application/json`
- **Body:**
  ```json
  {
    "contents": [
      {
        "parts": [
          { "text": "You are LifeOS AI, a private personal assistant. The user asks: [User Question]" }
        ]
      }
    ]
  }
  ```
- **Timeouts:** Connect: 15s, Read: 20s.
- **Graceful Failure:** If the request fails, network is unavailable, or key is invalid, the engine catches the exception and returns a helpful fallback response without crashing.

---

## 4. Privacy & Safety Summary

| Capability | Local Engine | Remote Fallback |
| :--- | :--- | :--- |
| **Network Required** | ❌ None (100% Offline) | ✅ Yes |
| **Data Leaves Device** | ❌ Never | ⚠️ Only the explicit query prompt |
| **Personal DB Sent to Cloud** | ❌ Never | ❌ Never (local records are NOT sent) |
| **Permission Gating** | ✅ Enforced via `AIPermissions` | N/A |
| **Default Configuration** | Active out of the box | Inactive unless user adds API key |
