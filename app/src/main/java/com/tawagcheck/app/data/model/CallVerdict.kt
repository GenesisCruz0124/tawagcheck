package com.tawagcheck.app.data.model

/** Result of running the heuristics engine against one incoming call. */
data class CallVerdict(
    val rawNumber: String,
    val normalizedNumber: String?,
    val isHidden: Boolean,
    val score: Int,
    val tier: RiskTier,
    val reasons: List<String>,
    val action: CallAction
)
