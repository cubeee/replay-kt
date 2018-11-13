package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer

data class ClubColorsAttribute(
    val blueFlag: Boolean,
    val blueColor: Byte,
    val orangeFlag: Boolean,
    val orangeColor: Byte
) {
    companion object {
        fun BitBuffer.readClubColors(): ClubColorsAttribute {
            return ClubColorsAttribute(
                blueFlag = getBoolean(),
                blueColor = getByte(),
                orangeFlag = getBoolean(),
                orangeColor = getByte()
            )
        }
    }
}