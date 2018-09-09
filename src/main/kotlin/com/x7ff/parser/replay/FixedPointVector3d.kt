package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.Vector3d.Companion.readVector

data class FixedPointVector3d(
    val x: Float,
    val y: Float,
    val z: Float
): Vector {
    override fun x() = x
    override fun y() = y
    override fun z() = z

    companion object {
        fun BitBuffer.readFixedPointVector(versions: Versions): FixedPointVector3d {
            val vector = readVector(versions)

            return FixedPointVector3d(
                x = vector.x / 100.0f,
                y = vector.y / 100.0f,
                z = vector.z / 100.0f)
        }
    }

}