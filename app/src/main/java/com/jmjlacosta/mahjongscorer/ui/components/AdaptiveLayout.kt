package com.jmjlacosta.mahjongscorer.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Adaptive layout for the tile input screen.
 * Uses side-by-side layout on expanded screens (Pixel Fold inner screen, tablets).
 * Uses stacked layout on compact screens (phones, Pixel Fold outer screen).
 */
@Composable
fun AdaptiveTileInputLayout(
    windowSizeClass: WindowSizeClass,
    handBuilder: @Composable () -> Unit,
    tileGrid: @Composable () -> Unit,
    scoreDisplay: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Expanded -> {
            // Side-by-side layout for Pixel Fold inner screen (~7.6") or tablets
            Row(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Left side: Tile grid
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    tileGrid()
                }
                // Right side: Hand builder + Score display
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    handBuilder()
                    scoreDisplay()
                }
            }
        }
        else -> {
            // Stacked layout for phones or Pixel Fold outer screen (~6.2")
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                handBuilder()
                tileGrid()
                scoreDisplay()
            }
        }
    }
}
