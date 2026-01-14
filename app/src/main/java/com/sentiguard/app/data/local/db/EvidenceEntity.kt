package com.sentiguard.app.data.local.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.sentiguard.app.domain.model.EventType
import com.sentiguard.app.domain.model.RiskLevel
import java.time.LocalDateTime

@Entity(
    tableName = "evidence_events",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EvidenceEntity(
    @PrimaryKey
    val id: String,
    val sessionId: String,
    val timestamp: LocalDateTime,
    val type: EventType,
    val riskLevel: RiskLevel,
    val latitude: Double?,
    val longitude: Double?,
    val sensorValue: String?,
    val data: Map<String, String>,
    val attachmentPath: String?
)
