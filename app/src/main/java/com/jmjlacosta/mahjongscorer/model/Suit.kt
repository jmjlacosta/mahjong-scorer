package com.jmjlacosta.mahjongscorer.model

/**
 * Represents the suit (category) of a mahjong tile.
 * Beijing Mahjong uses 34 unique tiles across 5 suits.
 */
enum class Suit(
    val chinese: String,
    val english: String,
    val isNumbered: Boolean
) {
    /** Dots/Circles (筒子) - numbered 1-9 */
    DOTS("筒", "Dots", true),

    /** Bamboo/Sticks (条子) - numbered 1-9 */
    BAMBOO("条", "Bamboo", true),

    /** Characters/Ten-thousands (万子) - numbered 1-9 */
    CHARACTERS("万", "Characters", true),

    /** Wind tiles (风牌) - East, South, West, North */
    WIND("风", "Wind", false),

    /** Dragon tiles (箭牌) - Red, Green, White */
    DRAGON("箭", "Dragon", false);

    /** Check if this suit is an honor tile (winds or dragons) */
    val isHonor: Boolean get() = !isNumbered
}
