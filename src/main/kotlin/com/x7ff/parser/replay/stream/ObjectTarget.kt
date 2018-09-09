package com.x7ff.parser.replay.stream

import com.x7ff.parser.buffer.BitBuffer

data class ObjectTarget(
    val flag: Boolean,
    val objectIndex: Int
) {
    companion object {
        fun BitBuffer.readObjectTarget(): ObjectTarget {
            val flag = getBoolean()
            val objectIndex = getInt()
            return ObjectTarget(flag, objectIndex)
        }
    }
}