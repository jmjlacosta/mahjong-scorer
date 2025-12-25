package com.jmjlacosta.mahjongscorer.model

/**
 * Represents a meld (set of tiles) in Mahjong.
 * A winning hand consists of 4 melds + 1 pair (standard) or special formations.
 */
sealed class Meld {
    /** All tiles in this meld */
    abstract val tiles: List<Tile>

    /** Chinese name of this meld type */
    abstract val chineseName: String

    /** Number of tiles in this meld */
    val size: Int get() = tiles.size

    // =========================================================================
    // Pong - 3 identical tiles (碰)
    // =========================================================================

    /**
     * A Pong is three identical tiles.
     * Example: 3 Red Dragons, 3 East Winds, 3 of 5-Dots
     */
    data class Pong(val tile: Tile) : Meld() {
        override val tiles: List<Tile> = listOf(tile, tile, tile)
        override val chineseName: String = "碰"
    }

    // =========================================================================
    // Kong - 4 identical tiles (杠)
    // =========================================================================

    /**
     * A Kong is four identical tiles.
     * Can be concealed (暗杠) or exposed (明杠).
     */
    data class Kong(
        val tile: Tile,
        val isConcealed: Boolean = false
    ) : Meld() {
        override val tiles: List<Tile> = listOf(tile, tile, tile, tile)
        override val chineseName: String = if (isConcealed) "暗杠" else "明杠"
    }

    // =========================================================================
    // Chow - 3 consecutive tiles of same suit (吃)
    // =========================================================================

    /**
     * A Chow is three consecutive numbered tiles in the same suit.
     * Example: 2-3-4 of Bamboo, 7-8-9 of Characters
     * Only numbered tiles (Dots, Bamboo, Characters) can form a Chow.
     */
    data class Chow(val startTile: Tile.NumberedTile) : Meld() {
        init {
            require(startTile.number <= 7) {
                "Chow must start with tile 1-7, got ${startTile.number}"
            }
        }

        override val tiles: List<Tile> = listOf(
            startTile,
            Tile.NumberedTile(startTile.suit, startTile.number + 1),
            Tile.NumberedTile(startTile.suit, startTile.number + 2)
        )
        override val chineseName: String = "吃"

        /** The three numbers in this chow (e.g., [2, 3, 4]) */
        val numbers: List<Int> = listOf(
            startTile.number,
            startTile.number + 1,
            startTile.number + 2
        )
    }

    // =========================================================================
    // Pair - 2 identical tiles (对)
    // =========================================================================

    /**
     * A Pair is two identical tiles.
     * Every standard winning hand has exactly one pair (the "eyes").
     */
    data class Pair(val tile: Tile) : Meld() {
        override val tiles: List<Tile> = listOf(tile, tile)
        override val chineseName: String = "对"
    }

    // =========================================================================
    // Companion utilities
    // =========================================================================

    companion object {
        /**
         * Try to create a Chow from three tiles.
         * Returns null if the tiles don't form a valid Chow.
         */
        fun tryChow(t1: Tile, t2: Tile, t3: Tile): Chow? {
            // All must be numbered tiles
            if (t1 !is Tile.NumberedTile || t2 !is Tile.NumberedTile || t3 !is Tile.NumberedTile) {
                return null
            }
            // All must be same suit
            if (t1.suit != t2.suit || t2.suit != t3.suit) {
                return null
            }
            // Sort by number and check consecutive
            val sorted = listOf(t1, t2, t3).sortedBy { it.number }
            if (sorted[1].number != sorted[0].number + 1 || sorted[2].number != sorted[1].number + 1) {
                return null
            }
            return Chow(sorted[0])
        }

        /**
         * Try to create a Pong from three tiles.
         * Returns null if the tiles aren't identical.
         */
        fun tryPong(t1: Tile, t2: Tile, t3: Tile): Pong? {
            return if (t1 == t2 && t2 == t3) Pong(t1) else null
        }

        /**
         * Try to create a Kong from four tiles.
         * Returns null if the tiles aren't identical.
         */
        fun tryKong(t1: Tile, t2: Tile, t3: Tile, t4: Tile, isConcealed: Boolean = false): Kong? {
            return if (t1 == t2 && t2 == t3 && t3 == t4) Kong(t1, isConcealed) else null
        }

        /**
         * Try to create a Pair from two tiles.
         * Returns null if the tiles aren't identical.
         */
        fun tryPair(t1: Tile, t2: Tile): Pair? {
            return if (t1 == t2) Pair(t1) else null
        }
    }
}
