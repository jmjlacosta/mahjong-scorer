package com.jmjlacosta.mahjongscorer

import com.jmjlacosta.mahjongscorer.model.Meld
import com.jmjlacosta.mahjongscorer.model.Suit
import com.jmjlacosta.mahjongscorer.model.Tile
import org.junit.Assert.*
import org.junit.Test

class MeldTest {

    // =========================================================================
    // Pong Tests
    // =========================================================================

    @Test
    fun `pong has 3 tiles`() {
        val pong = Meld.Pong(Tile.NumberedTile(Suit.DOTS, 5))
        assertEquals(3, pong.size)
    }

    @Test
    fun `pong chinese name is correct`() {
        val pong = Meld.Pong(Tile.WindTile(Tile.Wind.EAST))
        assertEquals("碰", pong.chineseName)
    }

    @Test
    fun `pong tiles are identical`() {
        val tile = Tile.DragonTile(Tile.Dragon.RED)
        val pong = Meld.Pong(tile)
        assertTrue(pong.tiles.all { it == tile })
    }

    // =========================================================================
    // Kong Tests
    // =========================================================================

    @Test
    fun `kong has 4 tiles`() {
        val kong = Meld.Kong(Tile.NumberedTile(Suit.BAMBOO, 7))
        assertEquals(4, kong.size)
    }

    @Test
    fun `exposed kong chinese name is correct`() {
        val kong = Meld.Kong(Tile.NumberedTile(Suit.BAMBOO, 7), isConcealed = false)
        assertEquals("明杠", kong.chineseName)
    }

    @Test
    fun `concealed kong chinese name is correct`() {
        val kong = Meld.Kong(Tile.NumberedTile(Suit.BAMBOO, 7), isConcealed = true)
        assertEquals("暗杠", kong.chineseName)
    }

    @Test
    fun `kong tiles are identical`() {
        val tile = Tile.WindTile(Tile.Wind.NORTH)
        val kong = Meld.Kong(tile)
        assertTrue(kong.tiles.all { it == tile })
    }

    // =========================================================================
    // Chow Tests
    // =========================================================================

    @Test
    fun `chow has 3 tiles`() {
        val chow = Meld.Chow(Tile.NumberedTile(Suit.CHARACTERS, 2))
        assertEquals(3, chow.size)
    }

    @Test
    fun `chow chinese name is correct`() {
        val chow = Meld.Chow(Tile.NumberedTile(Suit.CHARACTERS, 2))
        assertEquals("吃", chow.chineseName)
    }

    @Test
    fun `chow tiles are consecutive`() {
        val chow = Meld.Chow(Tile.NumberedTile(Suit.DOTS, 3))
        assertEquals(listOf(3, 4, 5), chow.numbers)
    }

    @Test
    fun `chow tiles are same suit`() {
        val chow = Meld.Chow(Tile.NumberedTile(Suit.BAMBOO, 5))
        assertTrue(chow.tiles.all { (it as Tile.NumberedTile).suit == Suit.BAMBOO })
    }

