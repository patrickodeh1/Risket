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
    var text by remember { mutableStateOf("") }
    var loaded by remember { mutableStateOf(false) }

    // Load the saved content exactly once, when the table first arrives.
    LaunchedEffect(table?.id) {
        if (!loaded && table != null) {
            text = table?.noteContent ?: ""
            loaded = true
        }
    }

    // Debounced autosave, only active after initial load to avoid overwriting with blank text.
    LaunchedEffect(text, loaded) {
        val current = table
        if (loaded && current != null) {
            delay(500)
            viewModel.saveNote(current, text)
        }
    }

    // Safety net: save immediately when leaving the screen.
    DisposableEffect(Unit) {
        onDispose {
            val current = table
            if (loaded && current != null) {
                viewModel.saveNote(current, text)
            }
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
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            placeholder = { Text("Start writing...") }
        )
    }
}
