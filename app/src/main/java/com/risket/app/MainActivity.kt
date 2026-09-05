package com.risket.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.risket.app.data.BackupHelper
import com.risket.app.ui.RisketViewModel
import com.risket.app.ui.RisketViewModelFactory
import com.risket.app.ui.av.AvTableScreen
import com.risket.app.ui.av.CreateAvTableScreen
import com.risket.app.ui.customtable.CreateCustomTableScreen
import com.risket.app.ui.customtable.CustomTableScreen
import com.risket.app.ui.home.HomeScreen
import com.risket.app.ui.notes.NoteScreen
import com.risket.app.ui.theme.RisketTheme
import com.risket.app.ui.todo.TodoScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as RisketApp

        setContent {
            RisketTheme {
                RisketNavHost(app)
            }
        }
    }
}

@Composable
fun RisketNavHost(app: RisketApp) {
    val navController = rememberNavController()
    val viewModel: RisketViewModel = viewModel(factory = RisketViewModelFactory(app.repository))
    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        if (uri != null) {
            BackupHelper.exportTo(context, uri)
            Toast.makeText(context, "Backup saved", Toast.LENGTH_SHORT).show()
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            BackupHelper.importFrom(context, uri)
            Toast.makeText(context, "Restored. Restart the app to see your data.", Toast.LENGTH_LONG).show()
        }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                navController = navController,
                onExport = { exportLauncher.launch("risket-backup.db") },
                onImport = { importLauncher.launch(arrayOf("application/octet-stream", "*/*")) }
            )
        }
        composable("create_av") {
            CreateAvTableScreen(viewModel = viewModel, navController = navController)
        }
        composable(
            "av_table/{tableId}",
            arguments = listOf(navArgument("tableId") { type = NavType.LongType })
        ) { backStackEntry ->
            val tableId = backStackEntry.arguments?.getLong("tableId") ?: 0L
            AvTableScreen(tableId = tableId, viewModel = viewModel, navController = navController)
        }
        composable(
            "note/{tableId}",
            arguments = listOf(navArgument("tableId") { type = NavType.LongType })
        ) { backStackEntry ->
            val tableId = backStackEntry.arguments?.getLong("tableId") ?: 0L
            NoteScreen(tableId = tableId, viewModel = viewModel, navController = navController)
        }
        composable(
            "todo/{tableId}",
            arguments = listOf(navArgument("tableId") { type = NavType.LongType })
        ) { backStackEntry ->
            val tableId = backStackEntry.arguments?.getLong("tableId") ?: 0L
            TodoScreen(tableId = tableId, viewModel = viewModel, navController = navController)
        }
        composable("create_custom") {
            CreateCustomTableScreen(viewModel = viewModel, navController = navController)
        }
        composable(
            "custom_table/{tableId}",
            arguments = listOf(navArgument("tableId") { type = NavType.LongType })
        ) { backStackEntry ->
            val tableId = backStackEntry.arguments?.getLong("tableId") ?: 0L
            CustomTableScreen(tableId = tableId, viewModel = viewModel, navController = navController)
        }
    }
}
