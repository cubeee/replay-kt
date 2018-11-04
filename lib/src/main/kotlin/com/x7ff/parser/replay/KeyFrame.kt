package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer

data class KeyFrame(
    val timestamp: Float,
    val frame: Long,
    val framePosition: Long
) {
    companion object {
        fun BitBuffer.readKeyFrames(): List<KeyFrame> {
            val keyFrames = mutableListOf<KeyFrame>()
            val count = getInt()
            for (i in 0 until count) {
                val timestamp = getFloat()
                val frame = getUInt()
                val framePosition = getUInt()
                keyFrames.add(KeyFrame(timestamp, frame, framePosition))
            }
            return keyFrames
        }
    }
}