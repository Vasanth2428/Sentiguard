# SPEC-1-Sentiguard

## Background

Manual scavengers in India operate in extremely hazardous environments such as septic tanks and sewers, facing toxic gases and oxygen deprivation. Fatalities are frequent, with limited access to affordable safety equipment. Even when deaths occur, families often fail to receive legally mandated compensation due to lack of evidence proving hazardous working conditions.

Sentiguard is conceived as a **mobile-only, offline-first digital life-shield** that leverages sensors already present in low-cost smartphones to:

* Detect early physiological danger signals
* Warn the worker in real time
* Securely record verifiable evidence for family and legal protection

The system intentionally avoids expensive hardware, internet dependency, and complex ML models to maximize accessibility, reliability, and trust.

---

## Requirements (MoSCoW)

### Must Have

* Offline Android mobile application
* Background cough monitoring using microphone
* Rule-based cough risk detection (frequency + intensity thresholds)
* Manual nail color check using camera (cyanosis detection via HSV/RGB rules)
* Immediate audio + vibration alerts on detected risk
* GPS location logging with timestamps
* Local, tamper-resistant evidence storage (audio + metadata)
* Works on low-end smartphones without internet

### Should Have

* Session-based monitoring (start/stop work sessions)
* Evidence log viewer for family or advocates
* Configurable alert intensity (sound/vibration)
* Preloaded emotional support / guidance audio

### Could Have

* Data export (read-only) via USB for legal aid
* Multi-language support (regional languages)
* Simple PIN-based app lock

### Won’t Have (MVP)

* Cloud sync or backend server
* Machine learning models
* Wearable or external hardware integration

## Method

### Design Philosophy

Sentiguard follows a **production-grade, offline-first architecture** designed to scale from a hackathon MVP to a nationally deployable safety system without requiring rewrites.

**Core principles**:

* Safety logic must never depend on network availability
* Evidence must be verifiable and tamper-evident
* Architecture must allow features to be enabled/disabled without refactoring
* MVP implements core flows; advanced capabilities are stubbed but designed

A **Clean Architecture** approach is used:

* Presentation Layer (UI)
* Domain Layer (Detection & Rules)
* Data Layer (Repositories & Storage)
* System Layer (Sensors, OS services)

---

### High-Level Architecture

**Components**:

* UI Module (Activities / Compose / Flutter Widgets)
* Monitoring Orchestrator (Session lifecycle)
* Cough Detection Engine (Audio thresholds)
* Nail Color Analyzer (HSV/RGB rules)
* Alert Manager (Sound + Vibration)
* Evidence Recorder (Audio, GPS, metadata)
* Evidence Integrity Module (Hash chaining)
* Local Data Store (SQLite + encrypted files)
* Export & Sync Adapter (disabled in MVP)

---

### Background Services Design

* **Foreground Service** for monitoring session (Android requirement)
* Watchdog timer ensures service restarts if killed
* Battery-safe polling intervals
* Graceful degradation if permissions are revoked

**Lifecycle**:

1. User starts session
2. Foreground service initiated
3. Microphone + GPS listeners activated
4. Detection engines run on sliding windows
5. Alerts triggered immediately on risk
6. Evidence persisted atomically

---

### Detection Algorithms (Rule-Based)

#### Cough Detection

* Audio sampled at low bitrate
* Sliding window FFT
* Thresholds:

  * Sound intensity (dB)
  * Frequency band associated with coughs
  * Repetition rate within time window
* Escalation levels (warning → danger)

#### Nail Color Detection

* Manual capture before entry
* ROI extraction on nail region
* HSV/RGB filtering
* Cyanosis detection via blue channel dominance
* Result stored as signed evidence

---

### Evidence Pipeline (Production-Grade)

Each monitoring session generates an **Evidence Chain**:

* Session ID (UUID)
* Event records (JSON)
* Audio snippets
* GPS + timestamp
* Hash of previous record (SHA-256)

This forms an **append-only hash chain**.

If any record is altered, integrity validation fails.

---

### Data Storage Design

**SQLite Tables**:

* `sessions(session_id, start_time, end_time, device_id)`
* `events(event_id, session_id, type, timestamp, prev_hash, hash)`
* `gps_logs(id, session_id, lat, lon, timestamp)`
* `audio_refs(id, session_id, file_path, hash)`

**File Storage**:

* Encrypted app directory
* Rolling storage quotas
* Old evidence never overwritten

---

### Export & Verification (Stubbed in MVP)

* Evidence packaged as:

  * ZIP archive
  * Manifest JSON
  * Integrity hashes
* Read-only verification tool planned (NGO / Legal)
* No cloud dependency in MVP

---

### Hackathon Scope vs Production Scope

**Implemented in Hackathon**:

* Detection engines
* Alerts
* Session logging
* Hash chaining
* Evidence viewer

**Designed but Stubbed**:

* Sync adapters
* Verification tooling
* Advanced security (PKI)
* Government dashboards

This ensures demo realism without overengineering.

