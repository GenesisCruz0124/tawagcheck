package com.tawagcheck.app.data.local.db

import androidx.room.TypeConverter
import com.tawagcheck.app.data.model.CallAction
import com.tawagcheck.app.data.model.RiskTier
import com.tawagcheck.app.data.model.ScamCategory
import com.tawagcheck.app.data.model.ScamMatchType

class Converters {
    @TypeConverter
    fun fromScamMatchType(value: ScamMatchType): String = value.name

    @TypeConverter
    fun toScamMatchType(value: String): ScamMatchType = ScamMatchType.valueOf(value)

    @TypeConverter
    fun fromScamCategory(value: ScamCategory): String = value.name

    @TypeConverter
    fun toScamCategory(value: String): ScamCategory = ScamCategory.valueOf(value)

    @TypeConverter
    fun fromRiskTier(value: RiskTier): String = value.name

    @TypeConverter
    fun toRiskTier(value: String): RiskTier = RiskTier.valueOf(value)

    @TypeConverter
    fun fromCallAction(value: CallAction): String = value.name

    @TypeConverter
    fun toCallAction(value: String): CallAction = CallAction.valueOf(value)
}
