package com.sentiguard.app.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sentiguard.app.data.local.db.HealthDao
import com.sentiguard.app.data.local.db.HealthRecordEntity
import com.sentiguard.app.data.local.db.SentiguardDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

data class HealthCheckState(
    val hasDizziness: Boolean = false,
    val hasCough: Boolean = false,
    val hasFever: Boolean = false,
    val temperatureInput: String = "",
    val isSubmitted: Boolean = false,
    val isFit: Boolean = false
)

class HealthCheckViewModel(application: Application) : AndroidViewModel(application) {

    private val healthDao: HealthDao = SentiguardDatabase.getDatabase(application).healthDao()

    private val _state = MutableStateFlow(HealthCheckState())
    val state: StateFlow<HealthCheckState> = _state.asStateFlow()

    fun onDizzinessChange(value: Boolean) {
        _state.update { it.copy(hasDizziness = value) }
    }

    fun onCoughChange(value: Boolean) {
        _state.update { it.copy(hasCough = value) }
    }

    fun onFeverChange(value: Boolean) {
        _state.update { it.copy(hasFever = value) }
    }

    fun onTemperatureChange(value: String) {
        _state.update { it.copy(temperatureInput = value) }
    }

    fun submitCheck() {
        viewModelScope.launch {
            val s = state.value
            val temp = s.temperatureInput.toFloatOrNull() ?: 0f
            
            // Logic: Unfit if any symptom is true OR temp > 38.0
            val isUnfit = s.hasDizziness || s.hasCough || s.hasFever || (temp > 38.0f)
            val isFit = !isUnfit

            val record = HealthRecordEntity(
                id = UUID.randomUUID().toString(),
                timestamp = LocalDateTime.now(),
                hasDizziness = s.hasDizziness,
                hasCough = s.hasCough,
                hasFever = s.hasFever,
                temperature = temp,
                isFit = isFit
            )
            
            healthDao.insertRecord(record)
            
            _state.update { 
                it.copy(
                    isSubmitted = true,
                    isFit = isFit
                ) 
            }
        }
    }
    
    fun reset() {
        _state.value = HealthCheckState()
    }
}
