package com.jmjlacosta.mahjongscorer.scoring

/**
 * A single scoring item (pattern and its point value).
 */
data class ScoreItem(
    val pattern: Pattern,
    val points: Int = pattern.points
) {
    /** Chinese representation of this score item */
    val chineseText: String get() = "${pattern.chinese} +$points"

    /** English representation of this score item */
    val englishText: String get() = "${pattern.english} +$points"
}

/**
 * Complete score breakdown for a winning hand.
 */
data class Score(
    /** List of all scoring items */
    val items: List<ScoreItem>,

    /** Total points */
    val totalPoints: Int
) {
    /** Chinese summary of all scoring items */
    val chineseSummary: String
        get() = items.joinToString(" ") { it.pattern.chinese }

    /** English summary of all scoring items */
    val englishSummary: String
        get() = items.joinToString(", ") { it.pattern.english }

    /** Detailed breakdown in Chinese */
    val chineseBreakdown: String
        get() = items.joinToString("\n") { it.chineseText } +
                "\n总分: $totalPoints"

    /** Detailed breakdown in English */
    val englishBreakdown: String
        get() = items.joinToString("\n") { it.englishText } +
                "\nTotal: $totalPoints"

    companion object {
        /** Empty score (no win) */
        val ZERO = Score(emptyList(), 0)

        /**
         * Create a Score from a list of patterns.
         */
        fun fromPatterns(patterns: List<Pattern>): Score {
            val items = patterns.map { ScoreItem(it) }
            val total = items.sumOf { it.points }
            return Score(items, total)
        }
    }
}
