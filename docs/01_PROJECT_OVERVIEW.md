# LifeOS — Project Overview

## 1. Project Identification

- **Official Project Name:** LifeOS
- **Application ID:** `com.aistudio.lifeos.kztuvq`
- **Primary Namespace:** `com.example`
- **Platform:** Android (Handheld, Tablet, ChromeOS responsive)
- **Minimum Android Version:** Android 7.0 (API Level 24)
- **Target Android Version:** Android 16 (API Level 36)
- **Primary Programming Language:** Kotlin 2.2.10
- **UI Framework:** Jetpack Compose (BOM 2024.09.00) with Material 3

---

## 2. Product Category & Purpose

LifeOS is an **offline-first, unified personal operating system and second brain** for Android. It replaces the fragmentation of having 5 to 7 disconnected apps (a separate todo app, habit tracker, expense logger, note-taking app, mood diary, and calendar) with a single, highly integrated, privacy-focused application.

---

## 3. Explanations Across Contexts

### 1-Sentence Explanation
> **LifeOS is a 100% private, offline-first personal operating system that unifies notes, tasks, habits, expenses, and diary memories with on-device AI intelligence.**

### 30-Second Elevator Pitch
> Most people juggle separate apps for their to-do lists, expense tracking, habit streaks, personal notes, and daily journaling—none of which talk to each other. LifeOS brings all five pillars of daily life into one cohesive, lightning-fast Android app. Everything is stored locally on your device in a private SQLite database with zero tracking. An integrated on-device AI assistant lets you ask questions about your life, extract actionable tasks from notes in seconds, and track habits and spending without compromising your privacy.

### 2-Minute Comprehensive Explanation
> In today's software landscape, personal productivity tools are fragmented and cloud-compromised. Users are forced to subscribe to cloud-based services that harvest personal diaries, financial records, and daily habits. 
> 
> LifeOS is built on the philosophy that your personal life data belongs strictly to you. Architected with modern Kotlin and Jetpack Compose, LifeOS runs an on-device Room database that persists Notes (with folder organization and AI summarization), Tasks (with priorities and due dates), Habits (with streak counters and daily logs), Expenses (with category analytics and spending metrics), and Diaries (with mood tracking and reflections). 
> 
> A unified Quick Capture sheet allows users to record any thought or metric in under 3 seconds. An on-device Timeline records major life events automatically as tasks are completed and expenses logged. The central LifeOS AI operates locally on the device to answer queries like "How much did I spend on food this month?" or "What habits am I struggling with?", while offering an optional, user-configured cloud fallback for general inquiries.

### Technical Explanation
> LifeOS is a native Android application engineered using the Model-View-ViewModel (MVVM) architecture with single-source-of-truth unidirectional data flow via Kotlin Coroutines and StateFlow. UI is built declaratively using Jetpack Compose with Material Design 3 tokens themed around the **Vibrant Palette** (Deep Purple `#6750A4` / Lavender `#EADDFF`). Local persistence is managed by Room Database 2.7.0 backed by SQLite (`lifeos_database`), featuring 8 relational entity tables and reactive Flow DAOs. 
> 
> The intelligence layer (`LifeOSAI`) features an offline heuristic NLP parser that runs on `Dispatchers.IO` directly against Room DAO queries with granular `AIPermissions` gating. For optional generative capabilities, it integrates an asynchronous REST client using OkHttp 4.10.0 to communicate with Google's Gemini 3.5 Flash model when a `GEMINI_API_KEY` is present in `BuildConfig`.

---

## 4. Target Audience

1. **Students & Academics:** Needing to organize lecture notes, assignment deadlines, study habits, and personal expenses in one place.
2. **Professionals & Knowledge Workers:** Requiring a fast, friction-free "Second Brain" to capture thoughts during meetings and extract actionable tasks.
3. **Privacy Advocates:** Individuals who refuse to store sensitive journals, health habits, and financial logs on third-party cloud servers.
4. **Productivity Enthusiasts:** Users who want to view their day holistically via progress percentages, streak charts, and unified timelines.

---

## 5. Core Philosophy & Principles

1. **Local-First & Private by Default:** User data never leaves the device unless explicitly exported by the user.
2. **Zero-Latency Capture (< 3 Seconds):** Adding a thought, note, expense, or completing a habit must be instant.
3. **Interconnected Life Pillars:** Completing a task or logging an expense creates a life event in the Timeline, linking productivity with history.
4. **No Forced Accounts:** No login walls, no mandatory remote servers, no subscription lock-in.
5. **On-Device Intelligence First:** AI features prioritize local rule-based parsing over costly and privacy-invasive cloud calls.
