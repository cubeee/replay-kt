package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer

data class ObjectTargetAttribute(
    val flag: Boolean,
    val objectIndex: Int
) {
    companion object {
        fun BitBuffer.readObjectTarget(): ObjectTargetAttribute {
            val flag = getBoolean()
            val objectIndex = getInt()
            return ObjectTargetAttribute(flag, objectIndex)
        }
    }
}