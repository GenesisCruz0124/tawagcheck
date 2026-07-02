package com.tawagcheck.app.domain.normalization

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil

data class NormalizedNumber(
    val e164: String,
    val regionCode: String?,
    val isPhilippines: Boolean
)

/** Normalizes raw caller-ID numbers to E.164, defaulting to the PH region for local formats. */
class PhoneNumberNormalizer {

    private val phoneUtil = PhoneNumberUtil.getInstance()

    fun normalize(rawNumber: String): NormalizedNumber? {
        val cleaned = rawNumber.trim()
        if (cleaned.isEmpty()) return null

        parseWithRegion(cleaned, DEFAULT_REGION)?.let { return it }

        // Fallback for numbers already carrying a country code but missing the leading '+'.
        if (!cleaned.startsWith("+")) {
            parseWithRegion("+$cleaned", null)?.let { return it }
        }

        return null
    }

    private fun parseWithRegion(number: String, region: String?): NormalizedNumber? {
        return try {
            val parsed = phoneUtil.parse(number, region)
            if (!phoneUtil.isValidNumber(parsed)) return null
            val regionCode = phoneUtil.getRegionCodeForNumber(parsed)
            NormalizedNumber(
                e164 = phoneUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164),
                regionCode = regionCode,
                isPhilippines = regionCode == DEFAULT_REGION
            )
        } catch (_: NumberParseException) {
            null
        }
    }

    private companion object {
        const val DEFAULT_REGION = "PH"
    }
}
