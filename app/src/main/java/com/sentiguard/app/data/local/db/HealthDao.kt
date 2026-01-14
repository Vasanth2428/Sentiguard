package com.sentiguard.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: HealthRecordEntity)

    @Query("SELECT * FROM health_records ORDER BY timestamp DESC LIMIT 1")
    fun getLatestRecord(): Flow<HealthRecordEntity?>
    
    @Query("SELECT * FROM health_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<HealthRecordEntity>>
}
