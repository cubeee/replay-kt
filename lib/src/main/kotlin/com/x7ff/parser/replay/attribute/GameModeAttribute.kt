package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.Versions

data class GameModeAttribute(
    val mode: Long
) {
    companion object {
        fun BitBuffer.readGameMode(versions: Versions): GameModeAttribute {
            val mode = when (versions.gte(868, 12, 0)) {
                true -> getByte().toLong()
                false -> getUIntMax(4)
            }
            return GameModeAttribute(mode)
        }
    }
}