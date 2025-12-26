package com.jmjlacosta.mahjongscorer.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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

/**
 * Adaptive layout for the result display screen.
 * Uses side-by-side layout on expanded screens (Pixel Fold inner screen, tablets).
 * Uses stacked layout on compact screens (phones, Pixel Fold outer screen).
 */
@Composable
fun AdaptiveResultLayout(
    windowSizeClass: WindowSizeClass,
    tileVisualization: @Composable () -> Unit,
    scoreBreakdown: @Composable () -> Unit,
    actionButtons: @Composable () -> Unit,
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
                // Left side: Tile visualization + Actions
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    tileVisualization()
                    Spacer(modifier = Modifier.height(16.dp))
                    actionButtons()
                }
                // Right side: Score breakdown (scrollable)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    scoreBreakdown()
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
                tileVisualization()
                Spacer(modifier = Modifier.height(16.dp))
                scoreBreakdown()
                Spacer(modifier = Modifier.height(16.dp))
                actionButtons()
            }
        }
    }
}

/**
 * Adaptive layout for the camera preview screen.
 * Uses side-by-side layout on expanded screens (Pixel Fold inner screen, tablets).
 * Uses stacked layout on compact screens (phones, Pixel Fold outer screen).
 */
@Composable
fun AdaptiveCameraLayout(
    windowSizeClass: WindowSizeClass,
    cameraPreview: @Composable () -> Unit,
    controls: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Expanded -> {
            // Side-by-side layout for Pixel Fold inner screen (~7.6") or tablets
            Row(modifier = modifier.fillMaxSize()) {
                // Camera preview takes most of the space
                Box(
                    modifier = Modifier
                        .weight(0.75f)
                        .fillMaxHeight()
                ) {
                    cameraPreview()
                }
                // Controls on the side
                Box(
                    modifier = Modifier
                        .weight(0.25f)
                        .fillMaxHeight()
                ) {
                    controls()
                }
            }
        }
        else -> {
            // Stacked layout for phones or Pixel Fold outer screen (~6.2")
            Box(modifier = modifier.fillMaxSize()) {
                // Camera preview fills the screen
                cameraPreview()
                // Controls overlay at the bottom
                Box(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    controls()
                }
            }
        }
    }
}
