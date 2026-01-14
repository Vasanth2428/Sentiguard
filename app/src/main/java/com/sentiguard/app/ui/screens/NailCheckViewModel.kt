package com.sentiguard.app.ui.screens

import android.app.Application
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sentiguard.app.system.ml.NailHealthDetector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class NailCheckViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(NailCheckState())
    val state: StateFlow<NailCheckState> = _state.asStateFlow()

    // Lazy initialization of the ML detector
    private val detector by lazy { NailHealthDetector(application) }

    fun onEvent(event: NailCheckEvent) {
        when (event) {
            is NailCheckEvent.ImageCaptured -> {
                analyzeImage(event.file)
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
    
    private fun analyzeImage(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Decode bitmap from file
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                
                if (bitmap != null) {
                    val result = detector.analyze(bitmap)
                    
                    val message = if (result.isRisky) {
                        "Warning: ${result.label} (${(result.confidence * 100).toInt()}% confidence)"
                    } else {
                        "Healthy: No issues detected (${(result.confidence * 100).toInt()}% confidence)"
                    }

                    _state.update { 
                        it.copy(
                            capturedImage = file,
                            isResultAvailable = true,
                            resultMessage = message
                        ) 
                    }
                } else {
                    _state.update { 
                        it.copy(
                            capturedImage = file,
                            isResultAvailable = true,
                            resultMessage = "Error: Could not process image."
                        ) 
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        capturedImage = file,
                        isResultAvailable = true,
                        resultMessage = "Analysis failed: ${e.localizedMessage}"
                    ) 
                }
            }
        }
    }
}
