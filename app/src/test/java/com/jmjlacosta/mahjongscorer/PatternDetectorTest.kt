package com.jmjlacosta.mahjongscorer

import com.jmjlacosta.mahjongscorer.model.Hand
import com.jmjlacosta.mahjongscorer.model.HandParser
import com.jmjlacosta.mahjongscorer.model.Meld
import com.jmjlacosta.mahjongscorer.model.Suit
import com.jmjlacosta.mahjongscorer.model.Tile
import com.jmjlacosta.mahjongscorer.scoring.Pattern
import com.jmjlacosta.mahjongscorer.scoring.PatternDetector
import com.jmjlacosta.mahjongscorer.scoring.WinContext
import org.junit.Assert.*
import org.junit.Test

class PatternDetectorTest {

    // =========================================================================
    // Helper functions
    // =========================================================================

    private fun dots(n: Int) = Tile.NumberedTile(Suit.DOTS, n)
    private fun bamboo(n: Int) = Tile.NumberedTile(Suit.BAMBOO, n)
    private fun chars(n: Int) = Tile.NumberedTile(Suit.CHARACTERS, n)
    private fun wind(w: Tile.Wind) = Tile.WindTile(w)
    private fun dragon(d: Tile.Dragon) = Tile.DragonTile(d)
    private fun repeat(tile: Tile, n: Int): List<Tile> = List(n) { tile }

    private fun handWithMelds(tiles: List<Tile>): Hand {
        val melds = HandParser.parseHand(tiles).firstOrNull()
        return Hand(tiles = tiles, melds = melds)
    }

    // =========================================================================
    // All Pongs Tests
    // =========================================================================

    @Test
    fun `all pongs detected correctly`() {
        val melds = listOf(
            Meld.Pong(dots(1)),
            Meld.Pong(dots(2)),
            Meld.Pong(dots(3)),
            Meld.Pong(dots(4)),
            Meld.Pair(dots(5))
        )
        assertTrue(PatternDetector.isAllPongs(melds))
    }

    @Test
    fun `all pongs with kong`() {
        val melds = listOf(
            Meld.Pong(dots(1)),
            Meld.Pong(dots(2)),
            Meld.Kong(dots(3)),
            Meld.Pong(dots(4)),
            Meld.Pair(dots(5))
        )
        assertTrue(PatternDetector.isAllPongs(melds))
    }

    @Test
    fun `not all pongs when chow present`() {
        val melds = listOf(
            Meld.Pong(dots(1)),
            Meld.Pong(dots(5)),
            Meld.Chow(dots(2)),
            Meld.Pong(dots(8)),
            Meld.Pair(dots(9))
        )
        assertFalse(PatternDetector.isAllPongs(melds))
    }

    // =========================================================================
    // Pure Hand Tests
    // =========================================================================

    @Test
    fun `pure hand all dots`() {
        val tiles = listOf(
            dots(1), dots(1), dots(1),
            dots(2), dots(3), dots(4),
            dots(5), dots(5), dots(5),
            dots(7), dots(8), dots(9),
            dots(9), dots(9)
        )
        assertTrue(PatternDetector.isPureHand(tiles))
    }

    @Test
    fun `pure hand all bamboo`() {
        val tiles = listOf(
            bamboo(1), bamboo(1), bamboo(1),
            bamboo(2), bamboo(3), bamboo(4),
            bamboo(5), bamboo(5), bamboo(5),
            bamboo(7), bamboo(8), bamboo(9),
            bamboo(9), bamboo(9)
        )
        assertTrue(PatternDetector.isPureHand(tiles))
    }

    @Test
    fun `not pure hand with mixed suits`() {
        val tiles = listOf(
            dots(1), dots(1), dots(1),
            bamboo(2), bamboo(3), bamboo(4),
            dots(5), dots(5), dots(5),
            dots(7), dots(8), dots(9),
            dots(9), dots(9)
        )
        assertFalse(PatternDetector.isPureHand(tiles))
    }

