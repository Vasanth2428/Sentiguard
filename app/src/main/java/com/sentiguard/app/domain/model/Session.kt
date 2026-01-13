package com.sentiguard.app.domain.model

import java.time.LocalDateTime

data class Session(
    val id: String, // UUID
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null,
    val isActive: Boolean = true
)
