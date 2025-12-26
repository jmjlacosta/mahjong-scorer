package com.jmjlacosta.mahjongscorer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jmjlacosta.mahjongscorer.model.Tile
import com.jmjlacosta.mahjongscorer.scoring.Score
import com.jmjlacosta.mahjongscorer.scoring.WinContext
import com.jmjlacosta.mahjongscorer.ui.components.AdaptiveResultLayout
import com.jmjlacosta.mahjongscorer.ui.components.ShareButton
import com.jmjlacosta.mahjongscorer.ui.components.TileVisualization

/**
 * Result display screen showing the calculated score with breakdown.
 */
@Composable
fun ResultScreen(
    windowSizeClass: WindowSizeClass,
    tiles: List<Tile>,
    score: Score?,
    winContext: WinContext,
    onScanAnother: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (score == null) {
        // Shouldn't happen, but handle gracefully
        Column(modifier = modifier.padding(16.dp)) {
            Text("No score to display", style = MaterialTheme.typography.bodyLarge)
            OutlinedButton(onClick = onScanAnother) {
                Text("Go Back")
            }
        }
        return
    }

    AdaptiveResultLayout(
        windowSizeClass = windowSizeClass,
        modifier = modifier,
        tileVisualization = {
            TileVisualization(tiles = tiles)

            Spacer(modifier = Modifier.height(12.dp))

            // Win context indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (winContext.isSelfDraw) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Self Draw") }
                    )
                }
                if (winContext.isConcealed) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Concealed") }
                    )
                }
            }
        },
        scoreBreakdown = {
            // Total score - prominent display
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "${score.totalPoints}",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "points",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Score breakdown
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Score Breakdown",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    score.items.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.pattern.chinese,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = item.pattern.english,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "+${item.points}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (index < score.items.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }
        },
        actionButtons = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onScanAnother,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Scan Another")
                }
                ShareButton(
                    tiles = tiles,
                    score = score,
                    winContext = winContext,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    )
}
