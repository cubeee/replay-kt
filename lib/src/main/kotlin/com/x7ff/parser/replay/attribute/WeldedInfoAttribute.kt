package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.Int8Vector
import com.x7ff.parser.replay.Int8Vector.Companion.readInt8Vector
import com.x7ff.parser.replay.Vector
import com.x7ff.parser.replay.Vector3d.Companion.readVector
import com.x7ff.parser.replay.Versions

data class WeldedInfoAttribute(
    val active: Boolean,
    val actorId: Int,
    val offset: Vector,
    val mass: Float,
    val rotation: Int8Vector
) {
    companion object {
        fun BitBuffer.readWeldedInfo(versions: Versions): WeldedInfoAttribute {
            return WeldedInfoAttribute(
                active = getBoolean(),
                actorId = getInt(),
                offset = readVector(versions),
                mass = getFloat(),
                rotation = readInt8Vector()
            )
        }
    }
}