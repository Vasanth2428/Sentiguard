package com.sentiguard.app.ui.screens

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sentiguard.app.system.service.MonitoringService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    fun onEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.ToggleMonitoring -> toggleMonitoring()
            else -> {} // Navigation events handled by UI
        }
    }

    private fun toggleMonitoring() {
        val currentIsMonitoring = _state.value.isMonitoring
        val newIsMonitoring = !currentIsMonitoring

        _state.update {
            it.copy(
                isMonitoring = newIsMonitoring,
                statusMessage = if (newIsMonitoring) "Monitoring Active" else "Monitoring Paused",
                // For MVP, toggle status color for visual feedback
                securityStatus = if (newIsMonitoring) SecurityStatus.SAFE else SecurityStatus.SAFE 
            )
        }

        // Start/Stop Service
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context, MonitoringService::class.java).apply {
            action = if (newIsMonitoring) MonitoringService.ACTION_START else MonitoringService.ACTION_STOP
        }
        
        if (newIsMonitoring) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } else {
            context.stopService(intent)
        }
    }
}
