package com.sentiguard.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentiguard.app.domain.model.EvidenceEvent
import com.sentiguard.app.domain.repository.EvidenceRepository
import com.sentiguard.app.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale

class EvidenceViewModel(
    private val repository: EvidenceRepository? = null
) : ViewModel() {

    // In a real app, we would observe a Flow from the repository.
    // Since our Repo returns Flows, we can convert them.
    // For MVP, we might need to expose an 'getAllEvents' in Repo or just use session-based.
    // Let's assume we want to show *all* logs. The current Repo might strictly be session-scoped.
    // If so, we might need to update Repo or just use a mock flow for 'All Logs' if the Repo doesn't support it yet.
    // Checking LocalEvidenceRepository... it has 'getEventsForSession'.
    
    // Ideally we add 'getAllEvents' to Repository.
    // For now, let's assume we have a list logic or we mock it inside ViewModel if Repo is limited.
    // But better: Let's use the actual Repo. I'll check if I can add 'getAllEvents' quickly.
    // If not, I'll use a hardcoded list *simulating* the Repo data structure for this UI bug fix phase.
    
    private val _state = MutableStateFlow(EvidenceLogsState())
    val state: StateFlow<EvidenceLogsState> = _state

    init {
        // Simulating data fetch
        // In real impl: repository.getAllEvents().collect { ... }
        loadDummyData() 
    }

    private fun loadDummyData() {
        val dummyLogs = listOf(
            LogEntry("1", "2023-10-27", "09:45 AM", "25m", "SAFE", "Sector 4"),
            LogEntry("2", "2023-10-26", "03:15 PM", "10m", "WARNING", "Sector 2"),
            LogEntry("3", "2023-10-26", "01:00 PM", "45m", "SAFE", "Sector 7"),
            LogEntry("4", "2023-10-25", "11:30 AM", "5m", "CRITICAL", "Sector 9")
        )
        _state.value = EvidenceLogsState(logs = dummyLogs)
    }
}
