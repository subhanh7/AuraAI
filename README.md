# AuraAI — Modern Android Assistant App

A high-performance Android application built with **Jetpack Compose**, **Clean Architecture**, **MVVM**, **Hilt**, and **AGSL RuntimeShaders**.

---

## 🌟 Overview

AuraAI delivers a fluid, real-time voice and text chat experience powered by a procedural, GPU-accelerated AGSL energy shader. The app follows modern Android development best practices, featuring offline persistence, clean state management, and custom glassmorphism design components.

---

## 🛠 Tech Stack & Architecture

- **Architecture:** MVVM + Clean Architecture (Presentation, Domain, Data, Infrastructure)
- **UI:** Jetpack Compose, Material 3, Custom Glassmorphism System
- **Shader Engine:** AGSL `RuntimeShader` (Procedural 3D Energy Ribbons)
- **Dependency Injection:** Hilt / Dagger
- **Concurrency & State:** Kotlin Coroutines & `StateFlow`
- **Audio Engine:** `AudioRecord` (Real-time PCM RMS Amplitude Processing)
- **Persistence:** Room Database & Preferences DataStore
- **Background Sync:** WorkManager

---

## 📁 Project Structure

```
com.example.auraai/
├── data/              # Room DB, DataStore, AudioRecorder, Repositories
├── di/                # Hilt Modules
├── domain/            # Models, Repository Interfaces, State Machine
├── infrastructure/    # Sync Manager, Background Workers
├── presentation/      # UI Layer (Compose Screens, ViewModels, Components)
└── theme/             # Design Tokens, Color Palette, Typography
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Jellyfish or newer
- JDK 17
- Android SDK 34 (Minimum SDK 33 for AGSL RuntimeShader)

### Build & Run

```bash
./gradlew installDebug
```

---

## 📄 License

Copyright (c) 2026. All rights reserved.
