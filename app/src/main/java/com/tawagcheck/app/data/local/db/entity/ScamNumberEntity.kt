package com.tawagcheck.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tawagcheck.app.data.model.ScamCategory
import com.tawagcheck.app.data.model.ScamMatchType

@Entity(tableName = "scam_numbers")
data class ScamNumberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    /** Full E.164 number for FULL matches, or a digits-only prefix (e.g. "+63961") for PREFIX matches. */
    val number: String,
    val type: ScamMatchType,
    val category: ScamCategory,
    val source: String,
    val dateAdded: Long
)
