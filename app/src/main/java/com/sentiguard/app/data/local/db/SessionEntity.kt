package com.sentiguard.app.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey
    val id: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
    val isActive: Boolean
)
