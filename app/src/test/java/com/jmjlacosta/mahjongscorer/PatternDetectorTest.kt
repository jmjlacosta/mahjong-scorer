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

    // =========================================================================
    // All Chows Tests
    // =========================================================================

    @Test
    fun `all chows detected correctly`() {
        val melds = listOf(
            Meld.Chow(dots(1)),
            Meld.Chow(dots(4)),
            Meld.Chow(bamboo(2)),
            Meld.Chow(chars(7)),
            Meld.Pair(wind(Tile.Wind.EAST))
        )
        assertTrue(PatternDetector.isAllChows(melds))
    }

    @Test
    fun `not all chows when pong present`() {
        val melds = listOf(
            Meld.Chow(dots(1)),
            Meld.Pong(dots(5)),
            Meld.Chow(bamboo(2)),
            Meld.Chow(chars(7)),
            Meld.Pair(wind(Tile.Wind.EAST))
        )
        assertFalse(PatternDetector.isAllChows(melds))
    }

    // =========================================================================
    // Dragon Pung Tests
    // =========================================================================

    @Test
    fun `count dragon pungs correctly`() {
        val melds = listOf(
            Meld.Pong(dragon(Tile.Dragon.RED)),
            Meld.Pong(dragon(Tile.Dragon.GREEN)),
            Meld.Pong(dots(1)),
            Meld.Chow(bamboo(2)),
            Meld.Pair(chars(5))
        )
        assertEquals(2, PatternDetector.countDragonPungs(melds))
    }

    @Test
    fun `kong counts as dragon pung`() {
        val melds = listOf(
            Meld.Kong(dragon(Tile.Dragon.WHITE)),
            Meld.Pong(dots(1)),
            Meld.Chow(bamboo(2)),
            Meld.Chow(chars(5)),
            Meld.Pair(wind(Tile.Wind.EAST))
        )
        assertEquals(1, PatternDetector.countDragonPungs(melds))
    }

    // =========================================================================
    // Seat Wind Pung Tests
    // =========================================================================

    @Test
    fun `seat wind pung detected`() {
        val melds = listOf(
            Meld.Pong(wind(Tile.Wind.EAST)),
            Meld.Pong(dots(1)),
            Meld.Chow(bamboo(2)),
            Meld.Chow(chars(5)),
            Meld.Pair(dots(9))
        )
        assertTrue(PatternDetector.hasSeatWindPung(melds, Tile.Wind.EAST))
        assertFalse(PatternDetector.hasSeatWindPung(melds, Tile.Wind.SOUTH))
    }

    @Test
    fun `seat wind kong counts`() {
        val melds = listOf(
            Meld.Kong(wind(Tile.Wind.SOUTH)),
            Meld.Pong(dots(1)),
            Meld.Chow(bamboo(2)),
            Meld.Chow(chars(5)),
            Meld.Pair(dots(9))
        )
        assertTrue(PatternDetector.hasSeatWindPung(melds, Tile.Wind.SOUTH))
    }

    // =========================================================================
    // Round Wind Pung Tests
    // =========================================================================

    @Test
    fun `round wind pung detected`() {
        val melds = listOf(
            Meld.Pong(wind(Tile.Wind.WEST)),
            Meld.Pong(dots(1)),
            Meld.Chow(bamboo(2)),
            Meld.Chow(chars(5)),
            Meld.Pair(dots(9))
        )
        assertTrue(PatternDetector.hasRoundWindPung(melds, Tile.Wind.WEST))
        assertFalse(PatternDetector.hasRoundWindPung(melds, Tile.Wind.NORTH))
    }

    // =========================================================================
    // Mixed Terminals Tests
    // =========================================================================

    @Test
    fun `mixed terminals with terminals and honors`() {
        val tiles = listOf(
            dots(1), dots(1), dots(1),
            dots(9), dots(9), dots(9),
            bamboo(1), bamboo(1), bamboo(1),
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST), wind(Tile.Wind.EAST),
            dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED)
        )
        assertTrue(PatternDetector.isMixedTerminals(tiles))
    }

    @Test
    fun `not mixed terminals all terminals`() {
        val tiles = listOf(
            dots(1), dots(1), dots(1),
            dots(9), dots(9), dots(9),
            bamboo(1), bamboo(1), bamboo(1),
            bamboo(9), bamboo(9), bamboo(9),
            chars(1), chars(1)
        )
        assertFalse(PatternDetector.isMixedTerminals(tiles)) // This is All Terminals
    }

    @Test
    fun `not mixed terminals all honors`() {
        val tiles = listOf(
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST), wind(Tile.Wind.EAST),
            wind(Tile.Wind.SOUTH), wind(Tile.Wind.SOUTH), wind(Tile.Wind.SOUTH),
            wind(Tile.Wind.WEST), wind(Tile.Wind.WEST), wind(Tile.Wind.WEST),
            dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED),
            dragon(Tile.Dragon.GREEN), dragon(Tile.Dragon.GREEN)
        )
        assertFalse(PatternDetector.isMixedTerminals(tiles)) // This is All Honors
    }

    @Test
    fun `not mixed terminals with simple tiles`() {
        val tiles = listOf(
            dots(1), dots(1), dots(1),
            dots(5), dots(5), dots(5), // Simple tile
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST), wind(Tile.Wind.EAST),
            dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED),
            bamboo(9), bamboo(9)
        )
        assertFalse(PatternDetector.isMixedTerminals(tiles))
    }

    // =========================================================================
    // Little Three Dragons Tests
    // =========================================================================

    @Test
    fun `little three dragons detected`() {
        val melds = listOf(
            Meld.Pong(dragon(Tile.Dragon.RED)),
            Meld.Pong(dragon(Tile.Dragon.GREEN)),
            Meld.Chow(dots(1)),
            Meld.Chow(bamboo(4)),
            Meld.Pair(dragon(Tile.Dragon.WHITE))
        )
        assertTrue(PatternDetector.isLittleThreeDragons(melds))
    }

    @Test
    fun `not little three dragons without dragon pair`() {
        val melds = listOf(
            Meld.Pong(dragon(Tile.Dragon.RED)),
            Meld.Pong(dragon(Tile.Dragon.GREEN)),
            Meld.Chow(dots(1)),
            Meld.Chow(bamboo(4)),
            Meld.Pair(wind(Tile.Wind.EAST)) // Not a dragon pair
        )
        assertFalse(PatternDetector.isLittleThreeDragons(melds))
    }

    @Test
    fun `not little three dragons with only one dragon pung`() {
        val melds = listOf(
            Meld.Pong(dragon(Tile.Dragon.RED)),
            Meld.Pong(dots(5)),
            Meld.Chow(dots(1)),
            Meld.Chow(bamboo(4)),
            Meld.Pair(dragon(Tile.Dragon.WHITE))
        )
        assertFalse(PatternDetector.isLittleThreeDragons(melds))
    }

    // =========================================================================
    // Big Three Dragons Tests
    // =========================================================================

    @Test
    fun `big three dragons detected`() {
        val melds = listOf(
            Meld.Pong(dragon(Tile.Dragon.RED)),
            Meld.Pong(dragon(Tile.Dragon.GREEN)),
            Meld.Pong(dragon(Tile.Dragon.WHITE)),
            Meld.Chow(dots(1)),
            Meld.Pair(bamboo(5))
        )
        assertTrue(PatternDetector.isBigThreeDragons(melds))
    }

    @Test
    fun `big three dragons with kong`() {
        val melds = listOf(
            Meld.Kong(dragon(Tile.Dragon.RED)),
            Meld.Pong(dragon(Tile.Dragon.GREEN)),
            Meld.Pong(dragon(Tile.Dragon.WHITE)),
            Meld.Chow(dots(1)),
            Meld.Pair(bamboo(5))
        )
        assertTrue(PatternDetector.isBigThreeDragons(melds))
    }

    @Test
    fun `not big three dragons with only two dragon pungs`() {
        val melds = listOf(
            Meld.Pong(dragon(Tile.Dragon.RED)),
            Meld.Pong(dragon(Tile.Dragon.GREEN)),
            Meld.Pong(dots(1)),
            Meld.Chow(bamboo(4)),
            Meld.Pair(dragon(Tile.Dragon.WHITE))
        )
        assertFalse(PatternDetector.isBigThreeDragons(melds))
    }

    // =========================================================================
    // Pattern Detection Integration Tests
    // =========================================================================

    @Test
    fun `big three dragons supersedes little three dragons`() {
        val melds = listOf(
            Meld.Pong(dragon(Tile.Dragon.RED)),
            Meld.Pong(dragon(Tile.Dragon.GREEN)),
            Meld.Pong(dragon(Tile.Dragon.WHITE)),
            Meld.Chow(dots(1)),
            Meld.Pair(bamboo(5))
        )
        val hand = Hand(
            tiles = repeat(dragon(Tile.Dragon.RED), 3) + repeat(dragon(Tile.Dragon.GREEN), 3) +
                    repeat(dragon(Tile.Dragon.WHITE), 3) + listOf(dots(1), dots(2), dots(3)) +
                    repeat(bamboo(5), 2),
            melds = melds
        )
        val patterns = PatternDetector.detectPatterns(hand, WinContext.DEFAULT)

        assertTrue(patterns.contains(Pattern.BIG_THREE_DRAGONS))
        assertFalse(patterns.contains(Pattern.LITTLE_THREE_DRAGONS))
    }

    @Test
    fun `seat and round wind stack when same`() {
        val melds = listOf(
            Meld.Pong(wind(Tile.Wind.EAST)),
            Meld.Chow(dots(1)),
            Meld.Chow(dots(4)),
            Meld.Chow(bamboo(2)),
            Meld.Pair(chars(5))
        )
        val hand = Hand(
            tiles = repeat(wind(Tile.Wind.EAST), 3) +
                    listOf(dots(1), dots(2), dots(3), dots(4), dots(5), dots(6)) +
                    listOf(bamboo(2), bamboo(3), bamboo(4)) + repeat(chars(5), 2),
            melds = melds
        )
        val context = WinContext(
            seatWind = Tile.Wind.EAST,
            roundWind = Tile.Wind.EAST
        )
        val patterns = PatternDetector.detectPatterns(hand, context)

        assertTrue(patterns.contains(Pattern.SEAT_WIND_PUNG))
        assertTrue(patterns.contains(Pattern.ROUND_WIND_PUNG))
        // Should have both for +2 total
        assertEquals(2, patterns.count { it == Pattern.SEAT_WIND_PUNG || it == Pattern.ROUND_WIND_PUNG })
    }

    @Test
    fun `dragon pungs stack with big three dragons`() {
        val melds = listOf(
            Meld.Pong(dragon(Tile.Dragon.RED)),
            Meld.Pong(dragon(Tile.Dragon.GREEN)),
            Meld.Pong(dragon(Tile.Dragon.WHITE)),
            Meld.Chow(dots(1)),
            Meld.Pair(bamboo(5))
        )
        val hand = Hand(
            tiles = repeat(dragon(Tile.Dragon.RED), 3) + repeat(dragon(Tile.Dragon.GREEN), 3) +
                    repeat(dragon(Tile.Dragon.WHITE), 3) + listOf(dots(1), dots(2), dots(3)) +
                    repeat(bamboo(5), 2),
            melds = melds
        )
        val patterns = PatternDetector.detectPatterns(hand, WinContext.DEFAULT)

        assertTrue(patterns.contains(Pattern.BIG_THREE_DRAGONS))
        // Should have 3 dragon pungs
        assertEquals(3, patterns.count { it == Pattern.DRAGON_PUNG })
    }
}
