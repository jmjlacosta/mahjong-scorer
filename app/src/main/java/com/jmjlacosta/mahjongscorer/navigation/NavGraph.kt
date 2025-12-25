package com.jmjlacosta.mahjongscorer.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jmjlacosta.mahjongscorer.ui.screens.TileInputScreen

/**
 * Navigation routes for the app.
 */
sealed class Screen(val route: String) {
    data object TileInput : Screen("tile_input")
    // Future routes:
    // data object Camera : Screen("camera")
    // data object Result : Screen("result")
}

/**
 * Main navigation graph for the Mahjong Scorer app.
 */
@Composable
fun MahjongNavGraph(
    navController: NavHostController,
    windowSizeClass: WindowSizeClass
) {
    NavHost(
        navController = navController,
        startDestination = Screen.TileInput.route
    ) {
        composable(Screen.TileInput.route) {
            TileInputScreen(windowSizeClass = windowSizeClass)
        }
    }
}
