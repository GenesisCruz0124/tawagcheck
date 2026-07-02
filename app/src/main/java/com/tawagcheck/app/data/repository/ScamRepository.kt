package com.tawagcheck.app.data.repository

import com.tawagcheck.app.data.local.db.dao.ScamNumberDao
import com.tawagcheck.app.data.local.db.entity.ScamNumberEntity
import com.tawagcheck.app.data.model.ScamCategory
import com.tawagcheck.app.data.model.ScamMatchType
import com.tawagcheck.app.data.model.ScamSource
import kotlinx.coroutines.flow.Flow

sealed class ScamMatch {
    data class Full(val category: ScamCategory) : ScamMatch()
    data class Prefix(val category: ScamCategory) : ScamMatch()
    data object None : ScamMatch()
}

class ScamRepository(private val scamNumberDao: ScamNumberDao) {

    fun observeAll(): Flow<List<ScamNumberEntity>> = scamNumberDao.observeAll()

    val lastRemoteUpdateTimestamp: suspend () -> Long? = { scamNumberDao.lastRemoteUpdateTimestamp() }

    suspend fun match(e164OrRaw: String): ScamMatch {
        scamNumberDao.findExactMatch(e164OrRaw)?.let { return ScamMatch.Full(it.category) }
        val prefixMatches = scamNumberDao.findPrefixMatches(e164OrRaw)
        val longest = prefixMatches.maxByOrNull { it.number.length }
        return longest?.let { ScamMatch.Prefix(it.category) } ?: ScamMatch.None
    }

    suspend fun reportUserScam(e164OrRaw: String) {
        scamNumberDao.insert(
            ScamNumberEntity(
                number = e164OrRaw,
                type = ScamMatchType.FULL,
                category = ScamCategory.USER_REPORTED,
                source = ScamSource.USER_REPORTED,
                dateAdded = System.currentTimeMillis()
            )
        )
    }

    suspend fun replaceRemoteEntries(entries: List<ScamNumberEntity>) {
        scamNumberDao.deleteAllExceptUserReported()
        scamNumberDao.insertAll(entries)
    }
}
