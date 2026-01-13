package com.sentiguard.app.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

class NailCheckViewModel : ViewModel() {

    private val _state = MutableStateFlow(NailCheckState())
    val state: StateFlow<NailCheckState> = _state.asStateFlow()

    fun onEvent(event: NailCheckEvent) {
        when (event) {
            is NailCheckEvent.ImageCaptured -> {
                // Simulate analysis
                simulateAnalysis(event.file)
            }
            is NailCheckEvent.Reset -> {
                _state.update { 
                    it.copy(
                        capturedImage = null,
                        isResultAvailable = false,
                        resultMessage = ""
                    ) 
                }
            }
        }
    }
    
    // Add logic for simulated "Analysis"
    private fun simulateAnalysis(file: File) {
         _state.update { 
            it.copy(
                capturedImage = file,
                isResultAvailable = true,
                resultMessage = "Oxygen levels normal (98%). No issues detected."
            ) 
        }
    }
}
