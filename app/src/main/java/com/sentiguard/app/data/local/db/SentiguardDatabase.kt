package com.sentiguard.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [SessionEntity::class, EvidenceEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SentiguardDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun evidenceDao(): EvidenceDao
}
