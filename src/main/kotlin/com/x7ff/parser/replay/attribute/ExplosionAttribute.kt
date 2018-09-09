package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.Vector
import com.x7ff.parser.replay.Vector3d.Companion.readVector
import com.x7ff.parser.replay.Versions

data class ExplosionAttribute(
    val flag: Boolean,
    val actorId: Int,
    val location: Vector
) {
    companion object {
        fun BitBuffer.readExplosion(versions: Versions): ExplosionAttribute {
            return ExplosionAttribute(
                flag = getBoolean(),
                actorId = getInt(),
                location = readVector(versions)
            )
        }
    }
}