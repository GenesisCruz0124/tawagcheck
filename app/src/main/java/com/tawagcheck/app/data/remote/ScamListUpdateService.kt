package com.tawagcheck.app.data.remote

import com.tawagcheck.app.data.local.db.entity.ScamNumberEntity
import com.tawagcheck.app.data.model.ScamCategory
import com.tawagcheck.app.data.model.ScamMatchType
import com.tawagcheck.app.data.model.ScamSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

sealed class ScamListUpdateResult {
    data class Success(val entries: List<ScamNumberEntity>) : ScamListUpdateResult()
    data class Failure(val message: String) : ScamListUpdateResult()
}

/** Fetches scamlist.json from a user-configured URL. Manual trigger only — no background sync. */
class ScamListUpdateService(
    private val httpClient: OkHttpClient = OkHttpClient()
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchUpdate(url: String): ScamListUpdateResult = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).get().build()
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext ScamListUpdateResult.Failure("Server returned HTTP ${response.code}")
                }
                val body = response.body?.string()
                    ?: return@withContext ScamListUpdateResult.Failure("Empty response body")

                val payload = json.decodeFromString<ScamListPayload>(body)
                val now = System.currentTimeMillis()
                val entities = payload.entries.mapNotNull { dto ->
                    runCatching {
                        ScamNumberEntity(
                            number = dto.number,
                            type = ScamMatchType.valueOf(dto.type),
                            category = ScamCategory.valueOf(dto.category),
                            source = ScamSource.REMOTE,
                            dateAdded = now
                        )
                    }.getOrNull()
                }
                ScamListUpdateResult.Success(entities)
            }
        } catch (e: IOException) {
            ScamListUpdateResult.Failure(e.message ?: "Network error")
        } catch (e: Exception) {
            ScamListUpdateResult.Failure(e.message ?: "Failed to parse scam list")
        }
    }
}