---

## Implementation

### Recommended Project Structure (Android – Kotlin, Production-Grade)

This structure follows **Clean Architecture + Android best practices**, while remaining hackathon-friendly.

```
Sentiguard/
├── app/
│   ├── src/main/
│   │   ├── java/com/sentiguard/app/
│   │   │   ├── SentiguardApp.kt
│   │   │   │
│   │   │   ├── ui/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── dashboard/
│   │   │   │   │   ├── DashboardActivity.kt
│   │   │   │   │   ├── DashboardViewModel.kt
│   │   │   │   │   └── DashboardState.kt
│   │   │   │   ├── nailcheck/
│   │   │   │   │   ├── NailCheckActivity.kt
│   │   │   │   │   ├── NailAnalyzerViewModel.kt
│   │   │   │   │   └── NailResult.kt
│   │   │   │   ├── logs/
│   │   │   │   │   ├── EvidenceLogActivity.kt
│   │   │   │   │   └── EvidenceAdapter.kt
│   │   │   │   └── settings/
│   │   │   │       └── SettingsActivity.kt
│   │   │   │
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   ├── Session.kt
│   │   │   │   │   ├── EvidenceEvent.kt
│   │   │   │   │   └── RiskLevel.kt
│   │   │   │   ├── detection/
│   │   │   │   │   ├── CoughDetector.kt
│   │   │   │   │   ├── NailColorDetector.kt
│   │   │   │   │   └── DetectionResult.kt
│   │   │   │   ├── rules/
│   │   │   │   │   └── RiskEvaluationEngine.kt
│   │   │   │   └── repository/
│   │   │   │       └── EvidenceRepository.kt
│   │   │   │
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── db/
│   │   │   │   │   │   ├── SentiguardDatabase.kt
│   │   │   │   │   │   ├── SessionDao.kt
│   │   │   │   │   │   └── EvidenceDao.kt
│   │   │   │   │   ├── file/
│   │   │   │   │   │   ├── AudioStorage.kt
│   │   │   │   │   │   └── EvidenceFileManager.kt
│   │   │   │   │   └── LocalEvidenceRepository.kt
│   │   │   │   └── sync/
│   │   │   │       └── SyncAdapterStub.kt
│   │   │   │
│   │   │   ├── system/
│   │   │   │   ├── service/
│   │   │   │   │   ├── MonitoringForegroundService.kt
│   │   │   │   │   └── ServiceWatchdog.kt
│   │   │   │   ├── audio/
│   │   │   │   │   ├── MicrophoneManager.kt
│   │   │   │   │   └── AudioAnalyzer.kt
│   │   │   │   ├── gps/
│   │   │   │   │   └── LocationTracker.kt
│   │   │   │   ├── alert/
│   │   │   │   │   └── AlertManager.kt
│   │   │   │   └── permissions/
│   │   │   │       └── PermissionHandler.kt
│   │   │   │
│   │   │   ├── security/
│   │   │   │   ├── HashUtils.kt
│   │   │   │   ├── EvidenceChainBuilder.kt
│   │   │   │   └── IntegrityVerifier.kt
│   │   │   │
│   │   │   └── util/
│   │   │       ├── TimeUtils.kt
│   │   │       └── Logger.kt
│   │   │
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── values/
│   │   │   └── raw/
│   │   │       └── calming_audio.mp3
│   │   │
│   │   └── AndroidManifest.xml
│   │
│   └── build.gradle
│
├── diagrams/
│   ├── architecture.puml
│   ├── evidence_flow.puml
│   └── service_lifecycle.puml
│
├── docs/
│   ├── threat_model.md
│   ├── evidence_format.md
│   └── hackathon_scope.md
│
└── README.md
```

---

### Why This Structure Works

* Clear separation of **UI / Domain / Data / System**
* Easy to explain to judges
* Contractors can implement independently
* Sync & verification are clearly future-ready
* Testing and scaling do not require refactors

---

## README – Development Responsibility Map (Human vs Gemini)

This section documents **which files can be safely generated using Gemini (AI coding agent)** and **which require careful human design and integration**. This is intended as a practical development checklist and an explanation of engineering responsibility.

---

### Legend

* ✅ **Gemini-safe** – Can be generated almost fully by Gemini
* ⚠️ **Gemini-assisted** – Gemini can generate, human must review & wire
* 🚫 **Human-critical** – Must be designed and integrated manually

---

## 1. Application Entry & Configuration

| File                  | Responsibility                  | Ownership          |
| --------------------- | ------------------------------- | ------------------ |
| `SentiguardApp.kt`    | App-level initialization        | ⚠️ Gemini-assisted |
| `AndroidManifest.xml` | Permissions, services, policies | 🚫 Human-critical  |

---

## 2. UI Layer (`app/ui`)

### Core Activities

