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

<img width="1512" height="982" alt="Screenshot 2026-07-25 at 12 30 22 AM" src="https://github.com/user-attachments/assets/8ffd6eb1-608c-49ae-8a35-c5aea65c6d53" />

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

# 📸 Screenshots

## Onboarding

<img width="629" height="866" alt="Screenshot 2026-07-24 at 8 45 55 PM" src="https://github.com/user-attachments/assets/49bfcefb-9af7-480a-a571-26b5214de1e1" />

---

## Personality Selection

<img width="617" height="861" alt="Screenshot 2026-07-24 at 8 46 33 PM" src="https://github.com/user-attachments/assets/1194b444-7a2a-4632-87fb-500606d3aed6" />

<img width="617" height="853" alt="Screenshot 2026-07-24 at 8 46 47 PM" src="https://github.com/user-attachments/assets/bf75496f-78cd-47e3-a93c-0ff0f1258cf0" />

---

## Home Screen

<img width="684" height="874" alt="Screenshot 2026-07-24 at 8 47 08 PM" src="https://github.com/user-attachments/assets/71e14284-2b62-4f06-84cd-faebf299aba1" />

---

## Chat Screen

<img width="592" height="854" alt="Screenshot 2026-07-24 at 8 47 21 PM" src="https://github.com/user-attachments/assets/2130fe9d-7476-4960-bacf-700747a179c2" />

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
