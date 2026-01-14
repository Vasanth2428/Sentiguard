package com.sentiguard.app.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "health_records")
data class HealthRecordEntity(
    @PrimaryKey val id: String,
    val timestamp: LocalDateTime,
    val hasDizziness: Boolean,
    val hasCough: Boolean,
    val hasFever: Boolean,
    val temperature: Float,
    val isFit: Boolean
)
