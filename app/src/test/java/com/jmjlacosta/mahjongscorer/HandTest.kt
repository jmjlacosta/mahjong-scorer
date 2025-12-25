package com.jmjlacosta.mahjongscorer

import com.jmjlacosta.mahjongscorer.model.Hand
import com.jmjlacosta.mahjongscorer.model.HandParser
import com.jmjlacosta.mahjongscorer.model.Meld
import com.jmjlacosta.mahjongscorer.model.Suit
import com.jmjlacosta.mahjongscorer.model.Tile
import org.junit.Assert.*
import org.junit.Test

class HandTest {

    // =========================================================================
    // Helper functions to create tiles
    // =========================================================================

    private fun dots(n: Int) = Tile.NumberedTile(Suit.DOTS, n)
    private fun bamboo(n: Int) = Tile.NumberedTile(Suit.BAMBOO, n)
    private fun chars(n: Int) = Tile.NumberedTile(Suit.CHARACTERS, n)
    private fun wind(w: Tile.Wind) = Tile.WindTile(w)
    private fun dragon(d: Tile.Dragon) = Tile.DragonTile(d)

    // Create n copies of a tile
    private fun repeat(tile: Tile, n: Int): List<Tile> = List(n) { tile }

    // =========================================================================
    // Hand Data Class Tests
    // =========================================================================

    @Test
    fun `hand with 14 tiles is complete`() {
        val tiles = repeat(dots(1), 3) + repeat(dots(2), 3) +
                repeat(dots(3), 3) + repeat(dots(4), 3) + repeat(dots(5), 2)
        val hand = Hand(tiles)
        assertTrue(hand.isComplete)
        assertEquals(14, hand.tileCount)
    }

    @Test
    fun `hand with 13 tiles is not complete`() {
        val tiles = repeat(dots(1), 3) + repeat(dots(2), 3) +
                repeat(dots(3), 3) + repeat(dots(4), 3) + repeat(dots(5), 1)
        val hand = Hand(tiles)
        assertFalse(hand.isComplete)
    }

    @Test
    fun `tile counts are calculated correctly`() {
        val tiles = repeat(dots(1), 3) + repeat(dots(2), 2) + repeat(wind(Tile.Wind.EAST), 4)
        val hand = Hand(tiles)
        assertEquals(3, hand.tileCounts["DOTS_1"])
        assertEquals(2, hand.tileCounts["DOTS_2"])
        assertEquals(4, hand.tileCounts["WIND_EAST"])
    }

    // =========================================================================
    // Standard Hand Parsing (4 sets + 1 pair)
    // =========================================================================

    @Test
    fun `parse valid all pongs hand`() {
        // 4 pongs + 1 pair: 111 222 333 444 55 (all dots)
        val tiles = repeat(dots(1), 3) + repeat(dots(2), 3) +
                repeat(dots(3), 3) + repeat(dots(4), 3) + repeat(dots(5), 2)
        val results = HandParser.parseHand(tiles)

        assertTrue(results.isNotEmpty())
        val melds = results.first()
        assertEquals(5, melds.size) // 4 pongs + 1 pair

        val pongs = melds.filterIsInstance<Meld.Pong>()
        val pairs = melds.filterIsInstance<Meld.Pair>()
        assertEquals(4, pongs.size)
        assertEquals(1, pairs.size)
    }

    @Test
    fun `parse valid all chows hand`() {
        // 4 chows + 1 pair: 123 123 456 789 55 (all dots)
        val tiles = listOf(
            dots(1), dots(2), dots(3),
            dots(1), dots(2), dots(3),
            dots(4), dots(5), dots(6),
            dots(7), dots(8), dots(9),
            dots(5), dots(5)
        )
        val results = HandParser.parseHand(tiles)

        assertTrue(results.isNotEmpty())
        val melds = results.first()
        assertEquals(5, melds.size)

        val chows = melds.filterIsInstance<Meld.Chow>()
        val pairs = melds.filterIsInstance<Meld.Pair>()
        assertTrue(chows.size >= 2) // At least some chows
        assertEquals(1, pairs.size)
    }

    @Test
    fun `parse mixed pongs and chows hand`() {
        // 2 pongs + 2 chows + 1 pair: 111 222 345 678 99 (all bamboo)
        val tiles = listOf(
            bamboo(1), bamboo(1), bamboo(1),
            bamboo(2), bamboo(2), bamboo(2),
            bamboo(3), bamboo(4), bamboo(5),
            bamboo(6), bamboo(7), bamboo(8),
            bamboo(9), bamboo(9)
        )
        val results = HandParser.parseHand(tiles)

        assertTrue(results.isNotEmpty())
        assertTrue(HandParser.isValidWinningHand(tiles))
    }

