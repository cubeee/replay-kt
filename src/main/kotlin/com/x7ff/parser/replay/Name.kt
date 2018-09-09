package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer

data class Name(
    val index: Long,
    val name: String
) {
    companion object {
        fun BitBuffer.readNames(): List<Name> {
            val length = getInt()

            val names = mutableListOf<Name>()
            for (i in 0 until length) {
                val name = getFixedLengthString()
                names.add(Name(i.toLong(), name))
            }
            return names.toList()
        }
    }
}