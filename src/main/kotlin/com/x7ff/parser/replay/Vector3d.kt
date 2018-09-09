package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer
import kotlin.math.pow

data class Vector3d(
    val size: Pair<Int, Int>,
    val bias: Int,
    val limit: Int,
    val x: Float,
    val y: Float,
    val z: Float
): Vector {
    override fun x() = x
    override fun y() = y
    override fun z() = z

    companion object {
        fun BitBuffer.readVector(versions: Versions): Vector3d {
            val size = when {
                versions.patchVersion >= 7 -> 22
                else -> 20
            }
            return readVector(size, this)
        }

        private fun readVector(sizeBits: Int, buffer: BitBuffer): Vector3d {
            val size = Pair(sizeBits, buffer.getIntMax(sizeBits))
            val bias = (2F.pow(size.second + 1)).toInt()
            val limit = (2F.pow(size.second + 2)).toInt()

            val x = buffer.getIntMax(limit) - bias
            val y = buffer.getIntMax(limit) - bias
            val z = buffer.getIntMax(limit) - bias

            return Vector3d(
                size = size,
                bias = bias,
                limit = limit,
                x = x.toFloat(),
                y = y.toFloat(),
                z = z.toFloat())
        }

    }
}