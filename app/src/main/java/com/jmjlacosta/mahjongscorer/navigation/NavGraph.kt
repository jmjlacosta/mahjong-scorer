package com.jmjlacosta.mahjongscorer.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jmjlacosta.mahjongscorer.ui.screens.ResultScreen
import com.jmjlacosta.mahjongscorer.ui.screens.TileInputScreen
import com.jmjlacosta.mahjongscorer.viewmodel.ResultViewModel

/**
 * Navigation routes for the app.
 */
sealed class Screen(val route: String) {
    data object TileInput : Screen("tile_input")
    data object Result : Screen("result")
    // Future routes:
    // data object Camera : Screen("camera")
}

/**
 * Main navigation graph for the Mahjong Scorer app.
 */
@Composable
fun MahjongNavGraph(
    navController: NavHostController,
    windowSizeClass: WindowSizeClass,
    resultViewModel: ResultViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.TileInput.route
    ) {
        composable(Screen.TileInput.route) {
            TileInputScreen(
                windowSizeClass = windowSizeClass,
                onNavigateToResult = { tiles, score, winContext ->
                    resultViewModel.setResult(tiles, score, winContext)
                    navController.navigate(Screen.Result.route)
                }
            )
        }
        composable(Screen.Result.route) {
            ResultScreen(
                windowSizeClass = windowSizeClass,
                tiles = resultViewModel.tiles,
                score = resultViewModel.score,
                winContext = resultViewModel.winContext,
                onScanAnother = {
                    navController.popBackStack()
                }
            )
        }
    }
}
