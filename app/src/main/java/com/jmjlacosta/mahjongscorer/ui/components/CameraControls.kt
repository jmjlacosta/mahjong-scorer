package com.jmjlacosta.mahjongscorer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Camera controls bar with capture button, flash toggle, gallery picker, and camera switch.
 */
@Composable
fun CameraControls(
    isFlashEnabled: Boolean,
    onFlashToggle: () -> Unit,
    onCapture: () -> Unit,
    onGalleryPick: () -> Unit,
    onSwitchCamera: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main controls row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gallery picker
            IconButton(onClick = onGalleryPick) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "Pick from gallery",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Capture button (large circle)
            IconButton(
                onClick = onCapture,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(4.dp, Color.Black.copy(alpha = 0.3f), CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(3.dp, Color.Black, CircleShape)
                )
            }

            // Flash toggle
            IconButton(onClick = onFlashToggle) {
                Icon(
                    imageVector = if (isFlashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = if (isFlashEnabled) "Flash on" else "Flash off",
                    tint = if (isFlashEnabled) Color.Yellow else Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Secondary controls row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Capture mahjong tiles",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )

            // Switch camera
            IconButton(onClick = onSwitchCamera) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Switch camera",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

/**
 * Vertical camera controls for expanded layout (Pixel Fold inner screen).
 */
@Composable
fun CameraControlsVertical(
    isFlashEnabled: Boolean,
    onFlashToggle: () -> Unit,
    onCapture: () -> Unit,
    onGalleryPick: () -> Unit,
    onSwitchCamera: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.8f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Back button
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go back",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Flash toggle
        IconButton(onClick = onFlashToggle) {
            Icon(
                imageVector = if (isFlashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                contentDescription = if (isFlashEnabled) "Flash on" else "Flash off",
                tint = if (isFlashEnabled) Color.Yellow else Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Capture button (large circle)
        IconButton(
            onClick = onCapture,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(4.dp, Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(3.dp, Color.Black, CircleShape)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Gallery picker
        IconButton(onClick = onGalleryPick) {
            Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = "Pick from gallery",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Switch camera
        IconButton(onClick = onSwitchCamera) {
            Icon(
                imageVector = Icons.Default.Cameraswitch,
                contentDescription = "Switch camera",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
