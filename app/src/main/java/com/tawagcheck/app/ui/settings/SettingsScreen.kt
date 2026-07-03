package com.tawagcheck.app.ui.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tawagcheck.app.data.model.AppLanguage
import com.tawagcheck.app.data.model.CallAction
import com.tawagcheck.app.data.model.RiskTier
import com.tawagcheck.app.ui.strings.LocalStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    val strings = LocalStrings.current
    val context = LocalContext.current

    val safeAction by viewModel.safeAction.collectAsStateWithLifecycle()
    val suspiciousAction by viewModel.suspiciousAction.collectAsStateWithLifecycle()
    val likelyScamAction by viewModel.likelyScamAction.collectAsStateWithLifecycle()
    val hiddenNumberAction by viewModel.hiddenNumberAction.collectAsStateWithLifecycle()
    val contactsCheckEnabled by viewModel.contactsCheckEnabled.collectAsStateWithLifecycle()
    val updateUrl by viewModel.updateUrl.collectAsStateWithLifecycle()
    val lastUpdatedAt by viewModel.lastUpdatedAt.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val updateState by viewModel.updateState.collectAsStateWithLifecycle()

    var urlFieldValue by remember(updateUrl) { mutableStateOf(updateUrl) }

    val contactsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> viewModel.setContactsCheckEnabled(granted) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = strings.settingsTitle, style = MaterialTheme.typography.headlineMedium)

            Text(text = strings.settingsActionsSection, style = MaterialTheme.typography.titleMedium)
            ActionPicker(strings.riskTierLabel(RiskTier.SAFE), safeAction) {
                viewModel.setActionForTier(RiskTier.SAFE, it)
            }
            ActionPicker(strings.riskTierLabel(RiskTier.SUSPICIOUS), suspiciousAction) {
                viewModel.setActionForTier(RiskTier.SUSPICIOUS, it)
            }
            ActionPicker(strings.riskTierLabel(RiskTier.LIKELY_SCAM), likelyScamAction) {
                viewModel.setActionForTier(RiskTier.LIKELY_SCAM, it)
            }
            ActionPicker(strings.settingsHiddenNumberPolicy, hiddenNumberAction) {
                viewModel.setHiddenNumberAction(it)
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = strings.settingsContactsPermission, style = MaterialTheme.typography.titleMedium)
                    Text(text = strings.settingsContactsPermissionDesc, style = MaterialTheme.typography.bodyMedium)
                }
                Switch(
                    checked = contactsCheckEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.READ_CONTACTS
                            ) == PackageManager.PERMISSION_GRANTED
                            if (hasPermission) {
                                viewModel.setContactsCheckEnabled(true)
                            } else {
                                contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                            }
                        } else {
                            viewModel.setContactsCheckEnabled(false)
                        }
                    }
                )
            }

            HorizontalDivider()

            Text(text = strings.settingsUpdateSection, style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = urlFieldValue,
                onValueChange = { urlFieldValue = it },
                label = { Text(strings.settingsUpdateUrlLabel) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        viewModel.setUpdateUrl(urlFieldValue)
                        viewModel.updateDatabase()
                    },
                    enabled = updateState !is UpdateState.Updating
                ) {
                    Text(if (updateState is UpdateState.Updating) strings.settingsUpdating else strings.settingsUpdateButton)
                }
                val lastUpdatedText = lastUpdatedAt?.let {
                    SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault()).format(Date(it))
                } ?: strings.settingsNeverUpdated
                Text(text = "${strings.settingsLastUpdated}: $lastUpdatedText", style = MaterialTheme.typography.bodyMedium)
            }
            when (val state = updateState) {
                is UpdateState.Success -> Text(
                    text = "Updated: ${state.count} entries",
                    style = MaterialTheme.typography.bodyMedium
                )
                is UpdateState.Error -> Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                else -> Unit
            }

            HorizontalDivider()

            Text(text = strings.settingsLanguage, style = MaterialTheme.typography.titleMedium)
            SingleChoiceSegmentedButtonRow {
                AppLanguage.entries.forEachIndexed { index, lang ->
                    SegmentedButton(
                        selected = language == lang,
                        onClick = { viewModel.setLanguage(lang) },
                        shape = SegmentedButtonDefaults.itemShape(index, AppLanguage.entries.size)
                    ) {
                        Text(if (lang == AppLanguage.ENGLISH) "English" else "Taglish")
                    }
                }
            }

            HorizontalDivider()

            Text(text = strings.settingsExportCsv, style = MaterialTheme.typography.titleMedium)
            Text(text = strings.settingsExportCsvDesc, style = MaterialTheme.typography.bodyMedium)
            Button(onClick = {
                viewModel.exportCsv(context) { uri ->
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, strings.settingsExportCsv))
                }
            }) {
                Text(strings.settingsExportCsv)
            }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun ActionPicker(label: String, selected: CallAction, onSelected: (CallAction) -> Unit) {
    val strings = LocalStrings.current
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = strings.callActionLabel(selected),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(androidx.compose.material3.ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                CallAction.entries.forEach { action ->
                    DropdownMenuItem(
                        text = { Text(strings.callActionLabel(action)) },
                        onClick = {
                            onSelected(action)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
