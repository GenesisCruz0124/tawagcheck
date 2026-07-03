package com.tawagcheck.app.domain.heuristics

import com.tawagcheck.app.data.local.contacts.ContactsLookup
import com.tawagcheck.app.data.local.datastore.SettingsDataStore
import com.tawagcheck.app.data.model.CallVerdict
import com.tawagcheck.app.data.model.RiskTier
import com.tawagcheck.app.data.repository.CallHistoryRepository
import com.tawagcheck.app.data.repository.ScamMatch
import com.tawagcheck.app.data.repository.ScamRepository
import com.tawagcheck.app.domain.normalization.PhoneNumberNormalizer
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

/** Scores an incoming call 0-100 and maps the result to the user's configured action. */
class HeuristicsEngine(
    private val scamRepository: ScamRepository,
    private val callHistoryRepository: CallHistoryRepository,
    private val contactsLookup: ContactsLookup,
    private val settingsDataStore: SettingsDataStore,
    private val normalizer: PhoneNumberNormalizer = PhoneNumberNormalizer()
) {

    suspend fun evaluate(rawNumber: String, isHidden: Boolean): CallVerdict {
        if (isHidden || rawNumber.isBlank()) {
            val action = settingsDataStore.hiddenNumberAction.first()
            return CallVerdict(
                rawNumber = rawNumber,
                normalizedNumber = null,
                isHidden = true,
                score = 0,
                tier = RiskTier.SUSPICIOUS,
                reasons = listOf("Hidden or private number"),
                action = action
            )
        }

        val normalized = normalizer.normalize(rawNumber)
        val lookupKey = normalized?.e164 ?: rawNumber
        val reasons = mutableListOf<String>()
        var score = 0

        when (val match = scamRepository.match(lookupKey)) {
            is ScamMatch.Full -> {
                score += FULL_MATCH_SCORE
                reasons += "Number matches known scam database entry (${match.category.name.lowercase().replace('_', ' ')})"
            }
            is ScamMatch.Prefix -> {
                score += PREFIX_MATCH_SCORE
                reasons += "Number prefix matches a known scam pattern (${match.category.name.lowercase().replace('_', ' ')})"
            }
            ScamMatch.None -> Unit
        }

        if (normalized != null && !normalized.isPhilippines) {
            score += INTERNATIONAL_SCORE
            reasons += "International number presenting in a local call"
        }

        if (settingsDataStore.contactsCheckEnabled.first() && normalized != null) {
            val isKnown = contactsLookup.isKnownContact(normalized.e164)
            if (isKnown == false) {
                val recentCalls = callHistoryRepository.recentCallCount(
                    normalized.e164,
                    TimeUnit.MINUTES.toMillis(REPEAT_WINDOW_MINUTES)
                )
                if (recentCalls >= REPEAT_CALL_THRESHOLD) {
                    score += REPEAT_CALL_SCORE
                    reasons += "Repeated calls from an unknown number within $REPEAT_WINDOW_MINUTES minutes"
                }
            }
        }

        score = score.coerceIn(0, 100)
        val tier = RiskTier.fromScore(score)
        val action = settingsDataStore.actionFor(tier).first()

        return CallVerdict(
            rawNumber = rawNumber,
            normalizedNumber = normalized?.e164,
            isHidden = false,
            score = score,
            tier = tier,
            reasons = reasons,
            action = action
        )
    }

    private companion object {
        const val FULL_MATCH_SCORE = 70
        const val PREFIX_MATCH_SCORE = 40
        const val INTERNATIONAL_SCORE = 20
        const val REPEAT_CALL_SCORE = 20
        const val REPEAT_CALL_THRESHOLD = 3
        const val REPEAT_WINDOW_MINUTES = 10L
    }
}
