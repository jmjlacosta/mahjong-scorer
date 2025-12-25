package com.jmjlacosta.mahjongscorer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jmjlacosta.mahjongscorer.model.Suit
import com.jmjlacosta.mahjongscorer.model.Tile
import com.jmjlacosta.mahjongscorer.ui.theme.TileBamboo
import com.jmjlacosta.mahjongscorer.ui.theme.TileCharacters
import com.jmjlacosta.mahjongscorer.ui.theme.TileDots
import com.jmjlacosta.mahjongscorer.ui.theme.TileDragonGreen
import com.jmjlacosta.mahjongscorer.ui.theme.TileDragonRed
import com.jmjlacosta.mahjongscorer.ui.theme.TileDragonWhite
import com.jmjlacosta.mahjongscorer.ui.theme.TileWind

/**
 * Grid of all 34 tile types for selection.
 * Organized by suit with appropriate colors.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TileGrid(
    onTileClick: (Tile) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dots (1-9)
        TileRow(
            label = "Dots",
            labelChinese = "筒",
            tiles = Tile.tilesOfSuit(Suit.DOTS),
            color = TileDots,
            onTileClick = onTileClick
        )

        // Bamboo (1-9)
        TileRow(
            label = "Bamboo",
            labelChinese = "条",
            tiles = Tile.tilesOfSuit(Suit.BAMBOO),
            color = TileBamboo,
            onTileClick = onTileClick
        )

        // Characters (1-9)
        TileRow(
            label = "Characters",
            labelChinese = "万",
            tiles = Tile.tilesOfSuit(Suit.CHARACTERS),
            color = TileCharacters,
            onTileClick = onTileClick
        )

        // Winds
        TileRow(
            label = "Winds",
            labelChinese = "风",
            tiles = Tile.WIND_TILES,
            color = TileWind,
            onTileClick = onTileClick
        )

        // Dragons
        DragonRow(
            onTileClick = onTileClick
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TileRow(
    label: String,
    labelChinese: String,
    tiles: List<Tile>,
    color: Color,
    onTileClick: (Tile) -> Unit
) {
    Column {
        Text(
            text = "$labelChinese $label",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tiles.forEach { tile ->
                TileButton(
                    tile = tile,
                    color = color,
                    onClick = { onTileClick(tile) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DragonRow(
    onTileClick: (Tile) -> Unit
) {
    Column {
        Text(
            text = "Dragons",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Tile.DRAGON_TILES.forEach { tile ->
                val color = when (tile.dragon) {
                    Tile.Dragon.RED -> TileDragonRed
                    Tile.Dragon.GREEN -> TileDragonGreen
                    Tile.Dragon.WHITE -> TileDragonWhite
                }
                TileButton(
                    tile = tile,
                    color = color,
                    onClick = { onTileClick(tile) }
                )
            }
        }
    }
}

/**
 * A single tile button in the grid.
 */
@Composable
fun TileButton(
    tile: Tile,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick),
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp),
        tonalElevation = 2.dp
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = tile.chineseName,
                fontSize = 16.sp,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}
