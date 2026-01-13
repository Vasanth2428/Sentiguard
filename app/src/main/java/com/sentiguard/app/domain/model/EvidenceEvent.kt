package com.sentiguard.app.domain.model

import java.time.LocalDateTime

data class EvidenceEvent(
    val id: String, // UUID
    val sessionId: String,
    val timestamp: LocalDateTime,
    val type: EventType,
    val riskLevel: RiskLevel,
    val data: Map<String, String>, // Flexible payload (e.g. dB level, location)
    val attachmentPath: String? = null // Path to audio/image file
)

enum class EventType {
    COUGH_DETECTED,
    NAIL_CHECK,
    LOCATION_UPDATE,
    USER_ALERT,
    SESSION_START,
    SESSION_END
}