| File                     | Responsibility                 | Ownership |
| ------------------------ | ------------------------------ | --------- |
| `MainActivity.kt`        | App entry & navigation         | ⚠️        |
| `DashboardActivity.kt`   | Start/stop monitoring sessions | ⚠️        |
| `NailCheckActivity.kt`   | Camera-based nail check        | ⚠️        |
| `EvidenceLogActivity.kt` | Evidence viewer (read-only)    | ⚠️        |
| `SettingsActivity.kt`    | App configuration UI           | ✅         |

### ViewModels & UI Models

| File                       | Responsibility              | Ownership |
| -------------------------- | --------------------------- | --------- |
| `DashboardViewModel.kt`    | UI logic                    | ✅         |
| `DashboardState.kt`        | UI state model              | ✅         |
| `NailAnalyzerViewModel.kt` | Nail analysis orchestration | ⚠️        |
| `NailResult.kt`            | Nail check result model     | ✅         |
| `EvidenceAdapter.kt`       | RecyclerView adapter        | ✅         |

---

## 3. Domain Layer (`app/domain`)

### Models

| File               | Responsibility            | Ownership |
| ------------------ | ------------------------- | --------- |
| `Session.kt`       | Monitoring session entity | ✅         |
| `EvidenceEvent.kt` | Evidence record model     | ⚠️        |
| `RiskLevel.kt`     | Risk enum                 | ✅         |

### Detection & Rules

| File                      | Responsibility              | Ownership |
| ------------------------- | --------------------------- | --------- |
| `CoughDetector.kt`        | Audio signal detection      | ⚠️        |
| `NailColorDetector.kt`    | Cyanosis detection rules    | ⚠️        |
| `DetectionResult.kt`      | Detection output model      | ✅         |
| `RiskEvaluationEngine.kt` | Risk escalation logic       | 🚫        |
| `EvidenceRepository.kt`   | Domain repository interface | ✅         |

---

## 4. System Layer (`app/system`)

### Services

| File                             | Responsibility        | Ownership |
| -------------------------------- | --------------------- | --------- |
| `MonitoringForegroundService.kt` | Core runtime service  | 🚫        |
| `ServiceWatchdog.kt`             | Service restart logic | ⚠️        |

### Sensors & OS Interaction

| File                   | Responsibility           | Ownership |
| ---------------------- | ------------------------ | --------- |
| `MicrophoneManager.kt` | Audio capture lifecycle  | ⚠️        |
| `AudioAnalyzer.kt`     | FFT & decibel analysis   | ⚠️        |
| `LocationTracker.kt`   | GPS tracking             | ⚠️        |
| `AlertManager.kt`      | Audio + vibration alerts | ⚠️        |
| `PermissionHandler.kt` | Runtime permissions      | ⚠️        |

---

## 5. Data Layer (`app/data`)

### Database (Room)

| File                    | Responsibility         | Ownership |
| ----------------------- | ---------------------- | --------- |
| `SentiguardDatabase.kt` | Database configuration | ⚠️        |
| `SessionDao.kt`         | Session persistence    | ✅        |
| `EvidenceDao.kt`        | Evidence persistence   | ⚠️        |

### File Storage & Repositories

| File                         | Responsibility          | Ownership |
| ---------------------------- | ----------------------- | --------- |
| `AudioStorage.kt`            | Audio file writes       | ⚠️        |
| `EvidenceFileManager.kt`     | Evidence file metadata  | ⚠️        |
| `LocalEvidenceRepository.kt` | DB + file coordination  | 🚫        |
| `SyncAdapterStub.kt`         | Future sync placeholder | ✅        |

---

## 6. Security Layer (`app/security`)

| File                      | Responsibility            | Ownership |
| ------------------------- | ------------------------- | --------- |
| `HashUtils.kt`            | Cryptographic hashing     | ✅       |
| `EvidenceChainBuilder.kt` | Hash chaining (integrity) | 🚫       |
| `IntegrityVerifier.kt`    | Tamper detection          | ⚠️       |

---

## 7. Utilities (`app/util`)

| File           | Responsibility      | Ownership |
| -------------- | ------------------- | --------- |
| `TimeUtils.kt` | Timestamp utilities | ✅        |
| `Logger.kt`    | Logging helper      | ✅        |

---

## 8. Documentation & Diagrams

| File                     | Responsibility        | Ownership |
| ------------------------ | --------------------- | --------- |
| `architecture.puml`      | System architecture   | ✅        |
| `evidence_flow.puml`     | Evidence lifecycle    | ⚠️        |
| `service_lifecycle.puml` | Service behavior      | ⚠️        |
| `threat_model.md`        | Security assumptions  | ⚠️        |
| `evidence_format.md`     | Legal evidence format | 🚫        |
| `hackathon_scope.md`     | Scope clarity         | ✅        |
| `README.md`              | Project overview      | ✅        |

---

## Summary

* **~40%** of files are safe to delegate to Gemini
* **~40%** require Gemini + human review
* **~20%** are safety- or integrity-critical and must be human-designed

> **Key rule:** Gemini may generate components, but humans must integrate system behavior, safety logic, and legal evidence guarantees.

---
