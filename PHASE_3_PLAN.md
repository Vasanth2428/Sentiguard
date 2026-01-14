# Phase 3: Field Readiness & "Justice Sentinel" Finalization
**Objective:** Transform Sentiguard from a functional prototype into a legally verifiable, secure, and accessible tool ready for field deployment. This phase focuses entirely on in-app capabilities without requiring external hardware.

---

## 1. Feature: Secure Evidence Export ("The Black Box")
**Goal:** Allow workers to generate a verifiable, self-contained evidence package that can be shared via WhatsApp, Email, or Bluetooth.

### Atomic Instructions:
1.  **Dependency Addition:**
    *   Add a ZIP compression library to `app/build.gradle.kts` (e.g., `java.util.zip` is standard, or a lightweight wrapper).
2.  **Create `ExportManager.kt` (`app/system/export/`):**
    *   Implement `generateEvidencePackage(sessionId: String): File`.
    *   **Step 2a:** Query `EvidenceDatabase` for all events with `sessionId`.
    *   **Step 2b:** Convert events to a single `JSON` or `CSV` file (`log.json`).
    *   **Step 2c:** Collect all related artifacts:
        *   Audio files from `app_data/audio/{sessionId}/`.
        *   Images from `app_data/images/{sessionId}/`.
    *   **Step 2d:** Generate `manifest.txt` listing all files and their SHA-256 hashes.
3.  **Implement Zipping Logic:**
    *   Create a ZIP file named `Sentiguard_Evidence_{Timestamp}.zip`.
    *   Compress all collected files into this archive.
4.  **UI Integration:**
    *   Modify `EvidenceDetailScreen.kt` or `EvidenceLogsScreen.kt`.
    *   Add a "Share Evidence" button `FloatingActionButton`.
    *   On click, call `ExportManager` -> `FileProvider` -> `Intent.ACTION_SEND` (Android Share Sheet).

---

## 2. Feature: Military-Grade Encryption (AES-256)
**Goal:** Encrypt all sensitive data (Audio, Images, Logs) at rest and inside the exported package.

### Atomic Instructions:
1.  **Create `EncryptionManager.kt` (`app/system/security/`):**
    *   Use `AndroidKeyStore` to generate/retrieve a master secret key (non-accessible to other apps).
    *   Implement `encrypt(data: ByteArray): ByteArray` using `AES/GCM/NoPadding`.
    *   Implement `decrypt(data: ByteArray): ByteArray`.
2.  **Update `AudioStreamProvider` & `AudioStorage`:**
    *   Refactor audio recording to write to a temporary buffer -> Encrypt chunk -> Write to disk.
    *   *Alternative (easier):* Encrypt the file immediately after recording stops.
3.  **Update `CameraManager`:**
    *   Intercept the saved image callback.
    *   Pass `File` -> `EncryptionManager` -> Overwrite with Encrypted Bytes.
4.  **Update `ExportManager`:**
    *   When generating the ZIP, the export must be *decrypted* locally before zipping (or the ZIP itself must be password protected).
    *   **Proposed Flow:** Export unencrypted for legal viewing, OR implement "Password Protected ZIP" if sharing via insecure channels.

---

## 3. Feature: In-App Integrity Audit
**Goal:** Allow the user (or an observer) to verify that the logs on the device have not been tampered with.

### Atomic Instructions:
1.  **Create `IntegrityToom.kt` (`app/system/security/`):**
    *   Implement `verifyChain(sessionId: String): Boolean`.
    *   **Logic:**
        *   Fetch all events sorted by timestamp.
        *   Iterate through events:
            *   Calculate `Hash(CurrentEventData + PreviousEventHash)`.
            *   Compare with stored `event.hash`.
            *   If mismatch, return `False` (Tampered at Index X).
2.  **UI Integration (`EvidenceLogsScreen.kt`):**
    *   Add a "Verify Integrity" action (in Toolbar/Menu).
    *   Show a progress dialog "Verifying Blockchain...".
    *   **Result:**
        *   **Success:** Show a Green Shield icon "Data Integrity Verified".
        *   **Fail:** Show a Red Warning "Integry Failure: Log #45 Modified".

---

## 4. Feature: "Glove Mode" (Accessibility)
**Goal:** Optimizations for usage in harsh environments (darkness, gloves, stress).

### Atomic Instructions:
1.  **Create `AccessibilityManager.kt`:**
    *   Manage a preference `isGloveModeEnabled`.
2.  **UI Updates:**
    *   In `Theme.kt`: Define a `GloveModeTypography` with 20% larger fonts.
    *   In `DashboardScreen.kt`:
        *   Increase padding on all `Clickable` elements to minimum 64dp (standard is 48dp).
        *   Simplify colors to High Contrast (Black/White/Neons) when enabled.
3.  **Settings Integration:**
    *   Add toggle "Glove Mode" in `SettingsScreen`.

---

## 5. Summary Checklist
- [ ] `ExportManager` (Zip generation)
- [ ] `EncryptionManager` (AES-256)
- [ ] `IntegrityTool` (Hash verification)
- [ ] "Glove Mode" UI Logic
