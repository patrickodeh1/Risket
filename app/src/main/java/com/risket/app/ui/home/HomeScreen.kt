package com.risket.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.risket.app.data.TYPE_AV
import com.risket.app.data.TYPE_CUSTOM
import com.risket.app.data.TYPE_NOTE
import com.risket.app.data.TYPE_TODO
import com.risket.app.data.TableEntity
import com.risket.app.ui.RisketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: RisketViewModel,
    navController: NavController,
    onExport: () -> Unit,
    onImport: () -> Unit
) {
    val tables by viewModel.tables.collectAsState(initial = emptyList())
    var showCreateMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Risket", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onExport) {
                        Icon(Icons.Default.Upload, contentDescription = "Backup")
                    }
                    IconButton(onClick = onImport) {
                        Icon(Icons.Default.Download, contentDescription = "Restore")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateMenu = true }) {
                Icon(Icons.Default.Add, contentDescription = "Create table")
            }
        }
    ) { padding ->
        if (tables.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No tables yet. Tap + to create one.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tables, key = { it.id }) { table ->
                    TableCard(
                        table = table,
                        onClick = {
                            when (table.type) {
                                TYPE_AV -> navController.navigate("av_table/${table.id}")
                                TYPE_NOTE -> navController.navigate("note/${table.id}")
                                TYPE_TODO -> navController.navigate("todo/${table.id}")
                                TYPE_CUSTOM -> navController.navigate("custom_table/${table.id}")
                            }
                        },
                        onDelete = { viewModel.deleteTable(table) },
                        onRename = { newName -> viewModel.renameTable(table, newName) }
                    )
                }
            }
        }
    }

    if (showCreateMenu) {
        CreateMenuSheet(
            onDismiss = { showCreateMenu = false },
            onPickAv = {
                showCreateMenu = false
                navController.navigate("create_av")
            },
            onPickNote = {
                showCreateMenu = false
                viewModel.createNoteTable("New Note") { id ->
                    navController.navigate("note/$id")
                }
            },
            onPickTodo = {
                showCreateMenu = false
                viewModel.createTodoTable("New To-do") { id ->
                    navController.navigate("todo/$id")
                }
            },
            onPickCustom = {
                showCreateMenu = false
                navController.navigate("create_custom")
            }
        )
    }
}

@Composable
private fun TableCard(
    table: TableEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onRename: (String) -> Unit
) {
    var showRenameDialog by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).clickable(onClick = onClick)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(table.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    if (table.isComplete) {
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Complete",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    when (table.type) {
                        TYPE_AV -> "AV table  •  initial balance ${"%.0f".format(table.initialBalance)}"
                        TYPE_NOTE -> "Note"
                        TYPE_TODO -> "To-do list"
                        else -> "Custom table"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { showRenameDialog = true }) {
                Icon(Icons.Default.Edit, contentDescription = "Rename")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }

    if (showRenameDialog) {
        RenameDialog(
            currentName = table.name,
            onDismiss = { showRenameDialog = false },
            onConfirm = {
                onRename(it)
                showRenameDialog = false
            }
        )
    }
}

@Composable
private fun RenameDialog(currentName: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename table") },
        text = {
            OutlinedTextField(value = name, onValueChange = { name = it }, singleLine = true)
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name.trim()) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateMenuSheet(
    onDismiss: () -> Unit,
    onPickAv: () -> Unit,
    onPickNote: () -> Unit,
    onPickTodo: () -> Unit,
    onPickCustom: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp).padding(bottom = 24.dp)) {
            Text("New table", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            CreateOptionRow("AV (risk / balance)", "Compounding 5% risk table, serials 1 to 100", onPickAv)
            CreateOptionRow("Note", "A plain freeform note", onPickNote)
            CreateOptionRow("To-do list", "Checklist with checkable items", onPickTodo)
            CreateOptionRow("Custom table", "Choose your own rows and columns", onPickCustom)
        }
    }
}

@Composable
private fun CreateOptionRow(title: String, subtitle: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
