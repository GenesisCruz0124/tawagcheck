package com.tawagcheck.app.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tawagcheck.app.data.local.db.entity.CallHistoryEntity
import com.tawagcheck.app.data.model.RiskTier
import com.tawagcheck.app.data.repository.CallHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(
    private val callHistoryRepository: CallHistoryRepository
) : ViewModel() {

    private val _selectedTier = MutableStateFlow<RiskTier?>(null)
    val selectedTier: StateFlow<RiskTier?> = _selectedTier.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val calls: StateFlow<List<CallHistoryEntity>> = _selectedTier
        .flatMapLatest { tier ->
            if (tier == null) callHistoryRepository.observeAll() else callHistoryRepository.observeByTier(tier)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectTier(tier: RiskTier?) {
        _selectedTier.value = tier
    }
}
