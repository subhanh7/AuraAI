# AuraAI – Android AI Assistant

AuraAI is a modern Android AI assistant prototype built using **Kotlin** and **Jetpack Compose** as part of the **KaStack Labs Android Developer Internship Assignment**.

The project demonstrates modern Android architecture, offline-first data management, coroutine-based state handling, custom UI components, and local persistence while maintaining a clean and scalable codebase.

---

# Project Overview

AuraAI provides an intelligent assistant experience through a modern UI featuring:

- Swipeable onboarding
- Persistent user profile
- Animated assistant aura
- Voice interaction
- Local chat history
- Offline-first architecture
- Coroutine-based conversation pipeline

The application follows Google's recommended Android development practices using MVVM, StateFlow, Room, DataStore, WorkManager, and Hilt.

---

# Features

## Part 1 – Onboarding

- Three-step swipeable onboarding
- Animated value proposition reveal
- User profile collection
- Mock OTP verification (1234)
- Personality trait selection
- Validation before proceeding
- Data persistence using DataStore
- State restoration on back navigation

---

## Part 2 – Home Screen

### Aura Assistant

- Animated Aura component
- Idle breathing animation
- Voice-reactive microphone state
- Reusable Compose component

### Chat Experience

- Glassmorphic interface
- Animated input panel
- Chat history
- Room database persistence
- Pagination support

---

## Part 3 – Coroutine State Machine

Every conversation follows the pipeline:

```
Typing
    ↓
Validating
    ↓
Processing
    ↓
Responding
    ↓
Idle
```

Features include:

- StateFlow
- Sealed Classes
- Coroutine cancellation
- Timeout handling
- Retry support

---

## Part 4 – Offline First Architecture

Local storage is implemented using Room Database.

Stored entities:

- UserProfile
- ChatMessage
- Reminder

Additional features:

- Flow-based DAO queries
- MessageMeta TypeConverter
- WorkManager synchronization
- Network-aware sync
- Local conflict resolution

---

# Architecture

The application follows MVVM with Clean Architecture principles.

```
Presentation (Jetpack Compose)
        │
        ▼
ViewModels
        │
        ▼
Domain Layer
        │
        ▼
Repository
        │
        ▼
Data Layer
(Room + DataStore + Audio + Workers)
```

Project structure:

```
app/
├── data/
│   ├── audio/
│   ├── local/
│   ├── repository/
│   └── worker/
│
├── di/
│
├── domain/
│   ├── model/
│   ├── repository/
│   └── statemachine/
│
├── infrastructure/
│   └── sync/
│
├── presentation/
│   ├── onboarding/
│   ├── home/
│   ├── navigation/
│   └── components/
│
└── theme/
```

---

# Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- MVVM
- Clean Architecture
- Kotlin Coroutines
- StateFlow
- Room Database
- Preferences DataStore
- WorkManager
- Hilt Dependency Injection
- AudioRecord
- Canvas-based custom UI animations

---

# Data Persistence

User data is persisted using:

- Preferences DataStore
- Room Database

The application restores user state automatically after relaunch without losing onboarding progress.

---

# Offline Sync

Synchronization is handled using WorkManager.

Features:

- Runs only when network is available
- Syncs changed records
- Local conflict resolution
- Observable sync state

---

# Unit Tests

The project includes unit tests for the conversation state machine.

Covered scenarios:

- Happy Path
- Cancellation Mid-Flow
- Processing Timeout → Error State

Unit test results:

```
docs/UnitTestResults.png
```

---

# System Design

The repository contains a system design document describing how user history, settings, and persona can be migrated securely between devices.

Location:

```
docs/AI-Assistant-Architecture.md
```

---

# Demo Instructions

### Mock OTP

```
1234
```

### Reset Application

```bash
adb shell pm clear com.example.auraai
```

---

# Getting Started

## Requirements

- Android Studio (Latest Stable)
- JDK 17+
- Android SDK

## Clone Repository

```bash
git clone https://github.com/subhanh7/AuraAI.git
```

## Build

```bash
./gradlew build
```

## Run

Open the project in Android Studio and run it on an emulator or physical Android device.

---

# Screenshots

Add screenshots inside:

```
docs/screenshots/
```

Suggested screenshots:

- Onboarding
- Personality Selection
- Home Screen
- Chat Screen
- Aura Animation

---

# Future Improvements

- AI backend integration
- Cloud synchronization
- Secure backup and restore
- Voice assistant enhancements
- Multi-device synchronization
- End-to-end encrypted user data

---

# Repository Contents

```
AuraAI
│
├── app/
├── docs/
│   ├── AI-Assistant-Architecture.md
│   └── UnitTestResults.png
├── gradle/
├── README.md
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew
```

---

# Author

**Mohammed Subhan**

B.E. Computer Science & Engineering
