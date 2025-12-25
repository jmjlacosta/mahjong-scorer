package com.jmjlacosta.mahjongscorer.scoring

/**
 * Scoring patterns in Beijing Mahjong.
 * Each pattern has a Chinese name, English name, and point value.
 */
enum class Pattern(
    val chinese: String,
    val english: String,
    val points: Int,
    val description: String
) {
    // =========================================================================
    // Context-based bonuses (how you won)
    // =========================================================================

    /** Basic win - every winning hand gets this */
    BASIC_WIN("胡", "Basic Win", 1, "Completing a winning hand"),

    /** Self-draw - drawing the winning tile yourself */
    SELF_DRAW("自摸", "Self Draw", 1, "Drew the winning tile yourself"),

    /** Concealed hand - no exposed melds */
    CONCEALED_HAND("门清", "Concealed Hand", 1, "All tiles concealed (no exposed melds)"),

    // =========================================================================
    // Meld-based patterns
    // =========================================================================

    /** All Chows - all melds are Chows */
    ALL_CHOWS("平胡", "All Chows", 1, "All melds are Chows, no Pongs or Kongs"),

    /** All Pongs - no chows, only pongs/kongs */
    ALL_PONGS("碰碰胡", "All Pongs", 2, "Hand contains only Pongs and Kongs, no Chows"),

    // =========================================================================
    // Honor tile patterns
    // =========================================================================

    /** Dragon Pung - pung/kong of any dragon tile (+1 per dragon pung) */
    DRAGON_PUNG("三元牌", "Dragon Pung", 1, "Pung or Kong of a dragon tile"),

    /** Seat Wind Pung - pung/kong of player's seat wind */
    SEAT_WIND_PUNG("门风刻", "Seat Wind Pung", 1, "Pung or Kong of your seat wind"),

    /** Round Wind Pung - pung/kong of current round wind */
    ROUND_WIND_PUNG("圈风刻", "Round Wind Pung", 1, "Pung or Kong of the round wind"),

    // =========================================================================
    // Suit-based patterns
    // =========================================================================

    /** Half Flush - one numbered suit plus honor tiles */
    HALF_FLUSH("混一色", "Half Flush", 3, "One numbered suit plus honor tiles"),

    /** Pure Hand - all tiles from one numbered suit */
    PURE_HAND("清一色", "Pure Hand", 6, "All tiles from one numbered suit, no honors"),

    /** All Honors - only wind and dragon tiles */
    ALL_HONORS("字一色", "All Honors", 8, "Only wind and dragon tiles"),

    // =========================================================================
    // Terminal patterns
    // =========================================================================

    /** Mixed Terminals - only terminals (1s, 9s) and honors */
    MIXED_TERMINALS("混老头", "Mixed Terminals", 6, "Only terminal tiles (1s and 9s) plus honor tiles"),

    /** All Terminals - only 1s and 9s */
    ALL_TERMINALS("清老头", "All Terminals", 8, "Only terminal tiles (1s and 9s)"),

    // =========================================================================
    // Dragon combinations
    // =========================================================================

    /** Little Three Dragons - 2 dragon pungs + 1 dragon pair */
    LITTLE_THREE_DRAGONS("小三元", "Little Three Dragons", 6, "Two dragon Pungs and one dragon Pair"),

    /** Big Three Dragons - all 3 dragon pungs */
    BIG_THREE_DRAGONS("大三元", "Big Three Dragons", 10, "Pungs of all three dragons"),

    // =========================================================================
    // Special hands
    // =========================================================================

    /** Seven Pairs - 7 pairs instead of standard melds */
    SEVEN_PAIRS("七对子", "Seven Pairs", 4, "Seven pairs instead of four sets and one pair"),

    /** Thirteen Orphans - one of each terminal and honor plus one duplicate */
    THIRTEEN_ORPHANS("十三幺", "Thirteen Orphans", 13, "One of each terminal and honor tile, plus one duplicate");

    companion object {
        /**
         * Patterns that are mutually exclusive or supersede others.
         * When a pattern is present, its exclusions are removed.
         */
        val EXCLUSIONS: Map<Pattern, Set<Pattern>> = mapOf(
            // All Chows and All Pongs are mutually exclusive
            ALL_CHOWS to setOf(ALL_PONGS),
            ALL_PONGS to setOf(ALL_CHOWS),

            // Pure Hand supersedes Half Flush
            PURE_HAND to setOf(HALF_FLUSH, MIXED_TERMINALS),

            // All Honors supersedes Half Flush and Mixed Terminals
            ALL_HONORS to setOf(HALF_FLUSH, MIXED_TERMINALS),

            // All Terminals supersedes Mixed Terminals (and is always all pongs)
            ALL_TERMINALS to setOf(ALL_PONGS, MIXED_TERMINALS),

            // Mixed Terminals is excluded by pure suit/honor hands
            MIXED_TERMINALS to setOf(PURE_HAND, ALL_HONORS, ALL_TERMINALS),

            // Big Three Dragons supersedes Little Three Dragons
            BIG_THREE_DRAGONS to setOf(LITTLE_THREE_DRAGONS),

            // Special hands
            THIRTEEN_ORPHANS to setOf(ALL_PONGS, ALL_CHOWS, HALF_FLUSH, PURE_HAND, CONCEALED_HAND),
            SEVEN_PAIRS to setOf(ALL_PONGS, ALL_CHOWS)
        )

        /**
         * Get patterns sorted by point value (highest first).
         */
        val BY_POINTS: List<Pattern> = entries.sortedByDescending { it.points }
    }
}
