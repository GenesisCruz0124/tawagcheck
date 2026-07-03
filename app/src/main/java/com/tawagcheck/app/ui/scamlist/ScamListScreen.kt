package com.tawagcheck.app.ui.scamlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tawagcheck.app.data.local.db.entity.ScamNumberEntity
import com.tawagcheck.app.data.model.ScamCategory
import com.tawagcheck.app.data.model.ScamMatchType
import com.tawagcheck.app.ui.strings.LocalStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ScamListScreen(viewModel: ScamListViewModel, modifier: Modifier = Modifier) {
    val strings = LocalStrings.current
    val entries by viewModel.entries.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = strings.scamListAddButton)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(text = strings.scamListTitle, style = MaterialTheme.typography.headlineMedium)

            Box(modifier = Modifier.padding(top = 12.dp)) {
                if (entries.isEmpty()) {
                    Text(text = strings.scamListEmpty, style = MaterialTheme.typography.bodyLarge)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(entries, key = { it.id }) { entry ->
                            ScamEntryRow(entry)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddScamNumberDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { number, type, category ->
                viewModel.addEntry(number, type, category) { showAddDialog = false }
            }
        )
    }
}

@Composable
private fun ScamEntryRow(entry: ScamNumberEntity) {
    val strings = LocalStrings.current
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = entry.number, style = MaterialTheme.typography.titleMedium)
                Text(text = strings.scamMatchTypeLabel(entry.type), style = MaterialTheme.typography.labelLarge)
            }
            Text(
                text = "${strings.scamCategoryLabel(entry.category)} • ${dateFormat.format(Date(entry.dateAdded))}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddScamNumberDialog(
    onDismiss: () -> Unit,
    onConfirm: (number: String, type: ScamMatchType, category: ScamCategory) -> Unit
) {
    val strings = LocalStrings.current
    var number by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ScamMatchType.FULL) }
    var category by remember { mutableStateOf(ScamCategory.USER_REPORTED) }
    var typeExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.scamListAddTitle) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text(strings.scamListNumberLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }) {
                    OutlinedTextField(
                        value = strings.scamMatchTypeLabel(type),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(strings.scamListTypeLabel) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                    )
                    ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                        ScamMatchType.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(strings.scamMatchTypeLabel(option)) },
                                onClick = {
                                    type = option
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = it }) {
                    OutlinedTextField(
                        value = strings.scamCategoryLabel(category),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(strings.scamListCategoryLabel) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                    )
                    ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                        ScamCategory.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(strings.scamCategoryLabel(option)) },
                                onClick = {
                                    category = option
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(number, type, category) },
                enabled = number.isNotBlank()
            ) {
                Text(strings.scamListAddButton)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.scamListCancelButton)
            }
        }
    )
}
