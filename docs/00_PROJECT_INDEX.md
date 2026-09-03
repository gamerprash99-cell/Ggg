# LifeOS — Master Documentation Index

Welcome to the central knowledge base and technical handover suite for **LifeOS**. This documentation system serves as the definitive reference for founders, software engineers, architects, QA personnel, and future AI coding agents.

---

## 🧭 Navigation Matrix: Who Should Read What

| Reader Role | Primary Starting Point | Secondary Documents to Consult |
| :--- | :--- | :--- |
| **Non-Technical Founder / Owner** | [22_NON_TECHNICAL_FOUNDER_GUIDE.md](22_NON_TECHNICAL_FOUNDER_GUIDE.md) | [01_PROJECT_OVERVIEW.md](01_PROJECT_OVERVIEW.md), [02_PRODUCT_VISION.md](02_PRODUCT_VISION.md), [25_MASTER_HANDOVER.md](25_MASTER_HANDOVER.md) |
| **New Software Developer** | [21_DEVELOPER_HANDOVER.md](21_DEVELOPER_HANDOVER.md) | [15_BUILD_RUN_SETUP.md](15_BUILD_RUN_SETUP.md), [12_CODEBASE_MAP.md](12_CODEBASE_MAP.md), [05_ARCHITECTURE.md](05_ARCHITECTURE.md) |
| **Senior Engineer / Architect** | [05_ARCHITECTURE.md](05_ARCHITECTURE.md) | [06_DATA_FLOW.md](06_DATA_FLOW.md), [07_DATABASE.md](07_DATABASE.md), [10_AI_SYSTEM.md](10_AI_SYSTEM.md), [19_KNOWN_ISSUES_TECHNICAL_DEBT.md](19_KNOWN_ISSUES_TECHNICAL_DEBT.md) |
| **AI Coding Agent (Gemini / Claude / GPT)** | [23_AI_AGENT_CONTEXT.md](23_AI_AGENT_CONTEXT.md) | [13_FILE_BY_FILE_REFERENCE.md](13_FILE_BY_FILE_REFERENCE.md), [07_DATABASE.md](07_DATABASE.md), [04_TECH_STACK.md](04_TECH_STACK.md) |
| **QA / Test Engineer** | [16_TESTING_QA.md](16_TESTING_QA.md) | [03_FEATURE_CATALOG.md](03_FEATURE_CATALOG.md), [18_CURRENT_IMPLEMENTATION_STATUS.md](18_CURRENT_IMPLEMENTATION_STATUS.md), [17_GITHUB_CI_CD.md](17_GITHUB_CI_CD.md) |
| **Emergency Handover (Single File)** | [25_MASTER_HANDOVER.md](25_MASTER_HANDOVER.md) | Complete synthesized project state |

---

## 📚 Document Catalog & Summaries

### 01. Overview & Strategy
- **[01_PROJECT_OVERVIEW.md](01_PROJECT_OVERVIEW.md)**
  - *Purpose*: High-level summary of what LifeOS is, problem solved, target users, platform, and explanations ranging from 1 sentence to deep technical summary.
  - *When to read*: First onboarding reading for any stakeholder.

- **[02_PRODUCT_VISION.md](02_PRODUCT_VISION.md)**
  - *Purpose*: Product goals, philosophy, core rules, non-goals, and reconciliation against source PRD documents.
  - *When to read*: Prior to product decisions or roadmap adjustments.

- **[03_FEATURE_CATALOG.md](03_FEATURE_CATALOG.md)**
  - *Purpose*: Exhaustive catalog of every module (Notes, Tasks, Habits, Expenses, Diary, Captures, Timeline, AI, Backup).
  - *When to read*: To check capabilities, data entities, and implementation depth.

### 02. Architecture & Technical Foundations
- **[04_TECH_STACK.md](04_TECH_STACK.md)**
  - *Purpose*: Verified technologies, language versions, SDK targets, plugins, and libraries without guesswork.
  - *When to read*: When configuring IDE, build tools, or considering upgrades.

- **[05_ARCHITECTURE.md](05_ARCHITECTURE.md)**
  - *Purpose*: Explains the architectural layers (UI, ViewModel, Repository, Room DAO), dependency directions, and design patterns.
  - *When to read*: Before adding new features or refactoring data structures.

- **[06_DATA_FLOW.md](06_DATA_FLOW.md)**
  - *Purpose*: Step-by-step tracing of how data flows from user gestures to the SQLite disk storage and back to Compose states.
  - *When to read*: Debugging UI state anomalies or reactivity issues.

- **[07_DATABASE.md](07_DATABASE.md)**
  - *Purpose*: Full Room schema, 8 tables/entities, DAOs, queries, converters, migrations, and seeding mechanism.
  - *When to read*: When altering schema or querying local records.

