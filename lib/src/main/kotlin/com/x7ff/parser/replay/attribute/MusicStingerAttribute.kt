package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer

data class MusicStingerAttribute(
    val flag: Boolean,
    val cue: Int,
    val trigger: Byte
) {
    companion object {
        fun BitBuffer.readMusicStinger(): MusicStingerAttribute {
            return MusicStingerAttribute(
                flag = getBoolean(),
                cue = getInt(),
                trigger = getByte()
            )
        }
    }
}