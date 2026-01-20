package com.sentiguard.app.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sentiguard.app.domain.model.EvidenceEvent
import com.sentiguard.app.domain.repository.EvidenceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.time.format.DateTimeFormatter
import java.util.*

data class LogEntry(
    val id: String,
    val date: String,
    val time: String,
    val duration: String,
    val status: String,
    val location: String,
    val notes: String = ""
)

data class EvidenceLogsState(
    val logs: List<LogEntry> = emptyList()
)

sealed class ExportState {
    object Idle : ExportState()
    object Loading : ExportState()
    data class Success(val file: File) : ExportState()
    data class Error(val message: String) : ExportState()
}

sealed class VerificationUiState {
    object Idle : VerificationUiState()
    object Verifying : VerificationUiState()
    data class Verified(val totalCount: Int) : VerificationUiState()
    data class Tampered(val failedIndex: Int) : VerificationUiState()
}

class EvidenceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EvidenceRepository
    
    init {
        val db = com.sentiguard.app.data.local.db.SentiguardDatabase.getDatabase(application)
        repository = com.sentiguard.app.data.local.LocalEvidenceRepository(db.sessionDao(), db.evidenceDao())
        loadEvents()
    }

    private val _state = MutableStateFlow(EvidenceLogsState())
    val state: StateFlow<EvidenceLogsState> = _state.asStateFlow()

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    private val _verificationState = MutableStateFlow<VerificationUiState>(VerificationUiState.Idle)
    val verificationState: StateFlow<VerificationUiState> = _verificationState.asStateFlow()

    private val exportManager by lazy { com.sentiguard.app.system.export.ExportManager(application) }

    private fun loadEvents() {
        viewModelScope.launch {
            repository.getAllEvents().collect { events ->
                val mockEvents = if (events.isEmpty()) {
                    listOf(
                        LogEntry("m1", "2024-01-14", "09:00 AM", "4h", "SAFE", "Sector 7", "Routine Patrol"),
                        LogEntry("m2", "2024-01-13", "02:30 PM", "20m", "WARNING", "Sector 4", "High Gas Levels Detected")
                    )
                } else emptyList()

                val realLogs = events.map { event ->
                    LogEntry(
                        id = event.id,
                        date = event.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())),
                        time = event.timestamp.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())),
                        duration = "0m",
                        status = event.riskLevel.name,
                        location = if (event.latitude != null) "${event.latitude}, ${event.longitude}" else "Unknown"
                    )
                }
                _state.value = EvidenceLogsState(logs = realLogs + mockEvents)
            }
        }
    }

    fun exportEvidence() {
        viewModelScope.launch {
            _exportState.value = ExportState.Loading
            repository.getAllEvents().take(1).collect { events ->
                val result = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    exportManager.generateEvidencePackage(events)
                }
                _exportState.value = if (result != null) ExportState.Success(result) else ExportState.Error("Export failed")
            }
        }
    }

    fun verifyIntegrity() {
        viewModelScope.launch {
            _verificationState.value = VerificationUiState.Verifying
            val result = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                 kotlinx.coroutines.delay(1000) // Keep the UX delay if desired, but inside IO is fine
                 repository.verifyIntegrity()
            }
            
            _verificationState.value = if (result.isClean) {
                VerificationUiState.Verified(result.totalVerified)
            } else {
                VerificationUiState.Tampered(result.failedIndex)
            }
        }
    }

    fun resetExportState() { _exportState.value = ExportState.Idle }
    fun resetVerificationState() { _verificationState.value = VerificationUiState.Idle }
}
