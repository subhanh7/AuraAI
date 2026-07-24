# AI Assistant — Android Architecture

MVVM + Clean Architecture · Jetpack Compose · Kotlin Coroutines · Room · DataStore · WorkManager

---

## 1. Layered Architecture

```mermaid
graph TB
    subgraph PRES["🔵 PRESENTATION LAYER"]
        direction LR
        P1[Compose Screens<br/>Onboarding · Home · Chat]
        P2[Reusable Components<br/>AuraCircle · InputPanel]
        P3[Navigation<br/>NavHost · NavGraph]
        P4[ViewModels<br/>StateFlow exposers]
    end

    subgraph DOM["🟣 DOMAIN LAYER"]
        direction LR
        D1[Models<br/>UserProfile · ChatMessage]
        D2[Repository Interfaces]
        D3[Use Cases<br/>SendMessage · SyncData]
        D4[State Machine<br/>ConversationStateMachine]
    end

    subgraph DATA["🟢 DATA LAYER"]
        direction LR
        A1[Repository Impl]
        A2[Room DB<br/>DAO + Entities]
        A3[DataStore<br/>UserProfile prefs]
        A4[TypeConverters]
        A5[WorkManager]
        A6[AudioRecorder]
    end

    subgraph INFRA["🟠 INFRASTRUCTURE LAYER"]
        direction LR
        I1[Canvas Renderer<br/>AuraCircle drawing engine]
        I2[Sync Engine<br/>SyncManager]
        I3[Utilities<br/>Validators · Mappers]
    end

    PRES -->|calls| DOM
    DOM -->|implemented by| DATA
    DATA -->|delegates to| INFRA

    style PRES fill:#E6F1FB,stroke:#185FA5,color:#042C53
    style DOM fill:#EEEDFE,stroke:#534AB7,color:#26215C
    style DATA fill:#EAF3DE,stroke:#3B6D11,color:#173404
    style INFRA fill:#FAEEDA,stroke:#854F0B,color:#412402
```

**Dependency rule:** Presentation → Domain ← Data. Domain has zero Android/framework imports. Data implements Domain's repository interfaces (dependency inversion). Infrastructure is consumed only by Data.

---

## 2. Folder Structure

```
app/
├── presentation/
│   ├── onboarding/
│   │   ├── OnboardingScreen.kt          # HorizontalPager, 3 steps
│   │   ├── ValuePropStep.kt
│   │   ├── ProfileFormStep.kt           # Name, Age, Phone, OTP
│   │   ├── PersonalityStep.kt           # exactly-3-traits selector
│   │   └── OnboardingViewModel.kt       # holds OnboardingUiState, back-nav restore
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   ├── components/
│   │   │   ├── AuraCircle.kt            # reusable Canvas composable
│   │   │   ├── InputPanel.kt            # animated bottom sheet
│   │   │   └── MicAmplitudeListener.kt
│   │   └── HomeViewModel.kt
│   ├── chat/
│   │   ├── ChatScreen.kt                # LazyColumn + Paging3
│   │   ├── ChatBubble.kt
│   │   └── ChatViewModel.kt
│   ├── navigation/
│   │   ├── NavGraph.kt
│   │   └── Destinations.kt
│   └── components/                      # shared: buttons, loaders, dialogs
│
├── domain/
│   ├── model/
│   │   ├── UserProfile.kt
│   │   ├── ChatMessage.kt
│   │   ├── MessageMeta.kt
│   │   └── Reminder.kt
│   ├── repository/
│   │   ├── UserRepository.kt            # interface
│   │   ├── ChatRepository.kt            # interface
│   │   └── SyncRepository.kt            # interface
│   ├── usecase/
│   │   ├── SaveUserProfileUseCase.kt
│   │   ├── VerifyOtpUseCase.kt
│   │   ├── SendMessageUseCase.kt
│   │   ├── GetPagedMessagesUseCase.kt
│   │   └── TriggerSyncUseCase.kt
│   └── statemachine/
│       ├── ConversationState.kt         # sealed class
│       └── ConversationStateMachine.kt
│
├── data/
│   ├── repository/
│   │   ├── UserRepositoryImpl.kt
│   │   ├── ChatRepositoryImpl.kt
│   │   └── SyncRepositoryImpl.kt
│   ├── local/
│   │   ├── room/
│   │   │   ├── AppDatabase.kt
│   │   │   ├── dao/
│   │   │   │   ├── UserProfileDao.kt
│   │   │   │   ├── ChatMessageDao.kt
│   │   │   │   └── ReminderDao.kt
│   │   │   ├── entity/
│   │   │   │   ├── UserProfileEntity.kt
│   │   │   │   ├── ChatMessageEntity.kt
│   │   │   │   └── ReminderEntity.kt
│   │   │   └── converter/
│   │   │       └── MessageMetaConverter.kt
│   │   └── datastore/
│   │       └── UserProfileDataStore.kt
│   ├── audio/
│   │   └── AudioRecorderImpl.kt         # AudioRecord wrapper, emits amplitude Flow
│   └── worker/
│       └── SyncWorker.kt                # WorkManager CoroutineWorker
│
└── infrastructure/
    ├── canvas/
    │   └── AuraRenderer.kt              # breathing / listening draw logic
    ├── sync/
    │   └── SyncManager.kt               # conflict resolution, lastSyncedAt logic
    └── util/
        ├── Validators.kt                # phone/age/OTP validation
        └── Mappers.kt                   # Entity <-> Domain mapping
```

