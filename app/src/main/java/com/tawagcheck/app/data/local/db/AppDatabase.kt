package com.tawagcheck.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tawagcheck.app.data.local.db.dao.CallHistoryDao
import com.tawagcheck.app.data.local.db.dao.ScamNumberDao
import com.tawagcheck.app.data.local.db.entity.CallHistoryEntity
import com.tawagcheck.app.data.local.db.entity.ScamNumberEntity

@Database(
    entities = [ScamNumberEntity::class, CallHistoryEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scamNumberDao(): ScamNumberDao
    abstract fun callHistoryDao(): CallHistoryDao

    companion object {
        const val DATABASE_NAME = "tawagcheck.db"
    }
}
