package com.risket.app.ui.customtable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.risket.app.ui.RisketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomTableScreen(viewModel: RisketViewModel, navController: NavController) {
    var name by remember { mutableStateOf("") }
    var rowCountText by remember { mutableStateOf("10") }
    val columns = remember { mutableStateListOf("Column 1") }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New custom table") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Table name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = rowCountText,
                onValueChange = { rowCountText = it },
                label = { Text("Number of rows") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Text("Columns", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(columns.size) { index ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = columns[index],
                            onValueChange = { columns[index] = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Column ${index + 1}") }
                        )
                        if (columns.size > 1) {
                            IconButton(onClick = { columns.removeAt(index) }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove column")
                            }
                        }
                    }
                }
            }

            TextButton(onClick = { columns.add("Column ${columns.size + 1}") }) {
                Text("+ Add column")
            }

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    val rowCount = rowCountText.toIntOrNull()
                    when {
                        name.isBlank() -> error = "Give the table a name"
                        rowCount == null || rowCount <= 0 -> error = "Enter a valid row count"
                        columns.any { it.isBlank() } -> error = "Column names cannot be empty"
                        else -> {
                            error = null
                            viewModel.createCustomTable(name.trim(), columns.toList(), rowCount) { id ->
                                navController.navigate("custom_table/$id") {
                                    popUpTo("home")
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create")
            }
        }
    }
}
