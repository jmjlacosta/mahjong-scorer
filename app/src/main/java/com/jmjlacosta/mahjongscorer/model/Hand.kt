package com.jmjlacosta.mahjongscorer.model

/**
 * Represents a mahjong hand (14 tiles for a winning hand).
 */
data class Hand(
    val tiles: List<Tile>,
    val melds: List<Meld>? = null,
    val winningTile: Tile? = null,
    val isConcealed: Boolean = true
) {
    /** Total tile count */
    val tileCount: Int get() = tiles.size

    /** Check if this is a complete 14-tile hand */
    val isComplete: Boolean get() = tiles.size == 14

    /** Get all tiles grouped by their ID (for counting duplicates) */
    val tileGroups: Map<String, List<Tile>> by lazy {
        tiles.groupBy { it.id }
    }

    /** Count of each unique tile */
    val tileCounts: Map<String, Int> by lazy {
        tileGroups.mapValues { it.value.size }
    }
}

/**
 * Parser for converting a list of tiles into valid meld combinations.
 * A standard winning hand is 4 sets + 1 pair.
 * Special hands: Seven Pairs, Thirteen Orphans.
 */
object HandParser {

    /**
     * Parse a 14-tile hand into all valid meld arrangements.
     * Returns empty list if no valid arrangement exists.
     */
    fun parseHand(tiles: List<Tile>): List<List<Meld>> {
        if (tiles.size != 14) return emptyList()

        val results = mutableListOf<List<Meld>>()

        // Check special hands first
        parseSevenPairs(tiles)?.let { results.add(it) }
        parseThirteenOrphans(tiles)?.let { results.add(it) }

        // Standard 4 sets + 1 pair
        results.addAll(parseStandardHand(tiles))

        return results
    }

    /**
     * Check if a list of tiles forms a valid winning hand.
     */
    fun isValidWinningHand(tiles: List<Tile>): Boolean {
        return parseHand(tiles).isNotEmpty()
    }

    // =========================================================================
    // Standard Hand Parsing (4 sets + 1 pair)
    // =========================================================================

    private fun parseStandardHand(tiles: List<Tile>): List<List<Meld>> {
        val results = mutableListOf<List<Meld>>()
        val tileCounts = tiles.groupingBy { it.id }.eachCount().toMutableMap()
        val uniqueTiles = tiles.distinctBy { it.id }

        // Try each unique tile as the pair
        for (pairTile in uniqueTiles) {
            val count = tileCounts[pairTile.id] ?: 0
            if (count < 2) continue

            // Remove pair from counts
            val remainingCounts = tileCounts.toMutableMap()
            remainingCounts[pairTile.id] = count - 2

            // Try to form 4 sets with remaining 12 tiles
            val sets = mutableListOf<Meld>()
            if (tryFormSets(remainingCounts, tiles.distinctBy { it.id }, sets, 4)) {
                val melds = sets + Meld.Pair(pairTile)
                results.add(melds)
            }
        }

        return results
    }

    /**
     * Recursively try to form the required number of sets from remaining tiles.
     */
    private fun tryFormSets(
        tileCounts: MutableMap<String, Int>,
        uniqueTiles: List<Tile>,
        sets: MutableList<Meld>,
        setsNeeded: Int
    ): Boolean {
        if (setsNeeded == 0) {
            // Check all tiles are used
            return tileCounts.values.all { it == 0 }
        }

        // Find first tile with remaining count
        val firstTile = uniqueTiles.firstOrNull { (tileCounts[it.id] ?: 0) > 0 } ?: return false
        val count = tileCounts[firstTile.id] ?: 0

        // Try Pong (3 identical)
        if (count >= 3) {
            tileCounts[firstTile.id] = count - 3
            sets.add(Meld.Pong(firstTile))
            if (tryFormSets(tileCounts, uniqueTiles, sets, setsNeeded - 1)) {
                return true
            }
            sets.removeAt(sets.lastIndex)
            tileCounts[firstTile.id] = count
        }

        // Try Chow (3 consecutive) - only for numbered tiles
        if (firstTile is Tile.NumberedTile && firstTile.number <= 7) {
            val tile2Id = "${firstTile.suit.name}_${firstTile.number + 1}"
            val tile3Id = "${firstTile.suit.name}_${firstTile.number + 2}"
            val count2 = tileCounts[tile2Id] ?: 0
            val count3 = tileCounts[tile3Id] ?: 0

            if (count >= 1 && count2 >= 1 && count3 >= 1) {
                tileCounts[firstTile.id] = count - 1
                tileCounts[tile2Id] = count2 - 1
                tileCounts[tile3Id] = count3 - 1
                sets.add(Meld.Chow(firstTile))
                if (tryFormSets(tileCounts, uniqueTiles, sets, setsNeeded - 1)) {
                    return true
                }
                sets.removeAt(sets.lastIndex)
                tileCounts[firstTile.id] = count
                tileCounts[tile2Id] = count2
                tileCounts[tile3Id] = count3
            }
        }

        return false
    }

    // =========================================================================
    // Seven Pairs (七对子)
    // =========================================================================

    /**
     * Check if tiles form Seven Pairs (7 pairs of identical tiles).
     */
    private fun parseSevenPairs(tiles: List<Tile>): List<Meld>? {
        val counts = tiles.groupingBy { it.id }.eachCount()

        // Must have exactly 7 unique tiles, each appearing exactly twice
        if (counts.size != 7) return null
        if (!counts.values.all { it == 2 }) return null

        return tiles.distinctBy { it.id }.map { Meld.Pair(it) }
    }

    /**
     * Check if a hand is Seven Pairs.
     */
    fun isSevenPairs(tiles: List<Tile>): Boolean {
        return parseSevenPairs(tiles) != null
    }

    // =========================================================================
    // Thirteen Orphans (十三幺)
    // =========================================================================

    /**
     * All 13 terminal and honor tiles required for Thirteen Orphans.
     */
    private val THIRTEEN_ORPHANS_TILES: Set<String> by lazy {
        val terminals = Tile.TERMINAL_TILES.map { it.id }
        val honors = Tile.HONOR_TILES.map { it.id }
        (terminals + honors).toSet()
    }

    /**
     * Check if tiles form Thirteen Orphans.
     * Requires one of each terminal/honor tile (13 unique) plus one duplicate.
     */
    private fun parseThirteenOrphans(tiles: List<Tile>): List<Meld>? {
        val tileIds = tiles.map { it.id }
        val uniqueIds = tileIds.toSet()

        // Must have all 13 required tiles
        if (!uniqueIds.containsAll(THIRTEEN_ORPHANS_TILES)) return null

        // Must have exactly 14 tiles with one duplicate from the 13
        if (tileIds.size != 14) return null

        val counts = tiles.groupingBy { it.id }.eachCount()
        val duplicateTile = tiles.find { counts[it.id] == 2 } ?: return null

        // Return as a special representation (13 singles + 1 pair)
        // For scoring purposes, we represent as 13 "singles" + 1 pair
        return listOf(Meld.Pair(duplicateTile))
    }

    /**
     * Check if a hand is Thirteen Orphans.
     */
    fun isThirteenOrphans(tiles: List<Tile>): Boolean {
        return parseThirteenOrphans(tiles) != null
    }
}
