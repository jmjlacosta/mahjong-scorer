package com.jmjlacosta.mahjongscorer.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.jmjlacosta.mahjongscorer.model.Tile
import com.jmjlacosta.mahjongscorer.scoring.Score
import com.jmjlacosta.mahjongscorer.scoring.WinContext
import java.io.File
import java.io.FileOutputStream

/**
 * Share button that opens a dialog to share the score as text or image.
 */
@Composable
fun ShareButton(
    tiles: List<Tile>,
    score: Score,
    winContext: WinContext,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Button(
        onClick = { showDialog = true },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Share")
    }

    if (showDialog) {
        ShareDialog(
            onDismiss = { showDialog = false },
            onShareText = {
                shareAsText(context, tiles, score, winContext)
                showDialog = false
            },
            onShareImage = {
                shareAsImage(context, tiles, score, winContext)
                showDialog = false
            }
        )
    }
}

/**
 * Dialog for choosing share format.
 */
@Composable
private fun ShareDialog(
    onDismiss: () -> Unit,
    onShareText: () -> Unit,
    onShareImage: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Share Score") },
        text = {
            Column {
                Text("How would you like to share your score?")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onShareText,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Text")
                    }
                    OutlinedButton(
                        onClick = onShareImage,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Image")
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Share the score as formatted text.
 */
private fun shareAsText(
    context: Context,
    tiles: List<Tile>,
    score: Score,
    winContext: WinContext
) {
    val text = buildString {
        appendLine("Mahjong Score: ${score.totalPoints} points")
        appendLine()

        // Hand tiles
        append("Hand: ")
        appendLine(tiles.joinToString(" ") { it.chineseName })
        appendLine()

        // Win context
        val contextParts = mutableListOf<String>()
        if (winContext.isSelfDraw) contextParts.add("Self Draw")
        if (winContext.isConcealed) contextParts.add("Concealed")
        if (contextParts.isNotEmpty()) {
            appendLine("Context: ${contextParts.joinToString(", ")}")
            appendLine()
        }

        // Score breakdown
        appendLine("Score Breakdown:")
        score.items.forEach { item ->
            appendLine("  ${item.pattern.chinese} (${item.pattern.english}): +${item.points}")
        }
        appendLine()
        appendLine("Total: ${score.totalPoints} points")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Score"))
}

/**
 * Share the score as an image.
 */
private fun shareAsImage(
    context: Context,
    tiles: List<Tile>,
    score: Score,
    winContext: WinContext
) {
    // Generate bitmap
    val bitmap = createScoreCardBitmap(tiles, score, winContext)

    // Save to cache directory
    val cacheDir = File(context.cacheDir, "shared_images")
    cacheDir.mkdirs()
    val imageFile = File(cacheDir, "mahjong_score.png")

    FileOutputStream(imageFile).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    // Get content URI via FileProvider
    val contentUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )

    // Share
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, contentUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share Score Image"))
}

/**
 * Create a bitmap representing the score card.
 */
private fun createScoreCardBitmap(
    tiles: List<Tile>,
    score: Score,
    winContext: WinContext
): Bitmap {
    val width = 600
    val lineHeight = 40
    val padding = 30
    val titleHeight = 60

    // Calculate height based on content
    val linesNeeded = 4 + score.items.size // Title, hand, context, breakdown header, items, total
    val height = titleHeight + (linesNeeded * lineHeight) + (padding * 2) + 40

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Background
    canvas.drawColor(AndroidColor.WHITE)

    // Paints
    val titlePaint = Paint().apply {
        color = AndroidColor.parseColor("#1976D2")
        textSize = 36f
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }
    val normalPaint = Paint().apply {
        color = AndroidColor.BLACK
        textSize = 24f
        isAntiAlias = true
    }
    val chinesePaint = Paint().apply {
        color = AndroidColor.parseColor("#388E3C")
        textSize = 28f
        isAntiAlias = true
    }
    val pointsPaint = Paint().apply {
        color = AndroidColor.parseColor("#D32F2F")
        textSize = 24f
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }

    var y = padding.toFloat()

    // Title
    canvas.drawText("Mahjong Score: ${score.totalPoints} pts", padding.toFloat(), y + 36f, titlePaint)
    y += titleHeight

    // Hand tiles
    val tilesText = "Hand: ${tiles.joinToString(" ") { it.chineseName }}"
    canvas.drawText(tilesText, padding.toFloat(), y, chinesePaint)
    y += lineHeight

    // Win context
    val contextParts = mutableListOf<String>()
    if (winContext.isSelfDraw) contextParts.add("Self Draw")
    if (winContext.isConcealed) contextParts.add("Concealed")
    if (contextParts.isNotEmpty()) {
        canvas.drawText("Context: ${contextParts.joinToString(", ")}", padding.toFloat(), y, normalPaint)
        y += lineHeight
    }

    // Divider line
    y += 10f
    val linePaint = Paint().apply {
        color = AndroidColor.LTGRAY
        strokeWidth = 2f
    }
    canvas.drawLine(padding.toFloat(), y, (width - padding).toFloat(), y, linePaint)
    y += 20f

    // Score breakdown
    score.items.forEach { item ->
        val itemText = "${item.pattern.chinese} (${item.pattern.english})"
        canvas.drawText(itemText, padding.toFloat(), y, normalPaint)
        canvas.drawText("+${item.points}", (width - padding - 60).toFloat(), y, pointsPaint)
        y += lineHeight
    }

    // Total
    y += 10f
    canvas.drawLine(padding.toFloat(), y, (width - padding).toFloat(), y, linePaint)
    y += 25f
    canvas.drawText("Total: ${score.totalPoints} points", padding.toFloat(), y, titlePaint)

    return bitmap
}
