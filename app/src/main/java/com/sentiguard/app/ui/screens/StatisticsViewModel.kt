package com.sentiguard.app.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.sentiguard.app.ui.theme.GreenSafe
import com.sentiguard.app.ui.theme.AmberWarning
import androidx.compose.ui.graphics.Color

data class StatData(
    val label: String,
    val value: String
)

data class SystemHealthData(
    val label: String,
    val value: String,
    val color: Color
)

data class StatisticsState(
    val summaryStats: List<StatData> = emptyList(),
    val weeklyActivity: List<Float> = emptyList(),
    val systemHealth: List<SystemHealthData> = emptyList()
)

class StatisticsViewModel : ViewModel() {

    private val _state = MutableStateFlow(StatisticsState())
    val state: StateFlow<StatisticsState> = _state.asStateFlow()

    init {
        // Mock Data
        _state.value = StatisticsState(
            summaryStats = listOf(
                StatData("Hours Monitored", "42.5"),
                StatData("Sessions", "12"),
                StatData("Safety Score", "98%")
            ),
            weeklyActivity = listOf(0.4f, 0.6f, 0.8f, 0.3f, 0.7f, 0.9f, 0.5f),
            systemHealth = listOf(
                SystemHealthData("Microphone Sensitivity", "Optimal", GreenSafe),
                SystemHealthData("Battery Impact", "Low (Polling 5m)", GreenSafe),
                SystemHealthData("Storage Space", "1.2GB Free", GreenSafe)
            )
        )
    }
}
