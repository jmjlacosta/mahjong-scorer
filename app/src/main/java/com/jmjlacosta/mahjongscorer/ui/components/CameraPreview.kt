package com.jmjlacosta.mahjongscorer.ui.components

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * CameraX preview composable that displays the camera feed.
 *
 * @param cameraSelector Which camera to use (front or back)
 * @param isFlashEnabled Whether flash is enabled
 * @param imageCapture The ImageCapture use case for taking photos
 * @param modifier Modifier for the preview
 */
@Composable
fun CameraPreview(
    cameraSelector: CameraSelector,
    isFlashEnabled: Boolean,
    imageCapture: ImageCapture,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }

    LaunchedEffect(cameraSelector, isFlashEnabled) {
        val cameraProvider = context.getCameraProvider()

        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        imageCapture.flashMode = if (isFlashEnabled) {
            ImageCapture.FLASH_MODE_ON
        } else {
            ImageCapture.FLASH_MODE_OFF
        }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier.fillMaxSize()
    )
}

/**
 * Extension function to get CameraProvider asynchronously.
 */
suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { future ->
            future.addListener(
                { continuation.resume(future.get()) },
                ContextCompat.getMainExecutor(this)
            )
        }
    }

/**
 * Take a photo and save it to the cache directory.
 *
 * @param context Android context
 * @param imageCapture The ImageCapture use case
 * @param executor Executor for the callback
 * @param onImageCaptured Callback with the saved image URI
 * @param onError Callback for errors
 */
fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val photoFile = File(
        context.cacheDir,
        "mahjong_capture_${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onImageCaptured(Uri.fromFile(photoFile))
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}