- **[08_NAVIGATION.md](08_NAVIGATION.md)**
  - *Purpose*: Jetpack Navigation routes, bottom bar tabs, backstack behavior, and screen argument contracts.
  - *When to read*: Adding new screens or deep-linking.

- **[09_UI_UX.md](09_UI_UX.md)**
  - *Purpose*: Material 3 design system, Vibrant Palette token specifications, typography, cards, and accessibility rules.
  - *When to read*: Designing or styling UI composables.

### 03. Core Capabilities & Trust
- **[10_AI_SYSTEM.md](10_AI_SYSTEM.md)**
  - *Purpose*: In-depth audit of the dual-mode AI engine (on-device heuristic NLP + optional cloud Gemini fallback), prompts, permissions, and limitations.
  - *When to read*: Modifying AI behavior, query handlers, or API endpoints.

- **[11_OFFLINE_PRIVACY_SECURITY.md](11_OFFLINE_PRIVACY_SECURITY.md)**
  - *Purpose*: Strict audit of offline guarantees, zero-telemetry policy, on-device encryption/backup, PIN lock, and network boundaries.
  - *When to read*: Validating security compliance, privacy claims, or Play Store disclosures.

### 04. Codebase Reference & Operations
- **[12_CODEBASE_MAP.md](12_CODEBASE_MAP.md)**
  - *Purpose*: Complete directory tree map and directory responsibilities.
  - *When to read*: Locating source files and organizing new packages.

- **[13_FILE_BY_FILE_REFERENCE.md](13_FILE_BY_FILE_REFERENCE.md)**
  - *Purpose*: Comprehensive technical audit of every single Kotlin and configuration file in the project.
  - *When to read*: Prior to editing any existing file.

- **[14_DEPENDENCIES.md](14_DEPENDENCIES.md)**
  - *Purpose*: Catalog of all Gradle libraries, plugins, versions, and removal risks.
  - *When to read*: Modifying `libs.versions.toml` or `build.gradle.kts`.

- **[15_BUILD_RUN_SETUP.md](15_BUILD_RUN_SETUP.md)**
  - *Purpose*: Developer guide to cloning, building, running, and configuring Android Studio and CLI environments.
  - *When to read*: Day 1 setup on a new machine.

- **[16_TESTING_QA.md](16_TESTING_QA.md)**
  - *Purpose*: Audit of existing tests, Robolectric, Roborazzi screenshots, known test discrepancies, and QA checklists.
  - *When to read*: Prior to commits, pull requests, and releases.

- **[17_GITHUB_CI_CD.md](17_GITHUB_CI_CD.md)**
  - *Purpose*: GitHub Actions pipeline, triggers, keystore decoding, build artifacts, and test reporting.
  - *When to read*: Maintaining CI/CD or automated APK builds.

### 05. Status, Roadmaps & Handover
- **[18_CURRENT_IMPLEMENTATION_STATUS.md](18_CURRENT_IMPLEMENTATION_STATUS.md)**
  - *Purpose*: Feature-by-feature matrix comparing PRD expectations against actual codebase realities.
  - *When to read*: Assessing project readiness and gaps.

- **[19_KNOWN_ISSUES_TECHNICAL_DEBT.md](19_KNOWN_ISSUES_TECHNICAL_DEBT.md)**
  - *Purpose*: Prioritized backlog of technical debt, bugs, template residue, and security warnings (P0 to P3).
  - *When to read*: Sprint planning and bug-fixing phases.

- **[20_ROADMAP_REMAINING_WORK.md](20_ROADMAP_REMAINING_WORK.md)**
  - *Purpose*: Phased development roadmap for post-MVP and production release.
  - *When to read*: Product planning.

- **[21_DEVELOPER_HANDOVER.md](21_DEVELOPER_HANDOVER.md)**
  - *Purpose*: Standalone developer onboarding guide for a new engineer stepping in today.
  - *When to read*: Essential onboarding.

- **[22_NON_TECHNICAL_FOUNDER_GUIDE.md](22_NON_TECHNICAL_FOUNDER_GUIDE.md)**
  - *Purpose*: Plain-English guide for founders, explaining architecture, database, offline nature, and how to pitch the tech stack.
  - *When to read*: Founder reference, investor meetings, and interviews.

- **[23_AI_AGENT_CONTEXT.md](23_AI_AGENT_CONTEXT.md)**
  - *Purpose*: System prompt context and strict rules for future LLM coding agents modifying this repository.
  - *When to read*: Injected into prompt contexts of future AI workflows.

- **[24_CHANGELOG.md](24_CHANGELOG.md)**
  - *Purpose*: Chronological record of architectural changes, styling updates, migrations, and CI/CD additions.
  - *When to read*: Reviewing version history.

- **[25_MASTER_HANDOVER.md](25_MASTER_HANDOVER.md)**
  - *Purpose*: The single-file emergency handover containing the essence of all 24 documents.
  - *When to read*: When you have time to read only one file.
