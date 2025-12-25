package com.jmjlacosta.mahjongscorer.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jmjlacosta.mahjongscorer.model.Hand
import com.jmjlacosta.mahjongscorer.model.Tile
import com.jmjlacosta.mahjongscorer.scoring.Score
import com.jmjlacosta.mahjongscorer.scoring.ScoringEngine
import com.jmjlacosta.mahjongscorer.scoring.WinContext
import com.jmjlacosta.mahjongscorer.ui.components.AdaptiveTileInputLayout
import com.jmjlacosta.mahjongscorer.ui.components.CalculateButton
import com.jmjlacosta.mahjongscorer.ui.components.HandBuilder
import com.jmjlacosta.mahjongscorer.ui.components.ScoreDisplay
import com.jmjlacosta.mahjongscorer.ui.components.TileGrid
import com.jmjlacosta.mahjongscorer.ui.components.WinContextToggles

/**
 * Manual tile input screen for testing the scoring engine.
 * Allows selecting 14 tiles and calculating the score.
 */
@Composable
fun TileInputScreen(
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    // State for selected tiles
    val selectedTiles = remember { mutableStateListOf<Tile>() }

    // State for win context options
    var isSelfDraw by remember { mutableStateOf(false) }
    var isConcealed by remember { mutableStateOf(true) }

    // State for calculated score
    var score by remember { mutableStateOf<Score?>(null) }

    AdaptiveTileInputLayout(
        windowSizeClass = windowSizeClass,
        modifier = modifier,
        handBuilder = {
            HandBuilder(
                tiles = selectedTiles,
                onTileRemove = { index ->
                    selectedTiles.removeAt(index)
                    score = null // Clear score when hand changes
                },
                onClear = {
                    selectedTiles.clear()
                    score = null
                }
            )
            WinContextToggles(
                isSelfDraw = isSelfDraw,
                isConcealed = isConcealed,
                onSelfDrawChange = {
                    isSelfDraw = it
                    score = null // Recalculate needed
                },
                onConcealedChange = {
                    isConcealed = it
                    score = null // Recalculate needed
                }
            )
        },
        tileGrid = {
            TileGrid(
                onTileClick = { tile ->
                    if (selectedTiles.size < 14) {
                        selectedTiles.add(tile)
                        score = null // Clear score when hand changes
                    }
                }
            )
        },
        scoreDisplay = {
            Column {
                CalculateButton(
                    enabled = selectedTiles.size == 14,
                    onClick = {
                        val context = WinContext(
                            isSelfDraw = isSelfDraw,
                            isConcealed = isConcealed
                        )
                        val hand = Hand(
                            tiles = selectedTiles.toList(),
                            isConcealed = isConcealed
                        )
                        score = ScoringEngine.calculateScore(hand, context)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                ScoreDisplay(score = score)
            }
        }
    )
}