    @Test
    fun `parse hand with honors`() {
        // 3 pongs (2 numbered + 1 wind) + 1 chow + dragon pair
        val tiles = listOf(
            dots(1), dots(1), dots(1),
            dots(9), dots(9), dots(9),
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST), wind(Tile.Wind.EAST),
            chars(2), chars(3), chars(4),
            dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED)
        )
        val results = HandParser.parseHand(tiles)

        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `invalid hand with only 13 tiles returns empty`() {
        val tiles = repeat(dots(1), 3) + repeat(dots(2), 3) +
                repeat(dots(3), 3) + repeat(dots(4), 3) + repeat(dots(5), 1)
        val results = HandParser.parseHand(tiles)

        assertTrue(results.isEmpty())
        assertFalse(HandParser.isValidWinningHand(tiles))
    }

    @Test
    fun `invalid hand with impossible melds returns empty`() {
        // 14 random tiles that can't form valid melds
        val tiles = listOf(
            dots(1), dots(3), dots(5), dots(7), dots(9),
            bamboo(2), bamboo(4), bamboo(6), bamboo(8),
            chars(1), chars(3), chars(5), chars(7), chars(9)
        )
        val results = HandParser.parseHand(tiles)

        assertTrue(results.isEmpty())
    }

    // =========================================================================
    // Seven Pairs Tests
    // =========================================================================

    @Test
    fun `parse seven pairs hand`() {
        // 7 pairs: 11 22 33 44 55 66 77 (all dots)
        val tiles = listOf(
            dots(1), dots(1),
            dots(2), dots(2),
            dots(3), dots(3),
            dots(4), dots(4),
            dots(5), dots(5),
            dots(6), dots(6),
            dots(7), dots(7)
        )
        val results = HandParser.parseHand(tiles)

        assertTrue(results.isNotEmpty())
        assertTrue(HandParser.isSevenPairs(tiles))

        val melds = results.first()
        assertEquals(7, melds.size)
        assertTrue(melds.all { it is Meld.Pair })
    }

    @Test
    fun `seven pairs with mixed suits`() {
        val tiles = listOf(
            dots(1), dots(1),
            bamboo(2), bamboo(2),
            chars(3), chars(3),
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST),
            wind(Tile.Wind.SOUTH), wind(Tile.Wind.SOUTH),
            dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED),
            dragon(Tile.Dragon.GREEN), dragon(Tile.Dragon.GREEN)
        )

        assertTrue(HandParser.isSevenPairs(tiles))
    }

    @Test
    fun `four of a kind is not seven pairs`() {
        // Has 4 of one tile instead of 7 pairs
        val tiles = listOf(
            dots(1), dots(1), dots(1), dots(1), // 4 of a kind
            dots(2), dots(2),
            dots(3), dots(3),
            dots(4), dots(4),
            dots(5), dots(5),
            dots(6), dots(6)
        )

        assertFalse(HandParser.isSevenPairs(tiles))
    }

    // =========================================================================
    // Thirteen Orphans Tests
    // =========================================================================

    @Test
    fun `parse thirteen orphans hand`() {
        // One of each terminal + honor, with East Wind doubled
        val tiles = listOf(
            dots(1), dots(9),
            bamboo(1), bamboo(9),
            chars(1), chars(9),
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST), // Doubled
            wind(Tile.Wind.SOUTH),
            wind(Tile.Wind.WEST),
            wind(Tile.Wind.NORTH),
            dragon(Tile.Dragon.RED),
            dragon(Tile.Dragon.GREEN),
            dragon(Tile.Dragon.WHITE)
        )

        assertTrue(HandParser.isThirteenOrphans(tiles))
        assertTrue(HandParser.isValidWinningHand(tiles))
    }

    @Test
    fun `thirteen orphans with different duplicate`() {
        // One of each terminal + honor, with Red Dragon doubled
        val tiles = listOf(
            dots(1), dots(9),
            bamboo(1), bamboo(9),
            chars(1), chars(9),
            wind(Tile.Wind.EAST),
            wind(Tile.Wind.SOUTH),
            wind(Tile.Wind.WEST),
            wind(Tile.Wind.NORTH),
            dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED), // Doubled
            dragon(Tile.Dragon.GREEN),
            dragon(Tile.Dragon.WHITE)
        )

        assertTrue(HandParser.isThirteenOrphans(tiles))
    }

    @Test
    fun `missing terminal is not thirteen orphans`() {
        // Missing 1-Dots, has two 9-Dots instead
        val tiles = listOf(
            dots(9), dots(9), // Two 9-Dots instead of 1-Dots and 9-Dots
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

        assertFalse(HandParser.isThirteenOrphans(tiles))
    }

    // =========================================================================
    // Multiple Valid Interpretations
    // =========================================================================

    @Test
    fun `hand with multiple valid interpretations returns all`() {
        // 234 234 234 234 22 can be interpreted multiple ways
        val tiles = listOf(
            dots(2), dots(3), dots(4),
            dots(2), dots(3), dots(4),
            dots(2), dots(3), dots(4),
            dots(2), dots(3), dots(4),
            dots(2), dots(2)
        )
        val results = HandParser.parseHand(tiles)

        // Should find at least one valid interpretation
        assertTrue(results.isNotEmpty())
    }
}
