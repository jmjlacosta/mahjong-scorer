package com.jmjlacosta.mahjongscorer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
 * Hand builder component showing 14 tile slots.
 * Allows removing tiles by tapping on them.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HandBuilder(
    tiles: List<Tile>,
    onTileRemove: (Int) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hand (${tiles.size}/14)",
                    style = MaterialTheme.typography.titleMedium
                )
                OutlinedButton(
                    onClick = onClear,
                    enabled = tiles.isNotEmpty()
                ) {
                    Text("Clear")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tile slots
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Show selected tiles
                tiles.forEachIndexed { index, tile ->
                    TileChip(
                        tile = tile,
                        onClick = { onTileRemove(index) }
                    )
                }
                // Show empty slots
                repeat(14 - tiles.size) {
                    EmptySlot()
                }
            }

            if (tiles.size < 14) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap tiles below to add, tap hand tiles to remove",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * A tile chip in the hand area.
 */
@Composable
fun TileChip(
    tile: Tile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = getTileColor(tile)
    Surface(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick),
        color = color.copy(alpha = 0.3f),
        shape = RoundedCornerShape(4.dp),
        tonalElevation = 4.dp
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

/**
 * Empty slot placeholder in the hand.
 */
@Composable
fun EmptySlot(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(4.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp)
            )
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        // Empty
    }
}

/**
 * Win context toggles for self-draw and concealed hand.
 */
@Composable
fun WinContextToggles(
    isSelfDraw: Boolean,
    isConcealed: Boolean,
    onSelfDrawChange: (Boolean) -> Unit,
    onConcealedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Win Context",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isSelfDraw,
                    onCheckedChange = onSelfDrawChange
                )
                Text(
                    text = "Self Draw ()",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isConcealed,
                    onCheckedChange = onConcealedChange
                )
                Text(
                    text = "Concealed Hand ()",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * Calculate score button.
 */
@Composable
fun CalculateButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = if (enabled) "Calculate Score" else "Select 14 tiles",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

/**
 * Get the color for a tile based on its suit/type.
 */
private fun getTileColor(tile: Tile): Color = when (tile) {
    is Tile.NumberedTile -> when (tile.suit) {
        Suit.DOTS -> TileDots
        Suit.BAMBOO -> TileBamboo
        Suit.CHARACTERS -> TileCharacters
        else -> Color.Gray
    }
    is Tile.WindTile -> TileWind
    is Tile.DragonTile -> when (tile.dragon) {
        Tile.Dragon.RED -> TileDragonRed
        Tile.Dragon.GREEN -> TileDragonGreen
        Tile.Dragon.WHITE -> TileDragonWhite
    }
}
