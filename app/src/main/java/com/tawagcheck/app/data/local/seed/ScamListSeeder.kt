package com.tawagcheck.app.data.local.seed

import android.content.Context
import com.tawagcheck.app.data.local.db.dao.ScamNumberDao
import com.tawagcheck.app.data.local.db.entity.ScamNumberEntity
import com.tawagcheck.app.data.model.ScamCategory
import com.tawagcheck.app.data.model.ScamMatchType
import com.tawagcheck.app.data.remote.ScamListPayload
import kotlinx.serialization.json.Json

/** Populates scam_numbers from the bundled asset the first time the database is empty. */
class ScamListSeeder(
    private val context: Context,
    private val scamNumberDao: ScamNumberDao
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun seedIfEmpty() {
        if (scamNumberDao.count() > 0) return

        val raw = context.assets.open(SEED_ASSET_NAME).bufferedReader().use { it.readText() }
        val payload = json.decodeFromString<ScamListPayload>(raw)
        val now = System.currentTimeMillis()

        val entities = payload.entries.mapNotNull { dto ->
            runCatching {
                ScamNumberEntity(
                    number = dto.number,
                    type = ScamMatchType.valueOf(dto.type),
                    category = ScamCategory.valueOf(dto.category),
                    source = dto.source,
                    dateAdded = now
                )
            }.getOrNull()
        }

        scamNumberDao.insertAll(entities)
    }

    private companion object {
        const val SEED_ASSET_NAME = "scamlist_seed.json"
    }
}
