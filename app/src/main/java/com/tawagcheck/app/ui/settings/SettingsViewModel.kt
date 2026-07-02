package com.tawagcheck.app.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tawagcheck.app.data.local.datastore.SettingsDataStore
import com.tawagcheck.app.data.model.AppLanguage
import com.tawagcheck.app.data.model.CallAction
import com.tawagcheck.app.data.model.RiskTier
import com.tawagcheck.app.data.remote.ScamListUpdateResult
import com.tawagcheck.app.data.remote.ScamListUpdateService
import com.tawagcheck.app.data.repository.CallHistoryRepository
import com.tawagcheck.app.data.repository.ScamRepository
import com.tawagcheck.app.util.CsvExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class UpdateState {
    data object Idle : UpdateState()
    data object Updating : UpdateState()
    data class Success(val count: Int) : UpdateState()
    data class Error(val message: String) : UpdateState()
}

class SettingsViewModel(
    private val settingsDataStore: SettingsDataStore,
    private val scamRepository: ScamRepository,
    private val callHistoryRepository: CallHistoryRepository,
    private val scamListUpdateService: ScamListUpdateService
) : ViewModel() {

    val safeAction: StateFlow<CallAction> = settingsDataStore.actionFor(RiskTier.SAFE)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CallAction.ALLOW)
    val suspiciousAction: StateFlow<CallAction> = settingsDataStore.actionFor(RiskTier.SUSPICIOUS)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CallAction.WARN)
    val likelyScamAction: StateFlow<CallAction> = settingsDataStore.actionFor(RiskTier.LIKELY_SCAM)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CallAction.REJECT)
    val hiddenNumberAction: StateFlow<CallAction> = settingsDataStore.hiddenNumberAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CallAction.WARN)

    val contactsCheckEnabled: StateFlow<Boolean> = settingsDataStore.contactsCheckEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val updateUrl: StateFlow<String> = settingsDataStore.updateUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    val lastUpdatedAt: StateFlow<Long?> = settingsDataStore.lastUpdatedAt
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val language: StateFlow<AppLanguage> = settingsDataStore.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppLanguage.ENGLISH)

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    fun setActionForTier(tier: RiskTier, action: CallAction) {
        viewModelScope.launch { settingsDataStore.setActionFor(tier, action) }
    }

    fun setHiddenNumberAction(action: CallAction) {
        viewModelScope.launch { settingsDataStore.setHiddenNumberAction(action) }
    }

    fun setContactsCheckEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setContactsCheckEnabled(enabled) }
    }

    fun setUpdateUrl(url: String) {
        viewModelScope.launch { settingsDataStore.setUpdateUrl(url) }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { settingsDataStore.setLanguage(language) }
    }

    fun updateDatabase() {
        viewModelScope.launch {
            _updateState.value = UpdateState.Updating
            val url = settingsDataStore.updateUrl.first()
            when (val result = scamListUpdateService.fetchUpdate(url)) {
                is ScamListUpdateResult.Success -> {
                    scamRepository.replaceRemoteEntries(result.entries)
                    val now = System.currentTimeMillis()
                    settingsDataStore.setLastUpdatedAt(now)
                    _updateState.value = UpdateState.Success(result.entries.size)
                }
                is ScamListUpdateResult.Failure -> {
                    _updateState.value = UpdateState.Error(result.message)
                }
            }
        }
    }

    fun exportCsv(context: Context, onReady: (android.net.Uri) -> Unit) {
        viewModelScope.launch {
            val calls = callHistoryRepository.observeAll().first()
            val uri = CsvExporter.export(context, calls)
            onReady(uri)
        }
    }
}
