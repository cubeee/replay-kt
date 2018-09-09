package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer

data class StatEventAttribute(
    val unknown: Boolean,
    val objectId: Int
) {
    companion object {
        fun BitBuffer.readStatEvent(): StatEventAttribute {
            return StatEventAttribute(
                unknown = getBoolean(),
                objectId = getInt()
            )
        }
    }
}