package com.tawagcheck.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tawagcheck.app.data.local.datastore.SettingsDataStore
import com.tawagcheck.app.data.local.db.dao.DailyFlaggedCount
import com.tawagcheck.app.data.repository.CallHistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val settingsDataStore: SettingsDataStore,
    callHistoryRepository: CallHistoryRepository
) : ViewModel() {

    val protectionEnabled: StateFlow<Boolean> = settingsDataStore.protectionEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val screenedToday: StateFlow<Int> = callHistoryRepository.observeScreenedToday()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val scamsBlocked: StateFlow<Int> = callHistoryRepository.observeCumulativeBlocked()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val last7DaysFlagged: StateFlow<List<DailyFlaggedCount>> = callHistoryRepository.observeLast7DaysFlagged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setProtectionEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setProtectionEnabled(enabled) }
    }
}