---

## 3. Core Data Flow

```mermaid
flowchart LR
    U[User] --> C[Composable]
    C --> VM[ViewModel]
    VM --> R[Repository]
    R --> RM[(Room)]
    R --> DS[(DataStore)]

    style U fill:#F1EFE8,stroke:#5F5E5A,color:#2C2C2A
    style C fill:#E6F1FB,stroke:#185FA5,color:#042C53
    style VM fill:#E6F1FB,stroke:#185FA5,color:#042C53
    style R fill:#EAF3DE,stroke:#3B6D11,color:#173404
    style RM fill:#EAF3DE,stroke:#3B6D11,color:#173404
    style DS fill:#EAF3DE,stroke:#3B6D11,color:#173404
```

ViewModel never touches Room/DataStore directly — it calls a Domain use case, which calls the Repository interface. Repository impl (Data layer) decides Room vs DataStore vs both.

---

## 4. Onboarding Flow

```mermaid
stateDiagram-v2
    [*] --> ValueProp: Step 1
    ValueProp --> ProfileForm: swipe →
    ProfileForm --> OtpVerify: submit Name/Age/Phone
    OtpVerify --> ProfileForm: invalid OTP (retry)
    OtpVerify --> Personality: OTP == 1234
    Personality --> Personality: select trait (max 3)
    Personality --> Saved: exactly 3 selected → Save
    Saved --> [*]: UserProfile → DataStore

    ProfileForm --> ValueProp: swipe back (state restored)
    Personality --> ProfileForm: swipe back (state restored)
```

