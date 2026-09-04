package com.risket.app.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.risket.app.ui.RisketViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(tableId: Long, viewModel: RisketViewModel, navController: NavController) {
    val table by viewModel.tableFlow(tableId).collectAsState(initial = null)
    var text by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(table?.id) {
        if (text == null) {
            text = table?.noteContent ?: ""
        }
    }

    // debounce save
    LaunchedEffect(text) {
        val current = table
        val value = text
        if (current != null && value != null) {
            delay(500)
            viewModel.saveNote(current, value)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(table?.name ?: "Note") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        OutlinedTextField(
            value = text ?: "",
            onValueChange = { text = it },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            placeholder = { Text("Start writing...") }
        )
    }
}
