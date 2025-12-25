package com.jmjlacosta.mahjongscorer.scoring

import com.jmjlacosta.mahjongscorer.model.Tile

/**
 * Context information about how a hand was won.
 * This affects scoring calculations.
 */
data class WinContext(
    /** Did the player draw the winning tile themselves? */
    val isSelfDraw: Boolean = false,

    /** Is the hand concealed (no exposed melds)? */
    val isConcealed: Boolean = true,

    /** The player's seat wind (East, South, West, North) */
    val seatWind: Tile.Wind? = null,

    /** The current round wind */
    val roundWind: Tile.Wind? = null,

    /** Was this the last tile from the wall? (海底捞月 / 河底捞鱼) */
    val isLastTile: Boolean = false,

    /** Did the win come from drawing after declaring a Kong? (杠上开花) */
    val isKongDraw: Boolean = false,

    /** Was the winning tile robbed from someone's Kong? (抢杠) */
    val isRobbingKong: Boolean = false
) {
    companion object {
        /** Default context for a basic discard win */
        val DEFAULT = WinContext()

        /** Context for a self-drawn win */
        val SELF_DRAW = WinContext(isSelfDraw = true, isConcealed = true)

        /** Context for an exposed hand win (has melded tiles) */
        fun exposed(isSelfDraw: Boolean = false) = WinContext(
            isSelfDraw = isSelfDraw,
            isConcealed = false
        )
    }
}
