package com.sentiguard.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EvidenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EvidenceEntity)

    @Query("SELECT * FROM evidence_events WHERE sessionId = :sessionId ORDER BY timestamp DESC")
    fun getEventsForSession(sessionId: String): Flow<List<EvidenceEntity>>
}
