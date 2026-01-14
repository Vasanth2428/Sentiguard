package com.sentiguard.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [SessionEntity::class, EvidenceEntity::class, HealthRecordEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SentiguardDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun evidenceDao(): EvidenceDao
    abstract fun healthDao(): HealthDao

    companion object {
        @Volatile
        private var INSTANCE: SentiguardDatabase? = null

        fun getDatabase(context: android.content.Context): SentiguardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    SentiguardDatabase::class.java,
                    "sentiguard-db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
