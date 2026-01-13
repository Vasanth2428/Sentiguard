package com.sentiguard.app.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class AlertSeverity { CRITICAL, WARNING, INFO }
enum class AlertStatus { ACTIVE, ACKNOWLEDGED, RESOLVED }

data class Alert(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val severity: AlertSeverity,
    val status: AlertStatus = AlertStatus.ACTIVE,
    val location: String = "Unknown Location"
)

data class AlertsState(
    val activeAlerts: List<Alert> = emptyList(),
    val historicalAlerts: List<Alert> = emptyList(),
    val selectedTab: Int = 0 
)

sealed class AlertsEvent {
    data class AcknowledgeAlert(val alertId: String) : AlertsEvent()
    data class DismissAlert(val alertId: String) : AlertsEvent()
    data class SelectTab(val tabIndex: Int) : AlertsEvent()
}

class AlertsViewModel : ViewModel() {

    private val _state = MutableStateFlow(AlertsState())
    val state: StateFlow<AlertsState> = _state.asStateFlow()

    init {
        // Load mock data
        val mockAlerts = listOf(
            Alert(
                id = "1",
                title = "Low Oxygen Level Warning",
                message = "Nail color analysis indicates moderate oxygen deprivation. Monitor closely.",
                timestamp = "15m ago",
                severity = AlertSeverity.WARNING,
                location = "Sector 7, Manhole Site A"
            ),
             Alert(
                id = "2",
                title = "High Heart Rate",
                message = "Wearable sensor detected elevated heart rate > 120bpm.",
                timestamp = "1h ago",
                severity = AlertSeverity.CRITICAL,
                status = AlertStatus.RESOLVED,
                location = "Sector 4"
            )
        )
        
        _state.update { 
            it.copy(
                activeAlerts = mockAlerts.filter { a -> a.status == AlertStatus.ACTIVE },
                historicalAlerts = mockAlerts.filter { a -> a.status != AlertStatus.ACTIVE }
            ) 
        }
    }

    fun onEvent(event: AlertsEvent) {
        when (event) {
            is AlertsEvent.AcknowledgeAlert -> {
                 // Move to history/acknowledged
                 updateAlertStatus(event.alertId, AlertStatus.ACKNOWLEDGED)
            }
            is AlertsEvent.DismissAlert -> {
                 // Move to history/resolved or remove
                 updateAlertStatus(event.alertId, AlertStatus.RESOLVED)
            }
            is AlertsEvent.SelectTab -> {
                _state.update { it.copy(selectedTab = event.tabIndex) }
            }
        }
    }
    
    private fun updateAlertStatus(id: String, newStatus: AlertStatus) {
        val currentActive = _state.value.activeAlerts.toMutableList()
        val currentHistory = _state.value.historicalAlerts.toMutableList()
        
        // Check active
        val alertIndex = currentActive.indexOfFirst { it.id == id }
        if (alertIndex != -1) {
            val alert = currentActive.removeAt(alertIndex).copy(status = newStatus)
            currentHistory.add(0, alert)
        }
        
        _state.update { 
            it.copy(
                activeAlerts = currentActive,
                historicalAlerts = currentHistory
            ) 
        }
    }
}
