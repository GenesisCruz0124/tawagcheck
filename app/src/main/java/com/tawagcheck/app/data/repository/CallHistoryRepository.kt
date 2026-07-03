package com.tawagcheck.app.data.repository

import com.tawagcheck.app.data.local.db.dao.CallHistoryDao
import com.tawagcheck.app.data.local.db.dao.DailyFlaggedCount
import com.tawagcheck.app.data.local.db.entity.CallHistoryEntity
import com.tawagcheck.app.data.model.CallVerdict
import com.tawagcheck.app.data.model.RiskTier
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.concurrent.TimeUnit

class CallHistoryRepository(private val callHistoryDao: CallHistoryDao) {

    fun observeAll(): Flow<List<CallHistoryEntity>> = callHistoryDao.observeAll()

    fun observeByTier(tier: RiskTier): Flow<List<CallHistoryEntity>> =
        callHistoryDao.observeByTier(tier.name)

    fun observeScreenedToday(): Flow<Int> = callHistoryDao.observeCountSince(startOfTodayMillis())

    fun observeCumulativeBlocked(): Flow<Int> = callHistoryDao.observeCumulativeBlockedCount()

    fun observeLast7DaysFlagged(): Flow<List<DailyFlaggedCount>> =
        callHistoryDao.observeDailyFlaggedCounts(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7))

    suspend fun recentCallCount(normalizedNumber: String, withinMillis: Long): Int =
        callHistoryDao.countRecentCallsFrom(normalizedNumber, System.currentTimeMillis() - withinMillis)

    suspend fun record(verdict: CallVerdict): Long =
        callHistoryDao.insert(
            CallHistoryEntity(
                rawNumber = verdict.rawNumber,
                normalizedNumber = verdict.normalizedNumber,
                isHidden = verdict.isHidden,
                timestamp = System.currentTimeMillis(),
                score = verdict.score,
                tier = verdict.tier,
                reasons = verdict.reasons.joinToString("|"),
                action = verdict.action
            )
        )

    private fun startOfTodayMillis(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}
