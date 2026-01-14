package com.sentiguard.app.ui.screens

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.sentiguard.app.system.service.MonitoringService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ToggleVibration -> {
                _state.update { it.copy(isVibrationEnabled = event.enabled) }
            }
            is SettingsEvent.ToggleAudioSupport -> {
                _state.update { it.copy(isAudioSupportEnabled = event.enabled) }
            }
            is SettingsEvent.SelectLanguage -> {
                _state.update { it.copy(selectedLanguage = event.language) }
            }
            is SettingsEvent.SimulateGasLeak -> {
                sendSimulationIntent(MonitoringService.ACTION_SIMULATE_GAS)
            }
            is SettingsEvent.SimulateHazard -> {
                sendSimulationIntent(MonitoringService.ACTION_SIMULATE_HAZARD)
            }
        }
    }
    
    private fun sendSimulationIntent(action: String) {
        val context = getApplication<Application>()
        val intent = Intent(context, MonitoringService::class.java).apply {
            this.action = action
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
}