    @Test
    fun `not pure hand with honors`() {
        val tiles = listOf(
            dots(1), dots(1), dots(1),
            dots(2), dots(3), dots(4),
            dots(5), dots(5), dots(5),
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST), wind(Tile.Wind.EAST),
            dots(9), dots(9)
        )
        assertFalse(PatternDetector.isPureHand(tiles))
    }

    // =========================================================================
    // Half Flush Tests
    // =========================================================================

    @Test
    fun `half flush dots with honors`() {
        val tiles = listOf(
            dots(1), dots(1), dots(1),
            dots(2), dots(3), dots(4),
            dots(5), dots(5), dots(5),
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST), wind(Tile.Wind.EAST),
            dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED)
        )
        assertTrue(PatternDetector.isHalfFlush(tiles))
    }

    @Test
    fun `not half flush pure numbered`() {
        val tiles = listOf(
            dots(1), dots(1), dots(1),
            dots(2), dots(3), dots(4),
            dots(5), dots(5), dots(5),
            dots(7), dots(8), dots(9),
            dots(9), dots(9)
        )
        assertFalse(PatternDetector.isHalfFlush(tiles)) // This is Pure Hand, not Half Flush
    }

    @Test
    fun `not half flush mixed numbered suits`() {
        val tiles = listOf(
            dots(1), dots(1), dots(1),
            bamboo(2), bamboo(3), bamboo(4),
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST), wind(Tile.Wind.EAST),
            dots(7), dots(8), dots(9),
            dots(9), dots(9)
        )
        assertFalse(PatternDetector.isHalfFlush(tiles))
    }

    // =========================================================================
    // All Honors Tests
    // =========================================================================

    @Test
    fun `all honors only winds and dragons`() {
        val tiles = listOf(
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST), wind(Tile.Wind.EAST),
            wind(Tile.Wind.SOUTH), wind(Tile.Wind.SOUTH), wind(Tile.Wind.SOUTH),
            wind(Tile.Wind.WEST), wind(Tile.Wind.WEST), wind(Tile.Wind.WEST),
            dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED),
            dragon(Tile.Dragon.GREEN), dragon(Tile.Dragon.GREEN)
        )
        assertTrue(PatternDetector.isAllHonors(tiles))
    }

    @Test
    fun `not all honors with numbered tile`() {
        val tiles = listOf(
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST), wind(Tile.Wind.EAST),
            wind(Tile.Wind.SOUTH), wind(Tile.Wind.SOUTH), wind(Tile.Wind.SOUTH),
            wind(Tile.Wind.WEST), wind(Tile.Wind.WEST), wind(Tile.Wind.WEST),
            dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED),
            dots(1), dots(1) // Numbered tiles
        )
        assertFalse(PatternDetector.isAllHonors(tiles))
    }

    // =========================================================================
    // All Terminals Tests
    // =========================================================================

    @Test
    fun `all terminals only ones and nines`() {
        val tiles = listOf(
            dots(1), dots(1), dots(1),
            dots(9), dots(9), dots(9),
            bamboo(1), bamboo(1), bamboo(1),
            bamboo(9), bamboo(9), bamboo(9),
            chars(1), chars(1)
        )
        assertTrue(PatternDetector.isAllTerminals(tiles))
    }

    @Test
    fun `not all terminals with middle tile`() {
        val tiles = listOf(
            dots(1), dots(1), dots(1),
            dots(9), dots(9), dots(9),
            bamboo(1), bamboo(1), bamboo(1),
            bamboo(9), bamboo(9), bamboo(9),
            chars(5), chars(5) // Middle tile
        )
        assertFalse(PatternDetector.isAllTerminals(tiles))
    }

    @Test
    fun `not all terminals with honor`() {
        val tiles = listOf(
            dots(1), dots(1), dots(1),
            dots(9), dots(9), dots(9),
            bamboo(1), bamboo(1), bamboo(1),
            bamboo(9), bamboo(9), bamboo(9),
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST) // Honor tiles
        )
        assertFalse(PatternDetector.isAllTerminals(tiles))
    }

    // =========================================================================
    // Seven Pairs Tests
    // =========================================================================

    @Test
    fun `seven pairs detected`() {
        val tiles = listOf(
            dots(1), dots(1),
            dots(2), dots(2),
            dots(3), dots(3),
            dots(4), dots(4),
            dots(5), dots(5),
            dots(6), dots(6),
            dots(7), dots(7)
        )
        assertTrue(PatternDetector.isSevenPairs(tiles))
    }

    // =========================================================================
    // Thirteen Orphans Tests
    // =========================================================================

    @Test
    fun `thirteen orphans detected`() {
        val tiles = listOf(
            dots(1), dots(9),
            bamboo(1), bamboo(9),
            chars(1), chars(9),
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST),
            wind(Tile.Wind.SOUTH),
            wind(Tile.Wind.WEST),
            wind(Tile.Wind.NORTH),
            dragon(Tile.Dragon.RED),
            dragon(Tile.Dragon.GREEN),
            dragon(Tile.Dragon.WHITE)
        )
        assertTrue(PatternDetector.isThirteenOrphans(tiles))
    }

    // =========================================================================
    // Pattern Detection with Context
    // =========================================================================

    @Test
    fun `detect patterns includes basic win`() {
        val tiles = repeat(dots(1), 3) + repeat(dots(2), 3) +
                repeat(dots(3), 3) + repeat(dots(4), 3) + repeat(dots(5), 2)
        val hand = handWithMelds(tiles)
        val patterns = PatternDetector.detectPatterns(hand, WinContext.DEFAULT)

        assertTrue(patterns.contains(Pattern.BASIC_WIN))
    }

    @Test
    fun `detect patterns with self draw`() {
        val tiles = repeat(dots(1), 3) + repeat(dots(2), 3) +
                repeat(dots(3), 3) + repeat(dots(4), 3) + repeat(dots(5), 2)
        val hand = handWithMelds(tiles)
        val patterns = PatternDetector.detectPatterns(hand, WinContext.SELF_DRAW)

        assertTrue(patterns.contains(Pattern.SELF_DRAW))
        assertTrue(patterns.contains(Pattern.CONCEALED_HAND))
    }

    @Test
    fun `pure hand supersedes half flush`() {
        val tiles = listOf(
            dots(1), dots(1), dots(1),
            dots(2), dots(3), dots(4),
            dots(5), dots(5), dots(5),
            dots(7), dots(8), dots(9),
            dots(9), dots(9)
        )
        val hand = handWithMelds(tiles)
        val patterns = PatternDetector.detectPatterns(hand, WinContext.DEFAULT)

        assertTrue(patterns.contains(Pattern.PURE_HAND))
        assertFalse(patterns.contains(Pattern.HALF_FLUSH))
    }

    @Test
    fun `all pongs pure hand combination`() {
        val tiles = repeat(dots(1), 3) + repeat(dots(2), 3) +
                repeat(dots(3), 3) + repeat(dots(4), 3) + repeat(dots(5), 2)
        val hand = handWithMelds(tiles)
        val patterns = PatternDetector.detectPatterns(hand, WinContext.DEFAULT)

        assertTrue(patterns.contains(Pattern.ALL_PONGS))
        assertTrue(patterns.contains(Pattern.PURE_HAND))
    }
}
