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

    /** All Pongs - no chows, only pongs/kongs */
    ALL_PONGS("碰碰胡", "All Pongs", 2, "Hand contains only Pongs and Kongs, no Chows"),

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

    /** All Terminals - only 1s and 9s */
    ALL_TERMINALS("清老头", "All Terminals", 8, "Only terminal tiles (1s and 9s)"),

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
         * Pure Hand supersedes Half Flush (you can't have both).
         */
        val EXCLUSIONS: Map<Pattern, Set<Pattern>> = mapOf(
            PURE_HAND to setOf(HALF_FLUSH),
            ALL_HONORS to setOf(HALF_FLUSH),
            ALL_TERMINALS to setOf(ALL_PONGS), // All terminals is always all pongs
            THIRTEEN_ORPHANS to setOf(ALL_PONGS, HALF_FLUSH, PURE_HAND, CONCEALED_HAND),
            SEVEN_PAIRS to setOf(ALL_PONGS)
        )

        /**
         * Get patterns sorted by point value (highest first).
         */
        val BY_POINTS: List<Pattern> = entries.sortedByDescending { it.points }
    }
}
