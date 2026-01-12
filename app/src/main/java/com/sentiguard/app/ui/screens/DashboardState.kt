package com.sentiguard.app.ui.screens

import androidx.compose.ui.graphics.Color

/**
 * Immutable state for the Dashboard Screen.
 */
data class DashboardState(
    val isMonitoring: Boolean = false,
    val securityStatus: SecurityStatus = SecurityStatus.SAFE,
    val sessionDuration: String = "00:00:00",
    val statusMessage: String = "Breathing normal"
)

enum class SecurityStatus(val label: String) {
    SAFE("SAFE"),
    WARNING("WARNING"),
    DANGER("DANGER")
}

/**
 * Events for Dashboard interactions.
 */
sealed class DashboardEvent {
    data object ToggleMonitoring : DashboardEvent()
    data object NavigateToNailCheck : DashboardEvent()
    data object NavigateToLogs : DashboardEvent()
    data object NavigateToSettings : DashboardEvent()
    data object NavigateToGuidance : DashboardEvent()
    data object NavigateToStatistics : DashboardEvent()
}