`OnboardingViewModel` holds a single `OnboardingUiState` (all 3 steps' fields + `PagerState`). Back navigation just moves the pager — no field is cleared, so partially filled forms persist across swipes until the whole `UserProfile` is committed to DataStore at the end.

---

## 5. Audio Flow — Aura Circle

```mermaid
flowchart LR
    AR["AudioRecord<br/>raw PCM buffer"] --> AMP["Amplitude<br/>calculator (RMS)"]
    AMP --> SF["StateFlow&lt;Float&gt;"]
    SF --> AURA["AuraCircle<br/>(Canvas)"]

    style AR fill:#FAEEDA,stroke:#854F0B,color:#412402
    style AMP fill:#FAEEDA,stroke:#854F0B,color:#412402
    style SF fill:#E6F1FB,stroke:#185FA5,color:#042C53
    style AURA fill:#E6F1FB,stroke:#185FA5,color:#042C53
```

- **Idle:** `AuraCircle` runs an `infiniteRepeatable` scale/alpha animation (breathing), independent of audio.
- **Listening:** `AudioRecorderImpl` (Data/audio) reads the mic buffer on a background coroutine, computes RMS amplitude, emits it via `StateFlow<Float>`. `AuraCircle` collects this flow and maps amplitude → radius/glow in its `Canvas` `onDraw` block. Rendering math itself lives in `infrastructure/canvas/AuraRenderer.kt` so the composable stays declarative.

---

## 6. Conversation State Machine

```mermaid
stateDiagram-v2
    [*] --> Idle
    Idle --> Typing: user input
    Typing --> Validating: debounce/submit
    Validating --> Processing: valid
    Validating --> Idle: invalid
    Processing --> Responding: result ready
    Processing --> Error: 8s timeout
    Responding --> Idle: displayed
    Error --> Processing: Retry
    Error --> Idle: dismiss

    Typing --> Typing: new message\n(cancel + restart)
    Processing --> Typing: new message\n(cancel job + restart)
```

```kotlin
sealed class ConversationState {
    object Idle : ConversationState()
    data class Typing(val draft: String) : ConversationState()
    object Validating : ConversationState()
    data class Processing(val job: Job) : ConversationState()
    data class Responding(val message: ChatMessage) : ConversationState()
    data class Error(val cause: Throwable, val lastInput: String) : ConversationState()
}
```

Implemented as a class wrapping a `MutableStateFlow<ConversationState>`. Each new message calls `currentJob?.cancel()` before launching a fresh coroutine, and `Processing` is wrapped in `withTimeout(8_000)` — a `TimeoutCancellationException` transitions straight to `Error`, which exposes a `retry()` function that re-enters `Processing` with the same payload.

---

## 7. Room Schema

```mermaid
erDiagram
    USER_PROFILE {
        string id PK
        string name
        int age
        string phone
        string traits
        long lastSyncedAt
    }
    CHAT_MESSAGE {
        string id PK
        string sender
        string content
        long timestamp
        string messageMeta
        long lastSyncedAt
    }
    REMINDER {
        string id PK
        string userId FK
        string title
        long triggerAt
        long lastSyncedAt
    }
    USER_PROFILE ||--o{ REMINDER : owns
```

`ChatMessageEntity.messageMeta` is stored as JSON text via `MessageMetaConverter : TypeConverter` (serializes `MessageMeta(intent, confidence, attachments)` with kotlinx.serialization). Every entity carries `lastSyncedAt` for the offline-first sync strategy below.

---

## 8. Offline-First Sync Flow

```mermaid
flowchart LR
    RM[(Room<br/>dirty rows)] --> WM[WorkManager<br/>NetworkType.CONNECTED]
    WM --> RS["Remote Sync<br/>(future API)"]
    RS -.conflict.-> LW["Local wins<br/>discard remote"]

    style RM fill:#EAF3DE,stroke:#3B6D11,color:#173404
    style WM fill:#EAF3DE,stroke:#3B6D11,color:#173404
    style RS fill:#FAEEDA,stroke:#854F0B,color:#412402
    style LW fill:#F1EFE8,stroke:#5F5E5A,color:#2C2C2A
```

`SyncManager` (Infrastructure):
1. `SyncWorker` (CoroutineWorker) is constrained to `NetworkType.CONNECTED`, enqueued via `WorkManager` as periodic + on-demand.
2. Queries only rows where `lastSyncedAt < updatedAt` (dirty rows) — never a full-table push.
3. On conflict, local data always wins; remote response is merged only for fields absent locally.
4. Exposes `StateFlow<SyncStatus>` (`Idle / Syncing / Success / Failed`) that `SyncRepositoryImpl` forwards up through Domain to any ViewModel that wants a sync indicator.

---

## 9. Navigation Flow

```mermaid
flowchart TD
    Splash --> Onboarding
    Onboarding -->|profile saved| Home
    Home -->|keyboard tap| InputPanel[Bottom Input Panel]
    Home -->|swipe up, parallax| Chat
    Chat -->|swipe down| Home
```

---

## Tech Stack Summary

| Concern | Choice |
|---|---|
| UI | Jetpack Compose, Canvas API |
| Async | Kotlin Coroutines, Flow, StateFlow |
| DI | Hilt |
| Local DB | Room (+ Paging 3) |
| Prefs | DataStore (Proto/Preferences) |
| Background sync | WorkManager |
| Audio | AudioRecord (raw PCM → RMS amplitude) |
| Architecture | MVVM + Clean Architecture, 4-layer |