    @Test
    fun `chow starting at 7 is valid`() {
        val chow = Meld.Chow(Tile.NumberedTile(Suit.DOTS, 7))
        assertEquals(listOf(7, 8, 9), chow.numbers)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `chow starting at 8 throws exception`() {
        Meld.Chow(Tile.NumberedTile(Suit.DOTS, 8))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `chow starting at 9 throws exception`() {
        Meld.Chow(Tile.NumberedTile(Suit.CHARACTERS, 9))
    }

    // =========================================================================
    // Pair Tests
    // =========================================================================

    @Test
    fun `pair has 2 tiles`() {
        val pair = Meld.Pair(Tile.DragonTile(Tile.Dragon.WHITE))
        assertEquals(2, pair.size)
    }

    @Test
    fun `pair chinese name is correct`() {
        val pair = Meld.Pair(Tile.DragonTile(Tile.Dragon.WHITE))
        assertEquals("对", pair.chineseName)
    }

    @Test
    fun `pair tiles are identical`() {
        val tile = Tile.NumberedTile(Suit.CHARACTERS, 9)
        val pair = Meld.Pair(tile)
        assertTrue(pair.tiles.all { it == tile })
    }

    // =========================================================================
    // Companion Factory Tests
    // =========================================================================

    @Test
    fun `tryPong returns pong for identical tiles`() {
        val tile = Tile.NumberedTile(Suit.DOTS, 5)
        val pong = Meld.tryPong(tile, tile, tile)
        assertNotNull(pong)
        assertEquals(tile, pong?.tile)
    }

    @Test
    fun `tryPong returns null for different tiles`() {
        val t1 = Tile.NumberedTile(Suit.DOTS, 5)
        val t2 = Tile.NumberedTile(Suit.DOTS, 6)
        val pong = Meld.tryPong(t1, t1, t2)
        assertNull(pong)
    }

    @Test
    fun `tryKong returns kong for identical tiles`() {
        val tile = Tile.WindTile(Tile.Wind.SOUTH)
        val kong = Meld.tryKong(tile, tile, tile, tile)
        assertNotNull(kong)
        assertEquals(tile, kong?.tile)
    }

    @Test
    fun `tryKong returns null for different tiles`() {
        val t1 = Tile.WindTile(Tile.Wind.SOUTH)
        val t2 = Tile.WindTile(Tile.Wind.NORTH)
        val kong = Meld.tryKong(t1, t1, t1, t2)
        assertNull(kong)
    }

    @Test
    fun `tryChow returns chow for consecutive tiles`() {
        val t1 = Tile.NumberedTile(Suit.BAMBOO, 3)
        val t2 = Tile.NumberedTile(Suit.BAMBOO, 4)
        val t3 = Tile.NumberedTile(Suit.BAMBOO, 5)
        val chow = Meld.tryChow(t1, t2, t3)
        assertNotNull(chow)
        assertEquals(t1, chow?.startTile)
    }

    @Test
    fun `tryChow works with unordered tiles`() {
        val t1 = Tile.NumberedTile(Suit.DOTS, 7)
        val t2 = Tile.NumberedTile(Suit.DOTS, 9)
        val t3 = Tile.NumberedTile(Suit.DOTS, 8)
        val chow = Meld.tryChow(t1, t2, t3)
        assertNotNull(chow)
        assertEquals(7, chow?.startTile?.number)
    }

    @Test
    fun `tryChow returns null for non-consecutive tiles`() {
        val t1 = Tile.NumberedTile(Suit.BAMBOO, 3)
        val t2 = Tile.NumberedTile(Suit.BAMBOO, 4)
        val t3 = Tile.NumberedTile(Suit.BAMBOO, 6)
        val chow = Meld.tryChow(t1, t2, t3)
        assertNull(chow)
    }

    @Test
    fun `tryChow returns null for different suits`() {
        val t1 = Tile.NumberedTile(Suit.BAMBOO, 3)
        val t2 = Tile.NumberedTile(Suit.DOTS, 4)
        val t3 = Tile.NumberedTile(Suit.BAMBOO, 5)
        val chow = Meld.tryChow(t1, t2, t3)
        assertNull(chow)
    }

    @Test
    fun `tryChow returns null for honor tiles`() {
        val t1 = Tile.WindTile(Tile.Wind.EAST)
        val t2 = Tile.WindTile(Tile.Wind.SOUTH)
        val t3 = Tile.WindTile(Tile.Wind.WEST)
        val chow = Meld.tryChow(t1, t2, t3)
        assertNull(chow)
    }

    @Test
    fun `tryPair returns pair for identical tiles`() {
        val tile = Tile.DragonTile(Tile.Dragon.GREEN)
        val pair = Meld.tryPair(tile, tile)
        assertNotNull(pair)
        assertEquals(tile, pair?.tile)
    }

    @Test
    fun `tryPair returns null for different tiles`() {
        val t1 = Tile.DragonTile(Tile.Dragon.GREEN)
        val t2 = Tile.DragonTile(Tile.Dragon.RED)
        val pair = Meld.tryPair(t1, t2)
        assertNull(pair)
    }
}
