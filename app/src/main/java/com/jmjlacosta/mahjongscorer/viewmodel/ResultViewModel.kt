package com.jmjlacosta.mahjongscorer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.jmjlacosta.mahjongscorer.model.Tile
import com.jmjlacosta.mahjongscorer.scoring.Score
import com.jmjlacosta.mahjongscorer.scoring.WinContext

/**
 * ViewModel for sharing result data between TileInputScreen and ResultScreen.
 */
class ResultViewModel : ViewModel() {
    /** The tiles in the winning hand */
    var tiles: List<Tile> by mutableStateOf(emptyList())
        private set

    /** The calculated score */
    var score: Score? by mutableStateOf(null)
        private set

    /** The win context used for calculation */
    var winContext: WinContext by mutableStateOf(WinContext())
        private set

    /**
     * Set the result data to be displayed.
     */
    fun setResult(tiles: List<Tile>, score: Score, winContext: WinContext) {
        this.tiles = tiles
        this.score = score
        this.winContext = winContext
    }

    /**
     * Clear the result data.
     */
    fun clear() {
        tiles = emptyList()
        score = null
        winContext = WinContext()
    }
}
