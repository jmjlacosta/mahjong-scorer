package com.jmjlacosta.mahjongscorer

import com.google.common.truth.Truth.assertThat
import com.jmjlacosta.mahjongscorer.model.Hand
import com.jmjlacosta.mahjongscorer.model.HandParser
import com.jmjlacosta.mahjongscorer.model.Meld
import com.jmjlacosta.mahjongscorer.model.Suit
import com.jmjlacosta.mahjongscorer.model.Tile
import com.jmjlacosta.mahjongscorer.scoring.Pattern
import com.jmjlacosta.mahjongscorer.scoring.PatternDetector
import com.jmjlacosta.mahjongscorer.scoring.WinContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

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
    // Parameterized Tests - Pure Hand Detection
    // =========================================================================

    companion object {
        private fun dots(n: Int) = Tile.NumberedTile(Suit.DOTS, n)
        private fun bamboo(n: Int) = Tile.NumberedTile(Suit.BAMBOO, n)
        private fun chars(n: Int) = Tile.NumberedTile(Suit.CHARACTERS, n)
        private fun wind(w: Tile.Wind) = Tile.WindTile(w)
        private fun dragon(d: Tile.Dragon) = Tile.DragonTile(d)
        private fun repeat(tile: Tile, n: Int): List<Tile> = List(n) { tile }

        @JvmStatic
        fun pureHandCases(): Stream<Arguments> = Stream.of(
            Arguments.of(
                repeat(dots(1), 3) + repeat(dots(2), 3) + repeat(dots(3), 3) +
                        repeat(dots(4), 3) + repeat(dots(5), 2),
                true,
                "All dots"
            ),
            Arguments.of(
                repeat(bamboo(1), 3) + repeat(bamboo(2), 3) + repeat(bamboo(3), 3) +
                        repeat(bamboo(4), 3) + repeat(bamboo(5), 2),
                true,
                "All bamboo"
            ),
            Arguments.of(
                repeat(chars(1), 3) + repeat(chars(2), 3) + repeat(chars(3), 3) +
                        repeat(chars(4), 3) + repeat(chars(5), 2),
                true,
                "All characters"
            ),
            Arguments.of(
                repeat(dots(1), 3) + repeat(bamboo(2), 3) + repeat(dots(3), 3) +
                        repeat(dots(4), 3) + repeat(dots(5), 2),
                false,
                "Mixed suits"
            ),
            Arguments.of(
                repeat(dots(1), 3) + repeat(dots(2), 3) + repeat(dots(3), 3) +
                        repeat(wind(Tile.Wind.EAST), 3) + repeat(dots(5), 2),
                false,
                "With honors"
            )
        )

        @JvmStatic
        fun halfFlushCases(): Stream<Arguments> = Stream.of(
            Arguments.of(
                repeat(dots(1), 3) + repeat(dots(2), 3) + repeat(dots(5), 3) +
                        repeat(wind(Tile.Wind.EAST), 3) + repeat(dragon(Tile.Dragon.RED), 2),
                true,
                "Dots with honors"
            ),
            Arguments.of(
                repeat(bamboo(1), 3) + repeat(bamboo(2), 3) + repeat(bamboo(5), 3) +
                        repeat(dragon(Tile.Dragon.GREEN), 3) + repeat(bamboo(9), 2),
                true,
                "Bamboo with dragon - is half flush"
            ),
            Arguments.of(
                repeat(dots(1), 3) + repeat(bamboo(2), 3) + repeat(chars(5), 3) +
                        repeat(wind(Tile.Wind.EAST), 3) + repeat(dragon(Tile.Dragon.RED), 2),
                false,
                "Mixed numbered suits"
            )
        )

        @JvmStatic
        fun allHonorsCases(): Stream<Arguments> = Stream.of(
            Arguments.of(
                repeat(wind(Tile.Wind.EAST), 3) + repeat(wind(Tile.Wind.SOUTH), 3) +
                        repeat(wind(Tile.Wind.WEST), 3) + repeat(dragon(Tile.Dragon.RED), 3) +
                        repeat(dragon(Tile.Dragon.GREEN), 2),
                true,
                "All winds and dragons"
            ),
            Arguments.of(
                repeat(wind(Tile.Wind.EAST), 3) + repeat(wind(Tile.Wind.SOUTH), 3) +
                        repeat(wind(Tile.Wind.WEST), 3) + repeat(dots(1), 3) +
                        repeat(dragon(Tile.Dragon.GREEN), 2),
                false,
                "With numbered tile"
            )
        )

        @JvmStatic
        fun allTerminalsCases(): Stream<Arguments> = Stream.of(
            Arguments.of(
                repeat(dots(1), 3) + repeat(dots(9), 3) + repeat(bamboo(1), 3) +
                        repeat(bamboo(9), 3) + repeat(chars(1), 2),
                true,
                "All 1s and 9s"
            ),
            Arguments.of(
                repeat(dots(1), 3) + repeat(dots(9), 3) + repeat(bamboo(1), 3) +
                        repeat(bamboo(9), 3) + repeat(chars(5), 2),
                false,
                "With middle tile"
            ),
            Arguments.of(
                repeat(dots(1), 3) + repeat(dots(9), 3) + repeat(bamboo(1), 3) +
                        repeat(bamboo(9), 3) + repeat(wind(Tile.Wind.EAST), 2),
                false,
                "With honor tile"
            )
        )

        @JvmStatic
        fun mixedTerminalsCases(): Stream<Arguments> = Stream.of(
            Arguments.of(
                repeat(dots(1), 3) + repeat(dots(9), 3) + repeat(bamboo(1), 3) +
                        repeat(wind(Tile.Wind.EAST), 3) + repeat(dragon(Tile.Dragon.RED), 2),
                true,
                "Terminals and honors"
            ),
            Arguments.of(
                repeat(dots(1), 3) + repeat(dots(9), 3) + repeat(bamboo(1), 3) +
                        repeat(bamboo(9), 3) + repeat(chars(1), 2),
                false,
                "All terminals - not mixed"
            ),
            Arguments.of(
                repeat(wind(Tile.Wind.EAST), 3) + repeat(wind(Tile.Wind.SOUTH), 3) +
                        repeat(wind(Tile.Wind.WEST), 3) + repeat(dragon(Tile.Dragon.RED), 3) +
                        repeat(dragon(Tile.Dragon.GREEN), 2),
                false,
                "All honors - not mixed"
            ),
            Arguments.of(
                repeat(dots(1), 3) + repeat(dots(5), 3) + repeat(bamboo(1), 3) +
                        repeat(wind(Tile.Wind.EAST), 3) + repeat(dragon(Tile.Dragon.RED), 2),
                false,
                "With simple tile"
            )
        )
    }

    @ParameterizedTest(name = "{2}: isPureHand = {1}")
    @MethodSource("pureHandCases")
    fun `pure hand detection`(tiles: List<Tile>, expected: Boolean, description: String) {
        assertThat(PatternDetector.isPureHand(tiles)).isEqualTo(expected)
    }

    @ParameterizedTest(name = "{2}: isHalfFlush = {1}")
    @MethodSource("halfFlushCases")
    fun `half flush detection`(tiles: List<Tile>, expected: Boolean, description: String) {
        assertThat(PatternDetector.isHalfFlush(tiles)).isEqualTo(expected)
    }

    @ParameterizedTest(name = "{2}: isAllHonors = {1}")
    @MethodSource("allHonorsCases")
    fun `all honors detection`(tiles: List<Tile>, expected: Boolean, description: String) {
        assertThat(PatternDetector.isAllHonors(tiles)).isEqualTo(expected)
    }

    @ParameterizedTest(name = "{2}: isAllTerminals = {1}")
    @MethodSource("allTerminalsCases")
    fun `all terminals detection`(tiles: List<Tile>, expected: Boolean, description: String) {
        assertThat(PatternDetector.isAllTerminals(tiles)).isEqualTo(expected)
    }

    @ParameterizedTest(name = "{2}: isMixedTerminals = {1}")
    @MethodSource("mixedTerminalsCases")
    fun `mixed terminals detection`(tiles: List<Tile>, expected: Boolean, description: String) {
        assertThat(PatternDetector.isMixedTerminals(tiles)).isEqualTo(expected)
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
        assertThat(PatternDetector.isAllPongs(melds)).isTrue()
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
        assertThat(PatternDetector.isAllPongs(melds)).isTrue()
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
        assertThat(PatternDetector.isAllPongs(melds)).isFalse()
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
        assertThat(PatternDetector.isAllChows(melds)).isTrue()
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
        assertThat(PatternDetector.isAllChows(melds)).isFalse()
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
        assertThat(PatternDetector.isSevenPairs(tiles)).isTrue()
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
        assertThat(PatternDetector.isThirteenOrphans(tiles)).isTrue()
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
        assertThat(PatternDetector.countDragonPungs(melds)).isEqualTo(2)
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
        assertThat(PatternDetector.countDragonPungs(melds)).isEqualTo(1)
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
        assertThat(PatternDetector.hasSeatWindPung(melds, Tile.Wind.EAST)).isTrue()
        assertThat(PatternDetector.hasSeatWindPung(melds, Tile.Wind.SOUTH)).isFalse()
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
        assertThat(PatternDetector.hasSeatWindPung(melds, Tile.Wind.SOUTH)).isTrue()
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
        assertThat(PatternDetector.hasRoundWindPung(melds, Tile.Wind.WEST)).isTrue()
        assertThat(PatternDetector.hasRoundWindPung(melds, Tile.Wind.NORTH)).isFalse()
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
        assertThat(PatternDetector.isLittleThreeDragons(melds)).isTrue()
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
        assertThat(PatternDetector.isLittleThreeDragons(melds)).isFalse()
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
        assertThat(PatternDetector.isLittleThreeDragons(melds)).isFalse()
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
        assertThat(PatternDetector.isBigThreeDragons(melds)).isTrue()
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
        assertThat(PatternDetector.isBigThreeDragons(melds)).isTrue()
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
        assertThat(PatternDetector.isBigThreeDragons(melds)).isFalse()
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

        assertThat(patterns).contains(Pattern.BASIC_WIN)
    }

    @Test
    fun `detect patterns with self draw`() {
        val tiles = repeat(dots(1), 3) + repeat(dots(2), 3) +
                repeat(dots(3), 3) + repeat(dots(4), 3) + repeat(dots(5), 2)
        val hand = handWithMelds(tiles)
        val patterns = PatternDetector.detectPatterns(hand, WinContext.SELF_DRAW)

        assertThat(patterns).contains(Pattern.SELF_DRAW)
        assertThat(patterns).contains(Pattern.CONCEALED_HAND)
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

        assertThat(patterns).contains(Pattern.PURE_HAND)
        assertThat(patterns).doesNotContain(Pattern.HALF_FLUSH)
    }

    @Test
    fun `all pongs pure hand combination`() {
        // Use non-consecutive numbers to force pong interpretation
        val tiles = repeat(dots(1), 3) + repeat(dots(3), 3) +
                repeat(dots(5), 3) + repeat(dots(7), 3) + repeat(dots(9), 2)
        val hand = handWithMelds(tiles)
        val patterns = PatternDetector.detectPatterns(hand, WinContext.DEFAULT)

        assertThat(patterns).contains(Pattern.ALL_PONGS)
        assertThat(patterns).contains(Pattern.PURE_HAND)
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

        assertThat(patterns).contains(Pattern.BIG_THREE_DRAGONS)
        assertThat(patterns).doesNotContain(Pattern.LITTLE_THREE_DRAGONS)
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

        assertThat(patterns).contains(Pattern.SEAT_WIND_PUNG)
        assertThat(patterns).contains(Pattern.ROUND_WIND_PUNG)
        // Should have both for +2 total
        val windPungCount = patterns.count { it == Pattern.SEAT_WIND_PUNG || it == Pattern.ROUND_WIND_PUNG }
        assertThat(windPungCount).isEqualTo(2)
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

        assertThat(patterns).contains(Pattern.BIG_THREE_DRAGONS)
        // Should have 3 dragon pungs
        assertThat(patterns.count { it == Pattern.DRAGON_PUNG }).isEqualTo(3)
    }
}
