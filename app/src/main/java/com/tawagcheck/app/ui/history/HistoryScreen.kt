package com.tawagcheck.app.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tawagcheck.app.data.local.db.entity.CallHistoryEntity
import com.tawagcheck.app.data.model.RiskTier
import com.tawagcheck.app.ui.common.RiskBadge
import com.tawagcheck.app.ui.strings.LocalStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(viewModel: HistoryViewModel, modifier: Modifier = Modifier) {
    val strings = LocalStrings.current
    val calls by viewModel.calls.collectAsStateWithLifecycle()
    val selectedTier by viewModel.selectedTier.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = strings.historyTitle, style = MaterialTheme.typography.headlineMedium)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTier == null,
                    onClick = { viewModel.selectTier(null) },
                    label = { Text(strings.filterAll) }
                )
                RiskTier.entries.forEach { tier ->
                    FilterChip(
                        selected = selectedTier == tier,
                        onClick = { viewModel.selectTier(tier) },
                        label = { Text(strings.riskTierLabel(tier)) }
                    )
                }
            }

            if (calls.isEmpty()) {
                Text(text = strings.historyEmpty, style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(calls, key = { it.id }) { call ->
                        CallHistoryRow(call)
                    }
                }
            }
    }
}

@Composable
private fun CallHistoryRow(call: CallHistoryEntity) {
    val strings = LocalStrings.current
    val dateFormat = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = call.normalizedNumber ?: call.rawNumber.ifBlank { "Hidden number" },
                    style = MaterialTheme.typography.titleMedium
                )
                RiskBadge(tier = call.tier)
            }
            Text(
                text = "${dateFormat.format(Date(call.timestamp))} • ${strings.callActionLabel(call.action)}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (call.reasons.isNotBlank()) {
                Text(
                    text = call.reasons.replace("|", ", "),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
