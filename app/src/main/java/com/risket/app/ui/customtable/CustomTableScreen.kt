package com.risket.app.ui.customtable

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.risket.app.ui.RisketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTableScreen(tableId: Long, viewModel: RisketViewModel, navController: NavController) {
    val table by viewModel.tableFlow(tableId).collectAsState(initial = null)
    val columns by viewModel.columnsFlow(tableId).collectAsState(initial = emptyList())
    val cells by viewModel.cellsFlow(tableId).collectAsState(initial = emptyList())

    val rowCount = remember(cells, columns) {
        if (columns.isEmpty()) 0 else cells.size / columns.size
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(table?.name ?: "Custom table") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (columns.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val cellWidth = 140.dp
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .horizontalScroll(scrollState)
        ) {
            Row {
                Text(
                    "#",
                    modifier = Modifier.width(40.dp).padding(8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                columns.forEach { col ->
                    Text(
                        col.name,
                        modifier = Modifier.width(cellWidth).padding(8.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Divider()

            LazyColumn {
                items(rowCount) { rowIndex ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${rowIndex + 1}",
                            modifier = Modifier.width(40.dp).padding(8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        columns.forEach { col ->
                            val cell = cells.firstOrNull { it.rowIndex == rowIndex && it.columnId == col.id }
                            if (cell != null) {
                                var value by remember(cell.id) { mutableStateOf(cell.value) }
                                OutlinedTextField(
                                    value = value,
                                    onValueChange = {
                                        value = it
                                        viewModel.updateCell(cell, it)
                                    },
                                    modifier = Modifier.width(cellWidth).padding(4.dp),
                                    singleLine = true
                                )
                            } else {
                                Spacer(modifier = Modifier.width(cellWidth))
                            }
                        }
                    }
                    Divider()
                }
            }

            // Add row button
            Button(
                onClick = { viewModel.addCustomRow(tableId, columns, rowCount) },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text("Add row")
            }
        }
    }
}
