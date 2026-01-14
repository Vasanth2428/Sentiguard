package com.sentiguard.app.data.local

import com.sentiguard.app.data.local.db.EvidenceDao
import com.sentiguard.app.data.local.db.EvidenceEntity
import com.sentiguard.app.data.local.db.SessionDao
import com.sentiguard.app.data.local.db.SessionEntity
import com.sentiguard.app.domain.model.EvidenceEvent
import com.sentiguard.app.domain.model.Session
import com.sentiguard.app.domain.repository.EvidenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class LocalEvidenceRepository(
    private val sessionDao: SessionDao,
    private val evidenceDao: EvidenceDao
) : EvidenceRepository {

    override fun getAllSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getActiveSession(): Flow<Session?> {
        return sessionDao.getActiveSession().map { it?.toDomain() }
    }

    override suspend fun startSession(): Result<String> {
        return try {
            val id = java.util.UUID.randomUUID().toString()
            val session = SessionEntity(
                id = id,
                startTime = LocalDateTime.now(),
                endTime = null,
                isActive = true
            )
            sessionDao.insertSession(session)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun stopSession(sessionId: String): Result<Unit> {
        return try {
            val entity = sessionDao.getSessionById(sessionId)
            if (entity != null) {
                val updated = entity.copy(
                    isActive = false,
                    endTime = LocalDateTime.now()
                )
                sessionDao.updateSession(updated)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Session not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logEvent(event: EvidenceEvent): Result<Unit> {
        return try {
            val entity = event.toEntity()
            evidenceDao.insertEvent(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getEventsForSession(sessionId: String): Flow<List<EvidenceEvent>> {
        return evidenceDao.getEventsForSession(sessionId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllEvents(): Flow<List<EvidenceEvent>> {
        return evidenceDao.getAllEvents().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // Mappers
    private fun SessionEntity.toDomain(): Session {
        return Session(id, startTime, endTime, isActive)
    }

    private fun EvidenceEvent.toEntity(): EvidenceEntity {
        return EvidenceEntity(id, sessionId, timestamp, type, riskLevel, latitude, longitude, sensorValue, data, attachmentPath)
    }

    private fun EvidenceEntity.toDomain(): EvidenceEvent {
        return EvidenceEvent(id, sessionId, timestamp, type, riskLevel, latitude, longitude, sensorValue, data, attachmentPath)
    }
}
