package com.tawagcheck.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tawagcheck.app.data.local.db.entity.CallHistoryEntity
import kotlinx.coroutines.flow.Flow

data class DailyFlaggedCount(
    val day: String,
    val count: Int
)

@Dao
interface CallHistoryDao {

    @Insert
    suspend fun insert(entity: CallHistoryEntity): Long

    @Query("SELECT * FROM call_history ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<CallHistoryEntity>>

    @Query("SELECT * FROM call_history WHERE tier = :tier ORDER BY timestamp DESC")
    fun observeByTier(tier: String): Flow<List<CallHistoryEntity>>

    @Query("SELECT COUNT(*) FROM call_history WHERE timestamp >= :sinceMillis")
    fun observeCountSince(sinceMillis: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM call_history WHERE action = 'REJECT'")
    fun observeCumulativeBlockedCount(): Flow<Int>

    @Query(
        """
        SELECT strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch') AS day, COUNT(*) AS count
        FROM call_history
        WHERE tier != 'SAFE' AND timestamp >= :sinceMillis
        GROUP BY day
        ORDER BY day ASC
        """
    )
    fun observeDailyFlaggedCounts(sinceMillis: Long): Flow<List<DailyFlaggedCount>>

    @Query("SELECT COUNT(*) FROM call_history WHERE normalizedNumber = :normalizedNumber AND timestamp >= :sinceMillis")
    suspend fun countRecentCallsFrom(normalizedNumber: String, sinceMillis: Long): Int
}
