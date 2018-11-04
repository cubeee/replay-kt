package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer

data class Level(
    val name: String
) {
    companion object {
        fun BitBuffer.readLevels(): List<Level> {
            val length = getInt()

            val levels = mutableListOf<Level>()
            for (i in 0 until length) {
                val name = getFixedLengthString()
                levels.add(Level(name))
            }
            return levels.toList()
        }
    }
}