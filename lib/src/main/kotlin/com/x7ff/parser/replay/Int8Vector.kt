package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer

// TODO: better name
data class Int8Vector(val pitch: Byte, val yaw: Byte, val roll: Byte): Vector {
    override fun x() = 0f
    override fun y() = 0f
    override fun z() = 0f

    companion object {
        fun BitBuffer.readInt8Vector(): Int8Vector {
            return Int8Vector(
                pitch = readOptionalByte(),
                yaw = readOptionalByte(),
                roll = readOptionalByte()
            )
        }

        private fun BitBuffer.readOptionalByte(): Byte {
            return when(getBoolean()) {
                true -> getByte()
                false -> 0
            }
        }

    }
}