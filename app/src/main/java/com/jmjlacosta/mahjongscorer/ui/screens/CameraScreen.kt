package com.jmjlacosta.mahjongscorer.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jmjlacosta.mahjongscorer.ui.components.AdaptiveCameraLayout
import com.jmjlacosta.mahjongscorer.ui.components.CameraControls
import com.jmjlacosta.mahjongscorer.ui.components.CameraControlsVertical
import com.jmjlacosta.mahjongscorer.ui.components.CameraPreview
import com.jmjlacosta.mahjongscorer.ui.components.ViewfinderOverlay
import com.jmjlacosta.mahjongscorer.ui.components.takePhoto
import com.jmjlacosta.mahjongscorer.viewmodel.CameraViewModel

/**
 * Camera preview screen for capturing mahjong hand photos.
 * Supports both compact (phone) and expanded (Pixel Fold) layouts.
 */
@Composable
fun CameraScreen(
    windowSizeClass: WindowSizeClass,
    onImageCaptured: (Uri) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: CameraViewModel = viewModel()

    // Permission state
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onImageCaptured(it) }
    }

    // ImageCapture use case
    val imageCapture = remember { ImageCapture.Builder().build() }

    // Request permission on launch if needed
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasPermission) {
        AdaptiveCameraLayout(
            windowSizeClass = windowSizeClass,
            modifier = modifier,
            cameraPreview = {
                Box(modifier = Modifier.fillMaxSize()) {
                    CameraPreview(
                        cameraSelector = viewModel.cameraSelector,
                        isFlashEnabled = viewModel.isFlashEnabled,
                        imageCapture = imageCapture,
                        modifier = Modifier.fillMaxSize()
                    )
                    ViewfinderOverlay(
                        modifier = Modifier.fillMaxSize()
                    )
                }
            },
            controls = {
                // Use vertical controls for expanded layout, horizontal for compact
                if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
                    CameraControlsVertical(
                        isFlashEnabled = viewModel.isFlashEnabled,
                        onFlashToggle = { viewModel.toggleFlash() },
                        onCapture = {
                            takePhoto(
                                context = context,
                                imageCapture = imageCapture,
                                executor = ContextCompat.getMainExecutor(context),
                                onImageCaptured = { uri ->
                                    viewModel.setCapturedImage(uri)
                                    onImageCaptured(uri)
                                },
                                onError = { exception ->
                                    exception.printStackTrace()
                                }
                            )
                        },
                        onGalleryPick = { galleryLauncher.launch("image/*") },
                        onSwitchCamera = { viewModel.switchCamera() },
                        onBack = onNavigateBack,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    CameraControls(
                        isFlashEnabled = viewModel.isFlashEnabled,
                        onFlashToggle = { viewModel.toggleFlash() },
                        onCapture = {
                            takePhoto(
                                context = context,
                                imageCapture = imageCapture,
                                executor = ContextCompat.getMainExecutor(context),
                                onImageCaptured = { uri ->
                                    viewModel.setCapturedImage(uri)
                                    onImageCaptured(uri)
                                },
                                onError = { exception ->
                                    exception.printStackTrace()
                                }
                            )
                        },
                        onGalleryPick = { galleryLauncher.launch("image/*") },
                        onSwitchCamera = { viewModel.switchCamera() },
                        onBack = onNavigateBack
                    )
                }
            }
        )
    } else {
        // Permission denied UI
        PermissionDeniedContent(
            onRequestPermission = {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onNavigateBack = onNavigateBack,
            modifier = modifier
        )
    }
}

/**
 * Content shown when camera permission is denied.
 */
@Composable
private fun PermissionDeniedContent(
    onRequestPermission: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Camera Permission Required",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "To capture mahjong tiles, please grant camera access.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onRequestPermission) {
            Text("Grant Permission")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onNavigateBack) {
            Text("Go Back")
        }
    }
}
