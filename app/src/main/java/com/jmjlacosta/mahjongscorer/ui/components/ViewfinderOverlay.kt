package com.jmjlacosta.mahjongscorer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Semi-transparent overlay with a cutout rectangle to guide hand positioning.
 * Shows a clear area where the user should position their 14 mahjong tiles.
 */
@Composable
fun ViewfinderOverlay(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Canvas with semi-transparent overlay and clear cutout
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(alpha = 0.99f) // Enable alpha compositing for blendMode
        ) {
            // Semi-transparent background
            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                size = size
            )

            // Calculate rectangle dimensions (landscape-oriented for tile row)
            val rectWidth = size.width * 0.85f
            val rectHeight = size.height * 0.25f
            val left = (size.width - rectWidth) / 2
            val top = (size.height - rectHeight) / 2

            // Clear rectangle in center (using BlendMode.Clear)
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(rectWidth, rectHeight),
                cornerRadius = CornerRadius(12.dp.toPx()),
                blendMode = BlendMode.Clear
            )

            // White border around the clear area
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(left, top),
                size = Size(rectWidth, rectHeight),
                cornerRadius = CornerRadius(12.dp.toPx()),
                style = Stroke(width = 3.dp.toPx())
            )

            // Corner markers for visual guidance
            val markerLength = 30.dp.toPx()
            val markerWidth = 4.dp.toPx()

            // Top-left corner
            drawLine(
                color = Color.White,
                start = Offset(left, top + markerLength),
                end = Offset(left, top),
                strokeWidth = markerWidth
            )
            drawLine(
                color = Color.White,
                start = Offset(left, top),
                end = Offset(left + markerLength, top),
                strokeWidth = markerWidth
            )

            // Top-right corner
            drawLine(
                color = Color.White,
                start = Offset(left + rectWidth, top),
                end = Offset(left + rectWidth - markerLength, top),
                strokeWidth = markerWidth
            )
            drawLine(
                color = Color.White,
                start = Offset(left + rectWidth, top),
                end = Offset(left + rectWidth, top + markerLength),
                strokeWidth = markerWidth
            )

            // Bottom-left corner
            drawLine(
                color = Color.White,
                start = Offset(left, top + rectHeight - markerLength),
                end = Offset(left, top + rectHeight),
                strokeWidth = markerWidth
            )
            drawLine(
                color = Color.White,
                start = Offset(left, top + rectHeight),
                end = Offset(left + markerLength, top + rectHeight),
                strokeWidth = markerWidth
            )

            // Bottom-right corner
            drawLine(
                color = Color.White,
                start = Offset(left + rectWidth, top + rectHeight - markerLength),
                end = Offset(left + rectWidth, top + rectHeight),
                strokeWidth = markerWidth
            )
            drawLine(
                color = Color.White,
                start = Offset(left + rectWidth, top + rectHeight),
                end = Offset(left + rectWidth - markerLength, top + rectHeight),
                strokeWidth = markerWidth
            )
        }

        // Helper text at top
        Text(
            text = "Position 14 tiles in frame",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp)
        )

        // Helper text at bottom
        Text(
            text = "Ensure good lighting and flat surface",
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 140.dp)
        )
    }
}
