package com.tawagcheck.app.data.remote

import kotlinx.serialization.Serializable

/** Shared JSON schema for both the bundled seed asset and the remote scamlist.json update. */
@Serializable
data class ScamListPayload(
    val entries: List<ScamListEntryDto> = emptyList()
)

@Serializable
data class ScamListEntryDto(
    val number: String,
    val type: String,
    val category: String,
    val source: String
)
