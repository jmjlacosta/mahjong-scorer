package com.jmjlacosta.mahjongscorer.model

/**
 * Represents a single mahjong tile.
 * Beijing Mahjong has 34 unique tile types (no flowers).
 *
 * Tiles are organized into:
 * - Numbered tiles (1-9): Dots, Bamboo, Characters (27 tiles)
 * - Honor tiles: Winds (4) + Dragons (3) = 7 tiles
 * Total: 34 unique tiles
 */
sealed class Tile {
    abstract val suit: Suit
    abstract val name: String
    abstract val chineseName: String

    /** Unique identifier for this tile type */
    abstract val id: String

    /** Check if this is a terminal tile (1 or 9 of numbered suits) */
    open val isTerminal: Boolean = false

    /** Check if this is an honor tile (wind or dragon) */
    val isHonor: Boolean get() = suit.isHonor

    // =========================================================================
    // Numbered Tiles (1-9 of Dots, Bamboo, Characters)
    // =========================================================================

    /**
     * A numbered tile (1-9) in one of the three numbered suits.
     */
    data class NumberedTile(
        override val suit: Suit,
        val number: Int
    ) : Tile() {
        init {
            require(suit.isNumbered) { "Suit must be DOTS, BAMBOO, or CHARACTERS" }
            require(number in 1..9) { "Number must be between 1 and 9" }
        }

        override val id: String = "${suit.name}_$number"
        override val name: String = "$number ${suit.english}"
        override val chineseName: String = "${numberToChinese(number)}${suit.chinese}"
        override val isTerminal: Boolean = number == 1 || number == 9

        /** Check if this is a simple tile (2-8) */
        val isSimple: Boolean = number in 2..8
    }

    // =========================================================================
    // Wind Tiles
    // =========================================================================

    /**
     * The four wind directions.
     */
    enum class Wind(val chinese: String, val english: String) {
        EAST("东", "East"),
        SOUTH("南", "South"),
        WEST("西", "West"),
        NORTH("北", "North")
    }

    /**
     * A wind tile (East, South, West, or North).
     */
    data class WindTile(val wind: Wind) : Tile() {
        override val suit: Suit = Suit.WIND
        override val id: String = "WIND_${wind.name}"
        override val name: String = "${wind.english} Wind"
        override val chineseName: String = wind.chinese
    }

    // =========================================================================
    // Dragon Tiles
    // =========================================================================

    /**
     * The three dragon types.
     */
    enum class Dragon(val chinese: String, val english: String) {
        RED("中", "Red"),      // 红中
        GREEN("发", "Green"),  // 发财
        WHITE("白", "White")   // 白板
    }

    /**
     * A dragon tile (Red, Green, or White).
     */
    data class DragonTile(val dragon: Dragon) : Tile() {
        override val suit: Suit = Suit.DRAGON
        override val id: String = "DRAGON_${dragon.name}"
        override val name: String = "${dragon.english} Dragon"
        override val chineseName: String = dragon.chinese
    }

    // =========================================================================
    // Companion Object - All Tiles
    // =========================================================================

    companion object {
        /**
         * All 34 unique tile types in Beijing Mahjong (no flowers).
         */
        val ALL_TILES: List<Tile> = buildList {
            // Numbered tiles: 9 x 3 suits = 27 tiles
            for (suit in listOf(Suit.DOTS, Suit.BAMBOO, Suit.CHARACTERS)) {
                for (n in 1..9) {
                    add(NumberedTile(suit, n))
                }
            }
            // Wind tiles: 4 tiles
            Wind.entries.forEach { add(WindTile(it)) }
            // Dragon tiles: 3 tiles
            Dragon.entries.forEach { add(DragonTile(it)) }
        }

        /** Get all tiles of a specific suit */
        fun tilesOfSuit(suit: Suit): List<Tile> = ALL_TILES.filter { it.suit == suit }

        /** Get all numbered tiles */
        val NUMBERED_TILES: List<NumberedTile> = ALL_TILES.filterIsInstance<NumberedTile>()

        /** Get all terminal tiles (1s and 9s) */
        val TERMINAL_TILES: List<NumberedTile> = NUMBERED_TILES.filter { it.isTerminal }

        /** Get all honor tiles (winds and dragons) */
        val HONOR_TILES: List<Tile> = ALL_TILES.filter { it.isHonor }

        /** Get all wind tiles */
        val WIND_TILES: List<WindTile> = ALL_TILES.filterIsInstance<WindTile>()

        /** Get all dragon tiles */
        val DRAGON_TILES: List<DragonTile> = ALL_TILES.filterIsInstance<DragonTile>()

        /** Find a tile by its ID */
        fun fromId(id: String): Tile? = ALL_TILES.find { it.id == id }
    }
}

// =========================================================================
// Helper Functions
// =========================================================================

/**
 * Convert a number (1-9) to its Chinese character.
 */
private fun numberToChinese(number: Int): String = when (number) {
    1 -> "一"
    2 -> "二"
    3 -> "三"
    4 -> "四"
    5 -> "五"
    6 -> "六"
    7 -> "七"
    8 -> "八"
    9 -> "九"
    else -> throw IllegalArgumentException("Number must be 1-9")
}
