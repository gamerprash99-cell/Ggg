# LifeOS — Project Changelog & Evolution History

This document records the architectural, design, and functional iterations of LifeOS.

---

## [1.0.0-PROD-DOCS] — 2026-09-03
### Added
- **Master Documentation Suite:** Generated a comprehensive 25-file technical and operational knowledge base under `/docs/` covering architecture, data flow, database schema, AI systems, testing, CI/CD, and handover guides.
- **GitHub Actions CI/CD Pipeline:** Created `.github/workflows/android.yml` automating JDK 17 setup, debug keystore decoding, unit testing via Robolectric, APK assembly, and artifact archiving.
- **Automated Build Validation:** Verified clean compilation under Android SDK 36, AGP 9.1.1, Kotlin 2.2.10, and Jetpack Compose BOM 2024.09.00.

---

## [0.9.5-BETA] — 2026-09-02
### Changed
- **Vibrant Palette Design System Overhaul:**
  - Applied the modern Material 3 Vibrant Palette styling across all screens.
  - Updated primary brand token to Deep Purple (`#6750A4`) with high-contrast Lavender containers (`#EADDFF`).
  - Implemented soft eye-friendly canvas (`#F7F2FA`), high-luminance surface containers (`#FEF7FF`), and subtle structural borders (`#CAC4D0`).
  - Retuned the bottom navigation bar (`LifeOSBottomBar`) with an elevated center Quick Capture action button.

---

## [0.9.0-ALPHA] — 2026-09-01
### Added
- **Dual-Mode AI Engine (`LifeOSAI.kt`):**
  - Integrated on-device heuristic query parser for tasks, expenses, habits, notes, and diary entries.
  - Implemented regex-based smart task extraction from notes with candidate review modal.
  - Implemented automatic note summarizer and thought-to-journal refinement tool.
  - Added optional cloud Gemini 3.5 Flash REST fallback via OkHttp when an API key is configured.
- **Cross-Module Life Timeline:**
  - Added automatic `LifeEventEntity` recording in `LifeOSRepository` whenever tasks are marked complete, habits checked in, or expenses logged.
- **Full Database Backup Engine:**
  - Implemented JSON serialization and deserialization in `LifeOSRepository` for full offline export and restore.

---

## [0.1.0-INIT] — Initial Scaffolding
### Added
- Initial project scaffolding using Jetpack Compose, Room Database 2.7.0, and AndroidX Navigation.
- Set up 8 core SQLite entities: `NoteEntity`, `TaskEntity`, `HabitEntity`, `HabitLogEntity`, `ExpenseEntity`, `DiaryEntity`, `CaptureEntity`, and `LifeEventEntity`.
- Established `LifeOSApp` singleton repository pattern and initial database seeding on first launch.
