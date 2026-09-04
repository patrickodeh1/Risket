package com.risket.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.risket.app.ui.RisketViewModel
import com.risket.app.ui.RisketViewModelFactory
import com.risket.app.ui.av.AvTableScreen
import com.risket.app.ui.av.CreateAvTableScreen
import com.risket.app.ui.customtable.CreateCustomTableScreen
import com.risket.app.ui.customtable.CustomTableScreen
import com.risket.app.ui.home.HomeScreen
import com.risket.app.ui.notes.NoteScreen
import com.risket.app.ui.theme.RisketTheme

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

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(viewModel = viewModel, navController = navController)
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
