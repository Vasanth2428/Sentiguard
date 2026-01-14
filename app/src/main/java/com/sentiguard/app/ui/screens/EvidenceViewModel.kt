package com.sentiguard.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentiguard.app.domain.model.EvidenceEvent
import com.sentiguard.app.domain.repository.EvidenceRepository
import com.sentiguard.app.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale

class EvidenceViewModel(application: android.app.Application) : androidx.lifecycle.AndroidViewModel(application) {

    private val repository: EvidenceRepository
    
    init {
        val db = com.sentiguard.app.data.local.db.SentiguardDatabase.getDatabase(application)
        repository = com.sentiguard.app.data.local.LocalEvidenceRepository(db.sessionDao(), db.evidenceDao())
        
        loadEvents()
    }

    private val _state = MutableStateFlow(EvidenceLogsState())
    val state: StateFlow<EvidenceLogsState> = _state.asStateFlow()

    private fun loadEvents() {
        viewModelScope.launch {
            repository.getAllEvents().collect { events ->
                // MOCK DATA INJECTION (If empty, or to supplement)
                val mockEvents = if (events.isEmpty()) {
                    listOf(
                        LogEntry("m1", "2024-01-14", "09:00 AM", "4h", "SAFE", "Sector 7", "Routine Patrol"),
                        LogEntry("m2", "2024-01-13", "02:30 PM", "20m", "WARNING", "Sector 4", "High Gas Levels Detected"),
                        LogEntry("m3", "2024-01-12", "11:15 AM", "1h", "SAFE", "Sector 2", "Maintenance"),
                        LogEntry("m4", "2024-01-10", "04:45 PM", "5m", "CRITICAL", "Sector 9", "Cough Detected - Stopped Work")
                    )
                } else {
                    emptyList()
                }

                // Transform Domain Event to UI LogEntry
                val realLogs = events.map { event ->
                    LogEntry(
                        id = event.id,
                        date = event.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())),
                        time = event.timestamp.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())),
                        duration = "0m", // Placeholder
                        status = when(event.riskLevel) {
                            com.sentiguard.app.domain.model.RiskLevel.SAFE -> "SAFE"
                            com.sentiguard.app.domain.model.RiskLevel.WARNING -> "WARNING"
                            com.sentiguard.app.domain.model.RiskLevel.CRITICAL -> "CRITICAL"
                            else -> "UNKNOWN"
                        },
                        location = if (event.latitude != null) "${event.latitude}, ${event.longitude}" else "Unknown"
                    )
                }
                
                _state.value = EvidenceLogsState(logs = realLogs + mockEvents)
            }
        }
    }
}
