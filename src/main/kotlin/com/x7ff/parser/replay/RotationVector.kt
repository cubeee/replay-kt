package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer

data class RotationVector(
    val x: Int,
    val y: Int,
    val z: Int
): Vector {
    override fun x() = x.toFloat()
    override fun y() = y.toFloat()
    override fun z() = z.toFloat()

    companion object {
        private const val limit = 65536

        fun BitBuffer.readRotationVector(): RotationVector {
            return RotationVector(
                x = getIntMax(limit),
                y = getIntMax(limit),
                z = getIntMax(limit)
            )
        }
    }
}