package com.risket.app.ui.av

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.risket.app.data.RowEntity
import com.risket.app.ui.RisketViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvTableScreen(tableId: Long, viewModel: RisketViewModel, navController: NavController) {
    val table by viewModel.tableFlow(tableId).collectAsState(initial = null)
    val rows by viewModel.rowsFlow(tableId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(table?.name ?: "AV table") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        val currentTable = table
        if (currentTable == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            SummaryBar(initialBalance = currentTable.initialBalance, rows = rows, isComplete = currentTable.isComplete)

            HeaderRow()

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(rows, key = { it.id }) { row ->
                    RowItem(
                        row = row,
                        onToggle = { viewModel.toggleRow(row, rows, currentTable) }
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun SummaryBar(initialBalance: Double, rows: List<RowEntity>, isComplete: Boolean) {
    val checkedCount = rows.count { it.checked }
    val currentBalance = rows.lastOrNull { it.checked }?.balance ?: initialBalance

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Progress", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("$checkedCount / 100", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("Current balance", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                "%.0f".format(currentBalance),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
    if (isComplete) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Table complete", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Medium)
        }
    }
    Divider()
}

@Composable
private fun HeaderRow() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("#", modifier = Modifier.width(32.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Text("Risk", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Text("Balance", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Text("Time", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(40.dp))
    }
}

private val timeFormat = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())

@Composable
private fun RowItem(row: RowEntity, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("${row.serialNumber}", modifier = Modifier.width(32.dp), style = MaterialTheme.typography.bodyMedium)
        Text("%.0f".format(row.risk), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Text("%.0f".format(row.balance), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Text(
            if (row.checked) timeFormat.format(Date(row.updatedAt)) else "-",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Checkbox(checked = row.checked, onCheckedChange = { onToggle() })
    }
}
