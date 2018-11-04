package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer

data class Mark(
    val type: String,
    val frame: Long
) {
    companion object {
        fun BitBuffer.readMarks(): List<Mark> {
            val length = getInt()

            val marks = mutableListOf<Mark>()
            for (i in 0 until length) {
                val type = getFixedLengthString()
                val frame = getUInt()
                marks.add(Mark(type, frame))
            }
            return marks.toList()
        }
    }
}