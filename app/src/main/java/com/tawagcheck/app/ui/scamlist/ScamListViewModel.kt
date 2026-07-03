package com.tawagcheck.app.ui.scamlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tawagcheck.app.data.local.db.entity.ScamNumberEntity
import com.tawagcheck.app.data.model.ScamCategory
import com.tawagcheck.app.data.model.ScamMatchType
import com.tawagcheck.app.data.repository.ScamRepository
import com.tawagcheck.app.domain.normalization.PhoneNumberNormalizer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScamListViewModel(
    private val scamRepository: ScamRepository,
    private val normalizer: PhoneNumberNormalizer = PhoneNumberNormalizer()
) : ViewModel() {

    val entries: StateFlow<List<ScamNumberEntity>> = scamRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Normalizes and adds a number. Returns true on success, false if the number couldn't be parsed. */
    fun addEntry(rawNumber: String, type: ScamMatchType, category: ScamCategory, onResult: (Boolean) -> Unit) {
        val normalized = normalizer.normalize(rawNumber)
        val numberToStore = when (type) {
            ScamMatchType.FULL -> normalized?.e164
            // Prefixes aren't full numbers, so they won't parse via libphonenumber - store as
            // typed, just making sure it carries a country code like the rest of the database.
            ScamMatchType.PREFIX -> rawNumber.trim().let { if (it.startsWith("+")) it else "+63${it.trimStart('0')}" }
        }

        if (numberToStore.isNullOrBlank()) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            scamRepository.addEntry(numberToStore, type, category)
            onResult(true)
        }
    }
}
