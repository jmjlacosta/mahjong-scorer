package com.jmjlacosta.mahjongscorer.scoring

import com.jmjlacosta.mahjongscorer.model.Hand
import com.jmjlacosta.mahjongscorer.model.HandParser
import com.jmjlacosta.mahjongscorer.model.Meld
import com.jmjlacosta.mahjongscorer.model.Suit
import com.jmjlacosta.mahjongscorer.model.Tile

/**
 * Detects scoring patterns in mahjong hands.
 */
object PatternDetector {

    /**
     * Detect all applicable patterns for a hand.
     * Applies exclusion rules (e.g., Pure Hand supersedes Half Flush).
     */
    fun detectPatterns(hand: Hand, context: WinContext): List<Pattern> {
        val patterns = mutableListOf<Pattern>()

        // Always include basic win
        patterns.add(Pattern.BASIC_WIN)

        // Context-based patterns
        if (context.isSelfDraw) {
            patterns.add(Pattern.SELF_DRAW)
        }
        if (context.isConcealed) {
            patterns.add(Pattern.CONCEALED_HAND)
        }

        // Special hands (check first as they have different rules)
        if (HandParser.isThirteenOrphans(hand.tiles)) {
            patterns.add(Pattern.THIRTEEN_ORPHANS)
            return applyExclusions(patterns)
        }

        if (HandParser.isSevenPairs(hand.tiles)) {
            patterns.add(Pattern.SEVEN_PAIRS)
            // Seven pairs can still have other patterns like Pure Hand
        }

        // Meld-based patterns
        hand.melds?.let { melds ->
            if (isAllPongs(melds)) {
                patterns.add(Pattern.ALL_PONGS)
            }
        }

        // Suit-based patterns (check on all tiles)
        when {
            isAllHonors(hand.tiles) -> patterns.add(Pattern.ALL_HONORS)
            isAllTerminals(hand.tiles) -> patterns.add(Pattern.ALL_TERMINALS)
            isPureHand(hand.tiles) -> patterns.add(Pattern.PURE_HAND)
            isHalfFlush(hand.tiles) -> patterns.add(Pattern.HALF_FLUSH)
        }

        return applyExclusions(patterns)
    }

    /**
     * Apply exclusion rules to remove superseded patterns.
     */
    private fun applyExclusions(patterns: MutableList<Pattern>): List<Pattern> {
        val toRemove = mutableSetOf<Pattern>()
        for (pattern in patterns) {
            Pattern.EXCLUSIONS[pattern]?.let { excluded ->
                toRemove.addAll(excluded)
            }
        }
        return patterns.filter { it !in toRemove }
    }

    // =========================================================================
    // Meld-based pattern detection
    // =========================================================================

    /**
     * Check if all melds are Pongs or Kongs (no Chows).
     * The pair doesn't count as a Chow.
     */
    fun isAllPongs(melds: List<Meld>): Boolean {
        val sets = melds.filter { it !is Meld.Pair }
        return sets.all { it is Meld.Pong || it is Meld.Kong }
    }

    // =========================================================================
    // Suit-based pattern detection
    // =========================================================================

    /**
     * Check if all tiles are from one numbered suit (no honors).
     */
    fun isPureHand(tiles: List<Tile>): Boolean {
        if (tiles.isEmpty()) return false

        // All tiles must be numbered
        if (tiles.any { it !is Tile.NumberedTile }) return false

        // All from the same suit
        val suits = tiles.map { it.suit }.distinct()
        return suits.size == 1
    }

    /**
     * Check if all tiles are from one numbered suit plus honors.
     */
    fun isHalfFlush(tiles: List<Tile>): Boolean {
        if (tiles.isEmpty()) return false

        val numberedTiles = tiles.filterIsInstance<Tile.NumberedTile>()
        val honorTiles = tiles.filter { it.isHonor }

        // Must have both numbered and honor tiles
        if (numberedTiles.isEmpty() || honorTiles.isEmpty()) return false

        // All numbered tiles must be the same suit
        val suits = numberedTiles.map { it.suit }.distinct()
        return suits.size == 1
    }

    /**
     * Check if all tiles are honor tiles (winds and dragons only).
     */
    fun isAllHonors(tiles: List<Tile>): Boolean {
        return tiles.isNotEmpty() && tiles.all { it.isHonor }
    }

    /**
     * Check if all tiles are terminals (1s and 9s only).
     */
    fun isAllTerminals(tiles: List<Tile>): Boolean {
        return tiles.isNotEmpty() && tiles.all { it.isTerminal }
    }

    // =========================================================================
    // Special hand detection (delegated to HandParser)
    // =========================================================================

    /**
     * Check if tiles form Seven Pairs.
     */
    fun isSevenPairs(tiles: List<Tile>): Boolean = HandParser.isSevenPairs(tiles)

    /**
     * Check if tiles form Thirteen Orphans.
     */
    fun isThirteenOrphans(tiles: List<Tile>): Boolean = HandParser.isThirteenOrphans(tiles)
}
