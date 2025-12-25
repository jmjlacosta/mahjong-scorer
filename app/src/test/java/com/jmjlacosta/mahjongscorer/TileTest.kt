package com.jmjlacosta.mahjongscorer

import com.google.common.truth.Truth.assertThat
import com.jmjlacosta.mahjongscorer.model.Suit
import com.jmjlacosta.mahjongscorer.model.Tile
import org.junit.jupiter.api.Test

class TileTest {

    @Test
    fun `all tiles count is 34`() {
        assertThat(Tile.ALL_TILES).hasSize(34)
    }

    @Test
    fun `numbered tiles count is 27`() {
        assertThat(Tile.NUMBERED_TILES).hasSize(27)
    }

    @Test
    fun `each numbered suit has 9 tiles`() {
        assertThat(Tile.tilesOfSuit(Suit.DOTS)).hasSize(9)
        assertThat(Tile.tilesOfSuit(Suit.BAMBOO)).hasSize(9)
        assertThat(Tile.tilesOfSuit(Suit.CHARACTERS)).hasSize(9)
    }

    @Test
    fun `wind tiles count is 4`() {
        assertThat(Tile.WIND_TILES).hasSize(4)
    }

    @Test
    fun `dragon tiles count is 3`() {
        assertThat(Tile.DRAGON_TILES).hasSize(3)
    }

    @Test
    fun `honor tiles count is 7`() {
        assertThat(Tile.HONOR_TILES).hasSize(7)
    }

    @Test
    fun `terminal tiles count is 6`() {
        // 1 and 9 of each numbered suit = 2 * 3 = 6
        assertThat(Tile.TERMINAL_TILES).hasSize(6)
    }

    @Test
    fun `numbered tile has correct chinese name`() {
        val fiveDots = Tile.NumberedTile(Suit.DOTS, 5)
        assertThat(fiveDots.chineseName).isEqualTo("五筒")
    }

    @Test
    fun `wind tile has correct chinese name`() {
        val eastWind = Tile.WindTile(Tile.Wind.EAST)
        assertThat(eastWind.chineseName).isEqualTo("东")
    }

    @Test
    fun `dragon tile has correct chinese name`() {
        val redDragon = Tile.DragonTile(Tile.Dragon.RED)
        assertThat(redDragon.chineseName).isEqualTo("中")
    }

    @Test
    fun `terminal detection works correctly`() {
        val oneDots = Tile.NumberedTile(Suit.DOTS, 1)
        val nineBamboo = Tile.NumberedTile(Suit.BAMBOO, 9)
        val fiveChars = Tile.NumberedTile(Suit.CHARACTERS, 5)

        assertThat(oneDots.isTerminal).isTrue()
        assertThat(nineBamboo.isTerminal).isTrue()
        assertThat(fiveChars.isTerminal).isFalse()
    }

    @Test
    fun `honor detection works correctly`() {
        val eastWind = Tile.WindTile(Tile.Wind.EAST)
        val redDragon = Tile.DragonTile(Tile.Dragon.RED)
        val oneDots = Tile.NumberedTile(Suit.DOTS, 1)

        assertThat(eastWind.isHonor).isTrue()
        assertThat(redDragon.isHonor).isTrue()
        assertThat(oneDots.isHonor).isFalse()
    }

    @Test
    fun `find tile by id works`() {
        val tile = Tile.fromId("DOTS_5")
        assertThat(tile).isNotNull()
        assertThat(tile).isInstanceOf(Tile.NumberedTile::class.java)
        assertThat((tile as Tile.NumberedTile).number).isEqualTo(5)
        assertThat(tile.suit).isEqualTo(Suit.DOTS)
    }

    @Test
    fun `all tile ids are unique`() {
        val ids = Tile.ALL_TILES.map { it.id }
        assertThat(ids).containsNoDuplicates()
    }
}
