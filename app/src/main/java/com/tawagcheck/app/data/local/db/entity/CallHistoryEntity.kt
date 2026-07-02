package com.tawagcheck.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tawagcheck.app.data.model.CallAction
import com.tawagcheck.app.data.model.RiskTier

@Entity(tableName = "call_history")
data class CallHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rawNumber: String,
    val normalizedNumber: String?,
    val isHidden: Boolean,
    val timestamp: Long,
    val score: Int,
    val tier: RiskTier,
    /** Human-readable reasons the heuristics engine flagged this call, joined with "|". */
    val reasons: String,
    val action: CallAction
)
