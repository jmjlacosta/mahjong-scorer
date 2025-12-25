package com.jmjlacosta.mahjongscorer

import com.google.common.truth.Truth.assertThat
import com.jmjlacosta.mahjongscorer.model.Hand
import com.jmjlacosta.mahjongscorer.model.Suit
import com.jmjlacosta.mahjongscorer.model.Tile
import com.jmjlacosta.mahjongscorer.scoring.Pattern
import com.jmjlacosta.mahjongscorer.scoring.Score
import com.jmjlacosta.mahjongscorer.scoring.ScoringEngine
import com.jmjlacosta.mahjongscorer.scoring.WinContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class ScoringEngineTest {

    // =========================================================================
    // Helper functions
    // =========================================================================

    private fun dots(n: Int) = Tile.NumberedTile(Suit.DOTS, n)
    private fun bamboo(n: Int) = Tile.NumberedTile(Suit.BAMBOO, n)
    private fun chars(n: Int) = Tile.NumberedTile(Suit.CHARACTERS, n)
    private fun wind(w: Tile.Wind) = Tile.WindTile(w)
    private fun dragon(d: Tile.Dragon) = Tile.DragonTile(d)
    private fun repeat(tile: Tile, n: Int): List<Tile> = List(n) { tile }

    private fun hasPattern(score: Score, pattern: Pattern): Boolean {
        return score.items.any { it.pattern == pattern }
    }

    // =========================================================================
    // Parameterized Tests - Score Calculations
    // =========================================================================

    companion object {
        private fun dots(n: Int) = Tile.NumberedTile(Suit.DOTS, n)
        private fun bamboo(n: Int) = Tile.NumberedTile(Suit.BAMBOO, n)
        private fun chars(n: Int) = Tile.NumberedTile(Suit.CHARACTERS, n)
        private fun wind(w: Tile.Wind) = Tile.WindTile(w)
        private fun dragon(d: Tile.Dragon) = Tile.DragonTile(d)
        private fun repeat(tile: Tile, n: Int): List<Tile> = List(n) { tile }

        @JvmStatic
        fun scoringCases(): Stream<Arguments> = Stream.of(
            // Basic win + concealed
            Arguments.of(
                listOf(
                    dots(1), dots(2), dots(3),
                    dots(4), dots(5), dots(6),
                    bamboo(2), bamboo(3), bamboo(4),
                    chars(5), chars(6), chars(7),
                    wind(Tile.Wind.EAST), wind(Tile.Wind.EAST)
                ),
                WinContext.DEFAULT,
                3, // Basic (1) + All Chows (1) + Concealed (1)
                "All chows + concealed"
            ),
            // Pure hand + all pongs + concealed
            Arguments.of(
                repeat(dots(1), 3) + repeat(dots(2), 3) +
                        repeat(dots(3), 3) + repeat(dots(4), 3) + repeat(dots(5), 2),
                WinContext.DEFAULT,
                10, // Basic (1) + All Pongs (2) + Pure Hand (6) + Concealed (1)
                "Pure hand + all pongs + concealed"
            ),
            // Pure hand + all pongs + self draw + concealed
            Arguments.of(
                repeat(dots(1), 3) + repeat(dots(2), 3) +
                        repeat(dots(3), 3) + repeat(dots(4), 3) + repeat(dots(5), 2),
                WinContext.SELF_DRAW,
                11, // Basic (1) + Self Draw (1) + Concealed (1) + All Pongs (2) + Pure Hand (6)
                "Pure hand + all pongs + self draw"
            ),
            // Seven pairs + pure hand
            Arguments.of(
                listOf(
                    dots(1), dots(1), dots(2), dots(2), dots(3), dots(3), dots(4), dots(4),
                    dots(5), dots(5), dots(6), dots(6), dots(7), dots(7)
                ),
                WinContext.DEFAULT,
                12, // Basic (1) + Seven Pairs (4) + Pure Hand (6) + Concealed (1)
                "Seven pairs + pure hand"
            )
        )

        @JvmStatic
        fun invalidHandCases(): Stream<Arguments> = Stream.of(
            Arguments.of(emptyList<Tile>(), "Empty hand"),
            Arguments.of(
                repeat(dots(1), 3) + repeat(dots(2), 3) +
                        repeat(dots(3), 3) + repeat(dots(4), 3) + dots(5),
                "13 tiles"
            ),
            Arguments.of(
                listOf(
                    dots(1), dots(3), dots(5), dots(7), dots(9),
                    bamboo(2), bamboo(4), bamboo(6), bamboo(8),
                    chars(1), chars(3), chars(5), chars(7), chars(9)
                ),
                "Invalid melds"
            )
        )
    }

    @ParameterizedTest(name = "{3}: {2} points")
    @MethodSource("scoringCases")
    fun `scoring calculations`(tiles: List<Tile>, context: WinContext, expectedPoints: Int, description: String) {
        val hand = Hand(tiles)
        val score = ScoringEngine.calculateScore(hand, context)
        assertThat(score.totalPoints).isEqualTo(expectedPoints)
    }

    @ParameterizedTest(name = "Invalid: {1}")
    @MethodSource("invalidHandCases")
    fun `invalid hands score zero`(tiles: List<Tile>, description: String) {
        val score = ScoringEngine.calculateScore(tiles)
        assertThat(score.totalPoints).isEqualTo(0)
    }

    // =========================================================================
    // Basic Win Tests
    // =========================================================================

    @Test
    fun `basic win scores at least 1 point`() {
        val tiles = listOf(
            dots(1), dots(2), dots(3),
            dots(4), dots(5), dots(6),
            bamboo(2), bamboo(3), bamboo(4),
            chars(5), chars(6), chars(7),
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST)
        )
        val score = ScoringEngine.calculateScore(tiles)

        assertThat(hasPattern(score, Pattern.BASIC_WIN)).isTrue()
        assertThat(score.totalPoints).isAtLeast(1)
    }

    // =========================================================================
    // Self-Draw Tests
    // =========================================================================

    @Test
    fun `self draw adds 1 point`() {
        val tiles = listOf(
            dots(1), dots(2), dots(3),
            dots(4), dots(5), dots(6),
            bamboo(2), bamboo(3), bamboo(4),
            chars(5), chars(6), chars(7),
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST)
        )
        val basicScore = ScoringEngine.calculateScore(tiles)
        val selfDrawScore = ScoringEngine.calculateScoreSelfDraw(tiles)

        assertThat(hasPattern(selfDrawScore, Pattern.SELF_DRAW)).isTrue()
        assertThat(selfDrawScore.totalPoints).isEqualTo(basicScore.totalPoints + 1)
    }

    // =========================================================================
    // All Pongs Tests
    // =========================================================================

    @Test
    fun `all pongs scores correctly`() {
        val tiles = repeat(dots(1), 3) + repeat(dots(2), 3) +
                repeat(dots(3), 3) + repeat(wind(Tile.Wind.EAST), 3) +
                repeat(dragon(Tile.Dragon.RED), 2)
        val score = ScoringEngine.calculateScore(tiles)

        assertThat(hasPattern(score, Pattern.ALL_PONGS)).isTrue()
        assertThat(score.totalPoints).isAtLeast(3) // Basic (1) + All Pongs (2)
    }

    // =========================================================================
    // Pure Hand Tests
    // =========================================================================

    @Test
    fun `pure hand all dots scores correctly`() {
        val tiles = repeat(dots(1), 3) + repeat(dots(2), 3) +
                repeat(dots(3), 3) + repeat(dots(4), 3) + repeat(dots(5), 2)
        val hand = Hand(tiles)
        val score = ScoringEngine.calculateScore(hand, WinContext.DEFAULT)

        assertThat(hasPattern(score, Pattern.PURE_HAND)).isTrue()
        assertThat(hasPattern(score, Pattern.ALL_PONGS)).isTrue()
        assertThat(score.totalPoints).isAtLeast(7)
    }

    @Test
    fun `pure hand does not include half flush`() {
        val tiles = repeat(dots(1), 3) + repeat(dots(2), 3) +
                repeat(dots(3), 3) + repeat(dots(4), 3) + repeat(dots(5), 2)
        val hand = Hand(tiles)
        val score = ScoringEngine.calculateScore(hand, WinContext.DEFAULT)

        assertThat(hasPattern(score, Pattern.PURE_HAND)).isTrue()
        assertThat(hasPattern(score, Pattern.HALF_FLUSH)).isFalse()
    }

    // =========================================================================
    // Half Flush Tests
    // =========================================================================

    @Test
    fun `half flush scores correctly`() {
        val tiles = listOf(
            dots(1), dots(1), dots(1),
            dots(2), dots(3), dots(4),
            dots(5), dots(5), dots(5),
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST), wind(Tile.Wind.EAST),
            dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED)
        )
        val score = ScoringEngine.calculateScore(tiles)

        assertThat(hasPattern(score, Pattern.HALF_FLUSH)).isTrue()
        assertThat(score.totalPoints).isAtLeast(4) // Basic (1) + Half Flush (3)
    }

    // =========================================================================
    // All Honors Tests
    // =========================================================================

    @Test
    fun `all honors scores correctly`() {
        val tiles = listOf(
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST), wind(Tile.Wind.EAST),
            wind(Tile.Wind.SOUTH), wind(Tile.Wind.SOUTH), wind(Tile.Wind.SOUTH),
            wind(Tile.Wind.WEST), wind(Tile.Wind.WEST), wind(Tile.Wind.WEST),
            dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED),
            dragon(Tile.Dragon.GREEN), dragon(Tile.Dragon.GREEN)
        )
        val score = ScoringEngine.calculateScore(tiles)

        assertThat(hasPattern(score, Pattern.ALL_HONORS)).isTrue()
        assertThat(score.totalPoints).isAtLeast(9) // Basic (1) + All Honors (8)
    }

    // =========================================================================
    // All Terminals Tests
    // =========================================================================

    @Test
    fun `all terminals scores correctly`() {
        val tiles = listOf(
            dots(1), dots(1), dots(1),
            dots(9), dots(9), dots(9),
            bamboo(1), bamboo(1), bamboo(1),
            bamboo(9), bamboo(9), bamboo(9),
            chars(1), chars(1)
        )
        val score = ScoringEngine.calculateScore(tiles)

        assertThat(hasPattern(score, Pattern.ALL_TERMINALS)).isTrue()
        assertThat(score.totalPoints).isAtLeast(9) // Basic (1) + All Terminals (8)
    }

    // =========================================================================
    // Seven Pairs Tests
    // =========================================================================

    @Test
    fun `seven pairs scores correctly`() {
        val tiles = listOf(
            dots(1), dots(1),
            dots(2), dots(2),
            dots(3), dots(3),
            dots(4), dots(4),
            dots(5), dots(5),
            dots(6), dots(6),
            dots(7), dots(7)
        )
        val score = ScoringEngine.calculateScore(tiles)

        assertThat(hasPattern(score, Pattern.SEVEN_PAIRS)).isTrue()
        assertThat(hasPattern(score, Pattern.PURE_HAND)).isTrue() // Also pure hand
        assertThat(score.totalPoints).isAtLeast(5) // Basic (1) + Seven Pairs (4)
    }

    // =========================================================================
    // Thirteen Orphans Tests
    // =========================================================================

    @Test
    fun `thirteen orphans scores correctly`() {
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
        val score = ScoringEngine.calculateScore(tiles)

        assertThat(hasPattern(score, Pattern.THIRTEEN_ORPHANS)).isTrue()
        assertThat(score.totalPoints).isAtLeast(14) // Basic (1) + Thirteen Orphans (13)
    }

    // =========================================================================
    // Score Breakdown Tests
    // =========================================================================

    @Test
    fun `score breakdown contains chinese names`() {
        val tiles = repeat(dots(1), 3) + repeat(dots(2), 3) +
                repeat(dots(3), 3) + repeat(dots(4), 3) + repeat(dots(5), 2)
        val score = ScoringEngine.calculateScore(tiles)

        assertThat(score.chineseSummary).contains("胡") // Basic win
        assertThat(score.chineseBreakdown).contains("总分")
    }

    @Test
    fun `score breakdown contains english names`() {
        val tiles = repeat(dots(1), 3) + repeat(dots(2), 3) +
                repeat(dots(3), 3) + repeat(dots(4), 3) + repeat(dots(5), 2)
        val score = ScoringEngine.calculateScore(tiles)

        assertThat(score.englishSummary).contains("Basic Win")
        assertThat(score.englishBreakdown).contains("Total")
    }

    // =========================================================================
    // Stacked Bonuses Tests
    // =========================================================================

    @Test
    fun `multiple bonuses stack correctly`() {
        // All pongs + Pure hand + Self draw + Concealed
        val tiles = repeat(dots(1), 3) + repeat(dots(2), 3) +
                repeat(dots(3), 3) + repeat(dots(4), 3) + repeat(dots(5), 2)
        val hand = Hand(tiles)
        val score = ScoringEngine.calculateScore(hand, WinContext.SELF_DRAW)

        assertThat(hasPattern(score, Pattern.BASIC_WIN)).isTrue()      // +1
        assertThat(hasPattern(score, Pattern.SELF_DRAW)).isTrue()      // +1
        assertThat(hasPattern(score, Pattern.CONCEALED_HAND)).isTrue() // +1
        assertThat(hasPattern(score, Pattern.ALL_PONGS)).isTrue()      // +2
        assertThat(hasPattern(score, Pattern.PURE_HAND)).isTrue()      // +6
        assertThat(score.totalPoints).isEqualTo(11)
    }

    // =========================================================================
    // All Chows Tests
    // =========================================================================

    @Test
    fun `all chows scores correctly`() {
        val tiles = listOf(
            dots(1), dots(2), dots(3),
            dots(4), dots(5), dots(6),
            bamboo(2), bamboo(3), bamboo(4),
            chars(5), chars(6), chars(7),
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST)
        )
        val score = ScoringEngine.calculateScore(tiles)

        assertThat(hasPattern(score, Pattern.ALL_CHOWS)).isTrue()
        assertThat(score.totalPoints).isAtLeast(2) // Basic (1) + All Chows (1)
    }

    // =========================================================================
    // Dragon Pung Tests
    // =========================================================================

    @Test
    fun `dragon pung scores correctly`() {
        val tiles = listOf(
            dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED),
            dots(1), dots(2), dots(3),
            dots(4), dots(5), dots(6),
            bamboo(2), bamboo(3), bamboo(4),
            chars(5), chars(5)
        )
        val score = ScoringEngine.calculateScore(tiles)

        assertThat(hasPattern(score, Pattern.DRAGON_PUNG)).isTrue()
        assertThat(score.totalPoints).isAtLeast(2) // Basic (1) + Dragon Pung (1)
    }

    @Test
    fun `multiple dragon pungs stack`() {
        val tiles = listOf(
            dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED),
            dragon(Tile.Dragon.GREEN), dragon(Tile.Dragon.GREEN), dragon(Tile.Dragon.GREEN),
            dots(1), dots(2), dots(3),
            bamboo(4), bamboo(5), bamboo(6),
            chars(5), chars(5)
        )
        val score = ScoringEngine.calculateScore(tiles)

        // Should have 2 dragon pungs
        val dragonPungCount = score.items.count { it.pattern == Pattern.DRAGON_PUNG }
        assertThat(dragonPungCount).isEqualTo(2)
    }

    // =========================================================================
    // Wind Pung Tests
    // =========================================================================

    @Test
    fun `seat wind pung scores correctly`() {
        val tiles = listOf(
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST), wind(Tile.Wind.EAST),
            dots(1), dots(2), dots(3),
            dots(4), dots(5), dots(6),
            bamboo(2), bamboo(3), bamboo(4),
            chars(5), chars(5)
        )
        val context = WinContext(seatWind = Tile.Wind.EAST)
        val hand = Hand(tiles)
        val score = ScoringEngine.calculateScore(hand, context)

        assertThat(hasPattern(score, Pattern.SEAT_WIND_PUNG)).isTrue()
    }

    @Test
    fun `seat and round wind stack for double points`() {
        val tiles = listOf(
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST), wind(Tile.Wind.EAST),
            dots(1), dots(2), dots(3),
            dots(4), dots(5), dots(6),
            bamboo(2), bamboo(3), bamboo(4),
            chars(5), chars(5)
        )
        val context = WinContext(
            seatWind = Tile.Wind.EAST,
            roundWind = Tile.Wind.EAST
        )
        val hand = Hand(tiles)
        val score = ScoringEngine.calculateScore(hand, context)

        assertThat(hasPattern(score, Pattern.SEAT_WIND_PUNG)).isTrue()
        assertThat(hasPattern(score, Pattern.ROUND_WIND_PUNG)).isTrue()
    }

    // =========================================================================
    // Mixed Terminals Tests
    // =========================================================================

    @Test
    fun `mixed terminals scores correctly`() {
        val tiles = listOf(
            dots(1), dots(1), dots(1),
            dots(9), dots(9), dots(9),
            bamboo(1), bamboo(1), bamboo(1),
            wind(Tile.Wind.EAST), wind(Tile.Wind.EAST), wind(Tile.Wind.EAST),
            dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED)
        )
        val score = ScoringEngine.calculateScore(tiles)

        assertThat(hasPattern(score, Pattern.MIXED_TERMINALS)).isTrue()
        assertThat(score.totalPoints).isAtLeast(7) // Basic (1) + Mixed Terminals (6)
    }

    // =========================================================================
    // Little Three Dragons Tests
    // =========================================================================

    @Test
    fun `little three dragons scores correctly`() {
        val tiles = listOf(
            dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED),
            dragon(Tile.Dragon.GREEN), dragon(Tile.Dragon.GREEN), dragon(Tile.Dragon.GREEN),
            dots(1), dots(2), dots(3),
            bamboo(4), bamboo(5), bamboo(6),
            dragon(Tile.Dragon.WHITE), dragon(Tile.Dragon.WHITE)
        )
        val score = ScoringEngine.calculateScore(tiles)

        assertThat(hasPattern(score, Pattern.LITTLE_THREE_DRAGONS)).isTrue()
        assertThat(score.totalPoints).isAtLeast(7) // Basic (1) + Little Three Dragons (6)
        // Also should have 2 dragon pungs
        assertThat(score.items.count { it.pattern == Pattern.DRAGON_PUNG }).isEqualTo(2)
    }

    // =========================================================================
    // Big Three Dragons Tests
    // =========================================================================

    @Test
    fun `big three dragons scores correctly`() {
        val tiles = listOf(
            dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED),
            dragon(Tile.Dragon.GREEN), dragon(Tile.Dragon.GREEN), dragon(Tile.Dragon.GREEN),
            dragon(Tile.Dragon.WHITE), dragon(Tile.Dragon.WHITE), dragon(Tile.Dragon.WHITE),
            dots(1), dots(2), dots(3),
            bamboo(5), bamboo(5)
        )
        val score = ScoringEngine.calculateScore(tiles)

        assertThat(hasPattern(score, Pattern.BIG_THREE_DRAGONS)).isTrue()
        assertThat(hasPattern(score, Pattern.LITTLE_THREE_DRAGONS)).isFalse() // Superseded
        assertThat(score.totalPoints).isAtLeast(11) // Basic (1) + Big Three Dragons (10)
        // Also should have 3 dragon pungs stacking
        assertThat(score.items.count { it.pattern == Pattern.DRAGON_PUNG }).isEqualTo(3)
    }

    @Test
    fun `big three dragons full scoring`() {
        // Big Three Dragons + 3 Dragon Pungs + All Chows excluded (has pungs)
        val tiles = listOf(
            dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED), dragon(Tile.Dragon.RED),
            dragon(Tile.Dragon.GREEN), dragon(Tile.Dragon.GREEN), dragon(Tile.Dragon.GREEN),
            dragon(Tile.Dragon.WHITE), dragon(Tile.Dragon.WHITE), dragon(Tile.Dragon.WHITE),
            dots(1), dots(2), dots(3),
            bamboo(5), bamboo(5)
        )
        val hand = Hand(tiles)
        val score = ScoringEngine.calculateScore(hand, WinContext.SELF_DRAW)

        // Basic (1) + Self Draw (1) + Concealed (1) + Big Three Dragons (10) + 3x Dragon Pung (3) = 16
        assertThat(score.totalPoints).isAtLeast(16)
    }
}
