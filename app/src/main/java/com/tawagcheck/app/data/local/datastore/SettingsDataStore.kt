package com.tawagcheck.app.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tawagcheck.app.data.model.AppLanguage
import com.tawagcheck.app.data.model.CallAction
import com.tawagcheck.app.data.model.RiskTier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "tawagcheck_settings")

private const val DEFAULT_UPDATE_URL =
    "https://github.com/genesiscruz0124/tawagcheck/releases/latest/download/scamlist.json"

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val PROTECTION_ENABLED = booleanPreferencesKey("protection_enabled")
        val CONTACTS_CHECK_ENABLED = booleanPreferencesKey("contacts_check_enabled")
        val ACTION_SAFE = stringPreferencesKey("action_safe")
        val ACTION_SUSPICIOUS = stringPreferencesKey("action_suspicious")
        val ACTION_LIKELY_SCAM = stringPreferencesKey("action_likely_scam")
        val HIDDEN_NUMBER_ACTION = stringPreferencesKey("hidden_number_action")
        val UPDATE_URL = stringPreferencesKey("update_url")
        val LAST_UPDATED_AT = longPreferencesKey("last_updated_at")
        val LANGUAGE = stringPreferencesKey("language")
    }

    val protectionEnabled: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.PROTECTION_ENABLED] ?: true }

    val contactsCheckEnabled: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.CONTACTS_CHECK_ENABLED] ?: false }

    val updateUrl: Flow<String> =
        context.dataStore.data.map { it[Keys.UPDATE_URL] ?: DEFAULT_UPDATE_URL }

    val lastUpdatedAt: Flow<Long?> =
        context.dataStore.data.map { it[Keys.LAST_UPDATED_AT] }

    val language: Flow<AppLanguage> = context.dataStore.data.map {
        AppLanguage.valueOf(it[Keys.LANGUAGE] ?: AppLanguage.ENGLISH.name)
    }

    fun actionFor(tier: RiskTier): Flow<CallAction> {
        val key = keyForTier(tier)
        val default = defaultActionFor(tier)
        return context.dataStore.data.map { prefs ->
            prefs[key]?.let { CallAction.valueOf(it) } ?: default
        }
    }

    val hiddenNumberAction: Flow<CallAction> = context.dataStore.data.map {
        it[Keys.HIDDEN_NUMBER_ACTION]?.let { name -> CallAction.valueOf(name) } ?: CallAction.WARN
    }

    suspend fun setProtectionEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PROTECTION_ENABLED] = enabled }
    }

    suspend fun setContactsCheckEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.CONTACTS_CHECK_ENABLED] = enabled }
    }

    suspend fun setActionFor(tier: RiskTier, action: CallAction) {
        context.dataStore.edit { it[keyForTier(tier)] = action.name }
    }

    suspend fun setHiddenNumberAction(action: CallAction) {
        context.dataStore.edit { it[Keys.HIDDEN_NUMBER_ACTION] = action.name }
    }

    suspend fun setUpdateUrl(url: String) {
        context.dataStore.edit { it[Keys.UPDATE_URL] = url }
    }

    suspend fun setLastUpdatedAt(timestampMillis: Long) {
        context.dataStore.edit { it[Keys.LAST_UPDATED_AT] = timestampMillis }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { it[Keys.LANGUAGE] = language.name }
    }

    private fun keyForTier(tier: RiskTier) = when (tier) {
        RiskTier.SAFE -> Keys.ACTION_SAFE
        RiskTier.SUSPICIOUS -> Keys.ACTION_SUSPICIOUS
        RiskTier.LIKELY_SCAM -> Keys.ACTION_LIKELY_SCAM
    }

    private fun defaultActionFor(tier: RiskTier) = when (tier) {
        RiskTier.SAFE -> CallAction.ALLOW
        RiskTier.SUSPICIOUS -> CallAction.WARN
        RiskTier.LIKELY_SCAM -> CallAction.REJECT
    }
}
