package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.Int8Vector.Companion.readInt8Vector
import com.x7ff.parser.replay.Vector3d.Companion.readVector

data class Initialization (
    val location: Vector3d?,
    val rotation: Int8Vector?
) {
    companion object {
        fun BitBuffer.readInitialization(versions: Versions, hasLocation: Boolean, hasRotation: Boolean): Initialization {
            val location: Vector3d? = if (hasLocation) readVector(versions) else null
            val int8Vector: Int8Vector? = if (hasRotation) readInt8Vector() else null
            return Initialization(location, int8Vector)
        }
    }
}