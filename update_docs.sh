#!/bin/bash
cat << 'MD' > docs/PROJECT_HANDOVER.md
# LifeOS - Project Handover

## Overview
LifeOS is an offline-first "Second Brain" Android app built with Kotlin, Jetpack Compose, and Room. It functions fully locally without an internet connection for core features.

## What is Working (Verified)
- **Quick Capture:** Thought, Photo, Video, Audio fully capture and persist locally using MediaStore and internal app storage.
- **Timeline:** Aggregates Diary, Notes, Thoughts, Media, Tasks, and Habits with correct ordering and rendering.
- **Database (Room):** Completely offline CRUD functionality using Room database. Hardcoded dummy data removed.
- **Habits & Tasks:** Functional creation, editing, deletion, streak tracking, and local Android `AlarmManager` notifications.
- **Security:** App Lock uses `androidx.biometric.BiometricPrompt` with device credential/PIN fallback.
- **Expenses:** CRUD operations working, computing live statistics without dummy numbers.

## Remaining Technical Debt
- **Video Playback:** Relies on the default system intent (`ACTION_VIEW`). Future integrations should implement a native ExoPlayer surface.
- **Background Restore:** Heavy Room database migrations or large media restores run on standard Coroutines; scaling may require WorkManager.

MD

cat << 'MD' > docs/FOUNDER_GUIDE.md
# Founder's Guide to LifeOS

Welcome to LifeOS! This guide is written in plain language to help non-technical founders understand the architecture, data flow, and technology stack of LifeOS.

## What is LifeOS?
LifeOS is built purely as a native Android application using **Kotlin** (the official programming language for Android) and **Jetpack Compose** (a modern toolkit for building user interfaces).

## Core Questions Answered
**Where is user data stored?**
Everything is stored directly on the user's physical device. We use **Room**, which is a local database system (SQLite under the hood) built by Google. No data leaves the device unless explicitly authorized (e.g. asking the AI Assistant a question).

**Does LifeOS require internet?**
No! LifeOS is **Offline-First**. The user can take photos, record voice memos, create habits, log expenses, and lock the app without any Wi-Fi or cellular connection.

**How does Photo/Video/Audio Capture work?**
- **Photos/Videos:** We open the Android Camera. When the picture is taken, we copy the file into LifeOS's private internal storage and save the file path in the Room database.
- **Audio:** We use Android's `MediaRecorder` to save an MP4 audio file privately, and track it just like a photo.

**How does App Lock work?**
It uses Android's built-in **BiometricPrompt**. This means it leverages the hardware fingerprint scanner or Face ID. If that fails, it falls back to a device PIN.

**How does a Notification get scheduled?**
When the user sets a reminder (e.g., "Drink Water at 8:00 AM"), the app tells the Android operating system (using `AlarmManager`) to wake up the app exactly at that time and deliver a notification, entirely offline.

## How to Continue Development
Any Android developer can open this project in **Android Studio** and click "Run". The `app/build.gradle.kts` file contains all the libraries we use. Start by looking at `MainActivity.kt` and the `com.example.ui.screens` folder.

MD

chmod +x update_docs.sh
./update_docs.sh
