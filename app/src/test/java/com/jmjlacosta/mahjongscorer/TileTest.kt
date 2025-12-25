package com.jmjlacosta.mahjongscorer

import com.jmjlacosta.mahjongscorer.model.Suit
import com.jmjlacosta.mahjongscorer.model.Tile
import org.junit.Assert.*
import org.junit.Test

class TileTest {

    @Test
    fun `all tiles count is 34`() {
        assertEquals(34, Tile.ALL_TILES.size)
    }

    @Test
    fun `numbered tiles count is 27`() {
        assertEquals(27, Tile.NUMBERED_TILES.size)
    }

    @Test
    fun `each numbered suit has 9 tiles`() {
        assertEquals(9, Tile.tilesOfSuit(Suit.DOTS).size)
        assertEquals(9, Tile.tilesOfSuit(Suit.BAMBOO).size)
        assertEquals(9, Tile.tilesOfSuit(Suit.CHARACTERS).size)
    }

    @Test
    fun `wind tiles count is 4`() {
        assertEquals(4, Tile.WIND_TILES.size)
    }

    @Test
    fun `dragon tiles count is 3`() {
        assertEquals(3, Tile.DRAGON_TILES.size)
    }

    @Test
    fun `honor tiles count is 7`() {
        assertEquals(7, Tile.HONOR_TILES.size)
    }

    @Test
    fun `terminal tiles count is 6`() {
        // 1 and 9 of each numbered suit = 2 * 3 = 6
        assertEquals(6, Tile.TERMINAL_TILES.size)
    }

    @Test
    fun `numbered tile has correct chinese name`() {
        val fiveDots = Tile.NumberedTile(Suit.DOTS, 5)
        assertEquals("五筒", fiveDots.chineseName)
    }

    @Test
    fun `wind tile has correct chinese name`() {
        val eastWind = Tile.WindTile(Tile.Wind.EAST)
        assertEquals("东", eastWind.chineseName)
    }

    @Test
    fun `dragon tile has correct chinese name`() {
        val redDragon = Tile.DragonTile(Tile.Dragon.RED)
        assertEquals("中", redDragon.chineseName)
    }

    @Test
    fun `terminal detection works correctly`() {
        val oneDots = Tile.NumberedTile(Suit.DOTS, 1)
        val nineBamboo = Tile.NumberedTile(Suit.BAMBOO, 9)
        val fiveChars = Tile.NumberedTile(Suit.CHARACTERS, 5)

        assertTrue(oneDots.isTerminal)
        assertTrue(nineBamboo.isTerminal)
        assertFalse(fiveChars.isTerminal)
    }

    @Test
    fun `honor detection works correctly`() {
        val eastWind = Tile.WindTile(Tile.Wind.EAST)
        val redDragon = Tile.DragonTile(Tile.Dragon.RED)
        val oneDots = Tile.NumberedTile(Suit.DOTS, 1)

        assertTrue(eastWind.isHonor)
        assertTrue(redDragon.isHonor)
        assertFalse(oneDots.isHonor)
    }

    @Test
    fun `find tile by id works`() {
        val tile = Tile.fromId("DOTS_5")
        assertNotNull(tile)
        assertTrue(tile is Tile.NumberedTile)
        assertEquals(5, (tile as Tile.NumberedTile).number)
        assertEquals(Suit.DOTS, tile.suit)
    }

    @Test
    fun `all tile ids are unique`() {
        val ids = Tile.ALL_TILES.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
    }
}
