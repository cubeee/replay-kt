package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer

data class RotationQuaternion(
    val x: Float,
    val y: Float,
    val z: Float,
    val w: Float
): Vector {
    override fun x() = x
    override fun y() = y
    override fun z() = z

    enum class Component {
        X, Y, Z, W
    }

    companion object {
        const val numBits = 18
        const val maxQuaternionValue = 0.7071067811865475244f // 1/sqrt(2)

        fun BitBuffer.readRotationQuaternion(): RotationQuaternion {
            val x = getIntFromBits(2)
            val component = when(x) {
                0 -> Component.X
                1 -> Component.Y
                2 -> Component.Z
                3 -> Component.W
                else -> throw RuntimeException("Invalid component: $x")
            }

            // TODO: these values are wrong, needs fixing
            val a = getUIntFromBits(numBits).decompressComponent()
            val b = getUIntFromBits(numBits).decompressComponent()
            val c = getUIntFromBits(numBits).decompressComponent()
            val d = Math.sqrt(1.0 - (a * a) - (b * b) - (c * c)).toFloat()

            return when(component) {
                Component.X -> RotationQuaternion(d, a, b, c)
                Component.Y -> RotationQuaternion(a, d, b, c)
                Component.Z -> RotationQuaternion(a, b, d, c)
                Component.W -> RotationQuaternion(a, b, c, d)
            }
        }

        private fun Long.decompressComponent(): Float {
            val maxValue = (1 shl numBits) - 1
            val positiveRangedValue = this / maxValue
            val rangedValue = (positiveRangedValue - 0.50f) * 2.0f
            return rangedValue * maxQuaternionValue
        }

    }
}