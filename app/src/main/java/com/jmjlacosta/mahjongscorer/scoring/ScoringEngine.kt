package com.jmjlacosta.mahjongscorer.scoring

import com.jmjlacosta.mahjongscorer.model.Hand
import com.jmjlacosta.mahjongscorer.model.HandParser
import com.jmjlacosta.mahjongscorer.model.Tile

/**
 * Main scoring engine for Beijing Mahjong.
 * Calculates the score for a winning hand based on patterns and context.
 */
object ScoringEngine {

    /**
     * Calculate the score for a winning hand.
     *
     * @param hand The winning hand with tiles and optionally parsed melds
     * @param context The win context (self-draw, concealed, etc.)
     * @return Score breakdown with total points
     */
    fun calculateScore(hand: Hand, context: WinContext): Score {
        // Ensure hand is valid
        if (!HandParser.isValidWinningHand(hand.tiles)) {
            return Score.ZERO
        }

        // Parse melds if not already done
        val handWithMelds = if (hand.melds == null) {
            val allMeldCombinations = HandParser.parseHand(hand.tiles)
            if (allMeldCombinations.isEmpty()) {
                return Score.ZERO
            }
            // Find the highest-scoring meld combination
            allMeldCombinations
                .map { melds -> hand.copy(melds = melds) }
                .maxByOrNull { h -> calculateScoreForHand(h, context).totalPoints }
                ?: return Score.ZERO
        } else {
            hand
        }

        return calculateScoreForHand(handWithMelds, context)
    }

    /**
     * Calculate score for a hand with parsed melds.
     */
    private fun calculateScoreForHand(hand: Hand, context: WinContext): Score {
        val patterns = PatternDetector.detectPatterns(hand, context)
        return Score.fromPatterns(patterns)
    }

    /**
     * Quick score calculation from tiles only (assumes concealed, not self-draw).
     */
    fun calculateScore(tiles: List<Tile>): Score {
        return calculateScore(Hand(tiles), WinContext.DEFAULT)
    }

    /**
     * Calculate score with self-draw bonus.
     */
    fun calculateScoreSelfDraw(tiles: List<Tile>): Score {
        return calculateScore(Hand(tiles), WinContext.SELF_DRAW)
    }

    /**
     * Get all possible scores for a hand (for hands with multiple valid interpretations).
     * Returns scores sorted highest to lowest.
     */
    fun getAllPossibleScores(hand: Hand, context: WinContext): List<Score> {
        if (!HandParser.isValidWinningHand(hand.tiles)) {
            return emptyList()
        }

        val allMeldCombinations = HandParser.parseHand(hand.tiles)
        if (allMeldCombinations.isEmpty()) {
            return emptyList()
        }

        return allMeldCombinations
            .map { melds -> hand.copy(melds = melds) }
            .map { h -> calculateScoreForHand(h, context) }
            .sortedByDescending { it.totalPoints }
            .distinctBy { it.totalPoints } // Remove duplicates with same score
    }
}
