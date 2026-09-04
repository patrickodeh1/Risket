package com.risket.app.ui.av

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.risket.app.ui.RisketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAvTableScreen(viewModel: RisketViewModel, navController: NavController) {
    var name by remember { mutableStateOf("") }
    var balanceText by remember { mutableStateOf("1000") }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New AV table") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Table name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = balanceText,
                onValueChange = { balanceText = it },
                label = { Text("Initial balance") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    val balance = balanceText.toDoubleOrNull()
                    when {
                        name.isBlank() -> error = "Give the table a name"
                        balance == null || balance <= 0 -> error = "Enter a valid initial balance"
                        else -> {
                            error = null
                            viewModel.createAvTable(name.trim(), balance) { id ->
                                navController.navigate("av_table/$id") {
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
