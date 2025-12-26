package com.jmjlacosta.mahjongscorer.viewmodel

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * ViewModel for managing camera state.
 */
class CameraViewModel : ViewModel() {
    /** Whether flash is enabled */
    var isFlashEnabled by mutableStateOf(false)
        private set

    /** URI of the last captured image */
    var capturedImageUri: Uri? by mutableStateOf(null)
        private set

    /** Current camera selector (front or back) */
    var cameraSelector by mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
        private set

    /**
     * Toggle flash on/off.
     */
    fun toggleFlash() {
        isFlashEnabled = !isFlashEnabled
    }

    /**
     * Switch between front and back camera.
     */
    fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    /**
     * Set the captured image URI.
     */
    fun setCapturedImage(uri: Uri) {
        capturedImageUri = uri
    }

    /**
     * Clear the captured image.
     */
    fun clearCapturedImage() {
        capturedImageUri = null
    }
}
