package com.sentiguard.app.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel : ViewModel() {

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
        }
    }
}
