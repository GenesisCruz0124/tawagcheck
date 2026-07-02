package com.tawagcheck.app.data.model

enum class RiskTier {
    SAFE,
    SUSPICIOUS,
    LIKELY_SCAM;

    companion object {
        fun fromScore(score: Int): RiskTier = when {
            score >= 60 -> LIKELY_SCAM
            score >= 30 -> SUSPICIOUS
            else -> SAFE
        }
    }
}
