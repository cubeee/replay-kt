package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer

data class TitleAttribute(
    val unknown1: Boolean,
    val unknown2: Boolean,
    val unknown3: Int,
    val unknown4: Int,
    val unknown5: Int,
    val unknown6: Int,
    val unknown7: Int,
    val unknown8: Boolean
) {
    companion object {
        fun BitBuffer.readTitle(): TitleAttribute {
            return TitleAttribute(
                unknown1 = getBoolean(),
                unknown2 = getBoolean(),
                unknown3 = getInt(),
                unknown4 = getInt(),
                unknown5 = getInt(),
                unknown6 = getInt(),
                unknown7 = getInt(),
                unknown8 = getBoolean()
            )
        }
    }
}