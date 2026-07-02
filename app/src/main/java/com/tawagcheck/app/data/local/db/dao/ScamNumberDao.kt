package com.tawagcheck.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tawagcheck.app.data.local.db.entity.ScamNumberEntity
import com.tawagcheck.app.data.model.ScamMatchType
import kotlinx.coroutines.flow.Flow

@Dao
interface ScamNumberDao {

    @Query("SELECT COUNT(*) FROM scam_numbers")
    suspend fun count(): Int

    @Query("SELECT * FROM scam_numbers ORDER BY dateAdded DESC")
    fun observeAll(): Flow<List<ScamNumberEntity>>

    @Query("SELECT * FROM scam_numbers WHERE type = :type AND number = :number LIMIT 1")
    suspend fun findExactMatch(number: String, type: ScamMatchType = ScamMatchType.FULL): ScamNumberEntity?

    @Query("SELECT * FROM scam_numbers WHERE type = 'PREFIX' AND :number LIKE number || '%'")
    suspend fun findPrefixMatches(number: String): List<ScamNumberEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<ScamNumberEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ScamNumberEntity)

    @Query("DELETE FROM scam_numbers WHERE source != 'user_reported'")
    suspend fun deleteAllExceptUserReported()

    @Query("SELECT MAX(dateAdded) FROM scam_numbers WHERE source = 'remote'")
    suspend fun lastRemoteUpdateTimestamp(): Long?
}
