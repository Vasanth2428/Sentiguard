package com.sentiguard.app.domain.repository

import com.sentiguard.app.domain.model.EvidenceEvent
import com.sentiguard.app.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface EvidenceRepository {
    fun getAllSessions(): Flow<List<Session>>
    
    fun getActiveSession(): Flow<Session?>
    
    suspend fun startSession(): Result<String>
    
    suspend fun stopSession(sessionId: String): Result<Unit>
    
    suspend fun logEvent(event: EvidenceEvent): Result<Unit>
    
    fun getEventsForSession(sessionId: String): Flow<List<EvidenceEvent>>

    fun getAllEvents(): Flow<List<EvidenceEvent>>
}
