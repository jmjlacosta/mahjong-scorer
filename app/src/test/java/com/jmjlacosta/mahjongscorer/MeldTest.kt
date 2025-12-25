package com.jmjlacosta.mahjongscorer

import com.google.common.truth.Truth.assertThat
import com.jmjlacosta.mahjongscorer.model.Meld
import com.jmjlacosta.mahjongscorer.model.Suit
import com.jmjlacosta.mahjongscorer.model.Tile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MeldTest {

    // =========================================================================
    // Pong Tests
    // =========================================================================

    @Test
    fun `pong has 3 tiles`() {
        val pong = Meld.Pong(Tile.NumberedTile(Suit.DOTS, 5))
        assertThat(pong.size).isEqualTo(3)
    }

    @Test
    fun `pong chinese name is correct`() {
        val pong = Meld.Pong(Tile.WindTile(Tile.Wind.EAST))
        assertThat(pong.chineseName).isEqualTo("碰")
    }

    @Test
    fun `pong tiles are identical`() {
        val tile = Tile.DragonTile(Tile.Dragon.RED)
        val pong = Meld.Pong(tile)
        assertThat(pong.tiles).containsExactly(tile, tile, tile)
    }

    // =========================================================================
    // Kong Tests
    // =========================================================================

    @Test
    fun `kong has 4 tiles`() {
        val kong = Meld.Kong(Tile.NumberedTile(Suit.BAMBOO, 7))
        assertThat(kong.size).isEqualTo(4)
    }

    @Test
    fun `exposed kong chinese name is correct`() {
        val kong = Meld.Kong(Tile.NumberedTile(Suit.BAMBOO, 7), isConcealed = false)
        assertThat(kong.chineseName).isEqualTo("明杠")
    }

    @Test
    fun `concealed kong chinese name is correct`() {
        val kong = Meld.Kong(Tile.NumberedTile(Suit.BAMBOO, 7), isConcealed = true)
        assertThat(kong.chineseName).isEqualTo("暗杠")
    }

    @Test
    fun `kong tiles are identical`() {
        val tile = Tile.WindTile(Tile.Wind.NORTH)
        val kong = Meld.Kong(tile)
        assertThat(kong.tiles).containsExactly(tile, tile, tile, tile)
    }

    // =========================================================================
    // Chow Tests
    // =========================================================================

    @Test
    fun `chow has 3 tiles`() {
        val chow = Meld.Chow(Tile.NumberedTile(Suit.CHARACTERS, 2))
        assertThat(chow.size).isEqualTo(3)
    }

    @Test
    fun `chow chinese name is correct`() {
        val chow = Meld.Chow(Tile.NumberedTile(Suit.CHARACTERS, 2))
        assertThat(chow.chineseName).isEqualTo("吃")
    }

    @Test
    fun `chow tiles are consecutive`() {
        val chow = Meld.Chow(Tile.NumberedTile(Suit.DOTS, 3))
        assertThat(chow.numbers).containsExactly(3, 4, 5).inOrder()
    }

    @Test
    fun `chow tiles are same suit`() {
        val chow = Meld.Chow(Tile.NumberedTile(Suit.BAMBOO, 5))
        assertThat(chow.tiles.all { (it as Tile.NumberedTile).suit == Suit.BAMBOO }).isTrue()
    }

    @Test
    fun `chow starting at 7 is valid`() {
        val chow = Meld.Chow(Tile.NumberedTile(Suit.DOTS, 7))
        assertThat(chow.numbers).containsExactly(7, 8, 9).inOrder()
    }

    @Test
    fun `chow starting at 8 throws exception`() {
        assertThrows<IllegalArgumentException> {
            Meld.Chow(Tile.NumberedTile(Suit.DOTS, 8))
        }
    }

    @Test
    fun `chow starting at 9 throws exception`() {
        assertThrows<IllegalArgumentException> {
            Meld.Chow(Tile.NumberedTile(Suit.CHARACTERS, 9))
        }
    }

    // =========================================================================
    // Pair Tests
    // =========================================================================

    @Test
    fun `pair has 2 tiles`() {
        val pair = Meld.Pair(Tile.DragonTile(Tile.Dragon.WHITE))
        assertThat(pair.size).isEqualTo(2)
    }

    @Test
    fun `pair chinese name is correct`() {
        val pair = Meld.Pair(Tile.DragonTile(Tile.Dragon.WHITE))
        assertThat(pair.chineseName).isEqualTo("对")
    }

    @Test
    fun `pair tiles are identical`() {
        val tile = Tile.NumberedTile(Suit.CHARACTERS, 9)
        val pair = Meld.Pair(tile)
        assertThat(pair.tiles).containsExactly(tile, tile)
    }

    // =========================================================================
    // Companion Factory Tests
    // =========================================================================

    @Test
    fun `tryPong returns pong for identical tiles`() {
        val tile = Tile.NumberedTile(Suit.DOTS, 5)
        val pong = Meld.tryPong(tile, tile, tile)
        assertThat(pong).isNotNull()
        assertThat(pong?.tile).isEqualTo(tile)
    }

    @Test
    fun `tryPong returns null for different tiles`() {
        val t1 = Tile.NumberedTile(Suit.DOTS, 5)
        val t2 = Tile.NumberedTile(Suit.DOTS, 6)
        val pong = Meld.tryPong(t1, t1, t2)
        assertThat(pong).isNull()
    }

    @Test
    fun `tryKong returns kong for identical tiles`() {
        val tile = Tile.WindTile(Tile.Wind.SOUTH)
        val kong = Meld.tryKong(tile, tile, tile, tile)
        assertThat(kong).isNotNull()
        assertThat(kong?.tile).isEqualTo(tile)
    }

    @Test
    fun `tryKong returns null for different tiles`() {
        val t1 = Tile.WindTile(Tile.Wind.SOUTH)
        val t2 = Tile.WindTile(Tile.Wind.NORTH)
        val kong = Meld.tryKong(t1, t1, t1, t2)
        assertThat(kong).isNull()
    }

    @Test
    fun `tryChow returns chow for consecutive tiles`() {
        val t1 = Tile.NumberedTile(Suit.BAMBOO, 3)
        val t2 = Tile.NumberedTile(Suit.BAMBOO, 4)
        val t3 = Tile.NumberedTile(Suit.BAMBOO, 5)
        val chow = Meld.tryChow(t1, t2, t3)
        assertThat(chow).isNotNull()
        assertThat(chow?.startTile).isEqualTo(t1)
    }

    @Test
    fun `tryChow works with unordered tiles`() {
        val t1 = Tile.NumberedTile(Suit.DOTS, 7)
        val t2 = Tile.NumberedTile(Suit.DOTS, 9)
        val t3 = Tile.NumberedTile(Suit.DOTS, 8)
        val chow = Meld.tryChow(t1, t2, t3)
        assertThat(chow).isNotNull()
        assertThat(chow?.startTile?.number).isEqualTo(7)
    }

    @Test
    fun `tryChow returns null for non-consecutive tiles`() {
        val t1 = Tile.NumberedTile(Suit.BAMBOO, 3)
        val t2 = Tile.NumberedTile(Suit.BAMBOO, 4)
        val t3 = Tile.NumberedTile(Suit.BAMBOO, 6)
        val chow = Meld.tryChow(t1, t2, t3)
        assertThat(chow).isNull()
    }

    @Test
    fun `tryChow returns null for different suits`() {
        val t1 = Tile.NumberedTile(Suit.BAMBOO, 3)
        val t2 = Tile.NumberedTile(Suit.DOTS, 4)
        val t3 = Tile.NumberedTile(Suit.BAMBOO, 5)
        val chow = Meld.tryChow(t1, t2, t3)
        assertThat(chow).isNull()
    }

    @Test
    fun `tryChow returns null for honor tiles`() {
        val t1 = Tile.WindTile(Tile.Wind.EAST)
        val t2 = Tile.WindTile(Tile.Wind.SOUTH)
        val t3 = Tile.WindTile(Tile.Wind.WEST)
        val chow = Meld.tryChow(t1, t2, t3)
        assertThat(chow).isNull()
    }

    @Test
    fun `tryPair returns pair for identical tiles`() {
        val tile = Tile.DragonTile(Tile.Dragon.GREEN)
        val pair = Meld.tryPair(tile, tile)
        assertThat(pair).isNotNull()
        assertThat(pair?.tile).isEqualTo(tile)
    }

    @Test
    fun `tryPair returns null for different tiles`() {
        val t1 = Tile.DragonTile(Tile.Dragon.GREEN)
        val t2 = Tile.DragonTile(Tile.Dragon.RED)
        val pair = Meld.tryPair(t1, t2)
        assertThat(pair).isNull()
    }
}
