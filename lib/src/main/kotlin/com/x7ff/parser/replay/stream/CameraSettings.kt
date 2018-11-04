package com.x7ff.parser.replay.stream

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.Versions

data class CameraSettings(
    val fieldOfView: Float,
    val height: Float,
    val pitch: Float,
    val distance: Float,
    val stiffness: Float,
    val swivelSpeed: Float,
    val transitionSpeed: Float
) {
    companion object {
        fun BitBuffer.readCameraSettings(versions: Versions): CameraSettings {
            val fieldOfView = getFloat()
            val height = getFloat()
            val pitch = getFloat()
            val distance = getFloat()
            val stiffness = getFloat()
            val swivelSpeed = getFloat()
            val transitionSpeed = when {
                versions.engineVersion >= 868 && versions.licenseeVersion >= 20 -> getFloat()
                else -> 1.0f
            }
            return CameraSettings(fieldOfView, height, pitch, distance, stiffness, swivelSpeed, transitionSpeed)
        }
    }
}