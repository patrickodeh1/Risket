package com.risket.app.ui.todo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.risket.app.data.TodoItemEntity
import com.risket.app.ui.RisketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(tableId: Long, viewModel: RisketViewModel, navController: NavController) {
    val table by viewModel.tableFlow(tableId).collectAsState(initial = null)
    val items by viewModel.todoItemsFlow(tableId).collectAsState(initial = emptyList())
    var newItemText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(table?.name ?: "To-do") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newItemText,
                    onValueChange = { newItemText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Add an item") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    if (newItemText.isNotBlank()) {
                        viewModel.addTodoItem(tableId, newItemText.trim(), items.size)
                        newItemText = ""
                    }
                }) {
                    Text("Add")
                }
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items, key = { item -> item.id }) { item ->
                    TodoRow(
                        item = item,
                        onToggle = { viewModel.toggleTodoItem(item) },
                        onDelete = { viewModel.deleteTodoItem(item) }
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun TodoRow(item: TodoItemEntity, onToggle: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = item.checked, onCheckedChange = { onToggle() })
        Text(
            item.text,
            modifier = Modifier.weight(1f),
            style = if (item.checked) {
                MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                )
            } else {
                MaterialTheme.typography.bodyLarge
            }
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete item")
        }
    }
}
