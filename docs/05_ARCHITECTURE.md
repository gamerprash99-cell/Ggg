# LifeOS — System Architecture

## 1. High-Level Architecture Overview

LifeOS is structured as a **Clean Architecture MVVM (Model-View-ViewModel)** system built entirely on unidirectional data flow (UDF) principles.

```
       ┌─────────────────────────────────────────────────────────┐
       │                       PRESENTATION                      │
       │  Jetpack Compose Composables (HomeScreen, Notes, Life)  │
       └─────────────────────────▲───────────────────────────────┘
                                 │ Observes StateFlow (UI State)
                                 │ Dispatches User Actions (Events)
       ┌─────────────────────────┴───────────────────────────────┐
       │                        VIEWMODEL                        │
       │  HomeViewModel, NotesViewModel, InsightsVM, LifeVM     │
       └─────────────────────────▲───────────────────────────────┘
                                 │ Exposes Flow<List<Entity>>
                                 │ Calls suspend functions (CRUD)
       ┌─────────────────────────┴───────────────────────────────┐
       │                       REPOSITORY                        │
       │             LifeOSRepository (Single Source of Truth)   │
       └───────────────────▲───────────────────▲─────────────────┘
                           │                   │
         Interacts directly│                   │ Interacts with AI
                           ▼                   ▼
       ┌───────────────────────┐   ┌─────────────────────────────┐
       │     DATA ACCESS       │   │      INTELLIGENCE LAYER     │
       │   8 Room DAOs (SQLite)│   │  LifeOSAI (Local NLP Heuristics│
       └───────────▲───────────┘   │  + Optional Cloud Gemini)   │
                   │               └─────────────────────────────┘
                   ▼
       ┌───────────────────────┐
       │   LOCAL PERSISTENCE   │
       │ Room Database Engine  │
       │  ("lifeos_database")  │
       └───────────────────────┘
```

---

## 2. Architecture Explained for Non-Technical Stakeholders

Think of LifeOS like a high-end restaurant:
1. **The Dining Room (UI / Compose Screens):** This is what the customer sees. It is beautiful, responsive, and easy to interact with. When a customer orders a meal, they don't go into the kitchen themselves.
2. **The Waiter (ViewModel):** The waiter takes the customer's order, brings it back to the kitchen, and waits for updates. When the meal is ready, the waiter brings it back to the table in a clean format. If anything changes, the waiter notifies the customer immediately.
3. **The Head Chef (LifeOSRepository):** The head chef manages all the ingredients and oversees all recipes. Whether an order is for notes, tasks, or financial records, the head chef knows exactly which kitchen station to call.
4. **The Kitchen Pantries (Room Database DAOs):** This is where all food and recipes are securely locked away in the building. Nobody from the outside can access the pantry directly; only the chefs can open it. Because the pantry is inside the building, the restaurant functions perfectly even if the city's power or internet is out.

---

## 3. Technical Architecture for Engineers

### 3.1 Layer Responsibilities

#### Presentation Layer (`com.example.ui`)
- **Composables:** Stateless and state-observant Composable functions. They receive an immutable data class (e.g., `HomeUiState`, `NotesUiState`) and render UI accordingly.
- **User Gestures:** Interactive components emit event callbacks up to their parent Composable or ViewModel (e.g., `viewModel.toggleTask(task)`).
- **No Direct DB Access:** Composables are strictly forbidden from directly touching Room DAOs or `AppDatabase`.

#### ViewModel Layer (`com.example.ui.screens.*`)
- **State Holders:** ViewModels inherit from AndroidX `ViewModel`.
- **Flow Combination:** ViewModels leverage Kotlin Coroutine operators like `combine()` to merge multiple continuous Room DAO flows into a single unified `StateFlow`.
- **Lifecycle Optimization:** UI state flows are started using `stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = ...)`. The 5-second timeout preserves database connection resources when the app goes into the background while avoiding recomposition flicker on screen rotation.

#### Repository Layer (`com.example.data.repository.LifeOSRepository`)
- **Single Source of Truth:** Centralizes business logic across all 8 modules.
- **Cross-Module Integration:** When a task is marked complete or an expense is logged, `LifeOSRepository` automatically creates a corresponding `LifeEventEntity` record in the timeline, maintaining system cohesion.
- **Data Serialization:** Coordinates backup export to JSON and data restoration into Room.

#### Data Access & Persistence Layer (`com.example.data.local`)
- **Room SQLite Engine:** Version 2.7.0. Provides compile-time verification of SQL queries via KSP.
- **DAOs:** Expose reactive `Flow<List<T>>` for read operations, guaranteeing that any database write instantly triggers an update throughout the UI without manual refresh calls.
- **Suspend Functions:** Write operations (`insert`, `update`, `delete`) are `suspend` functions executed off the main thread.

---

## 4. Dependency Injection & Service Locator Strategy

LifeOS implements a lightweight, high-performance **Application Service Locator pattern**:
- In `LifeOSApp.kt` (`Application` subclass):
  ```kotlin
  class LifeOSApp : Application() {
      lateinit var repository: LifeOSRepository
          private set

      override fun onCreate() {
          super.onCreate()
          instance = this
          val database = AppDatabase.getDatabase(this)
          repository = LifeOSRepository(database)
      }

      companion object {
          lateinit var instance: LifeOSApp
              private set
          val repo: LifeOSRepository get() = instance.repository
      }
  }
  ```
- ViewModels inject this repository via constructor default arguments:
  ```kotlin
  class HomeViewModel(
      private val repository: LifeOSRepository = LifeOSApp.repo
  ) : ViewModel()
  ```
- **Architectural Benefits:**
  - Zero cold-start annotation processing or reflection overhead (unlike heavy DI frameworks).
  - Effortless unit testing: tests can pass a mock or in-memory repository directly to the ViewModel constructor without setting up Dagger/Hilt test components.
