package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer

data class ClassMapping(
    val name: String,
    val streamId: Long
) {
    companion object {
        fun BitBuffer.readClassMappings(): List<ClassMapping> {
            val length = getUInt()

            val mappings = mutableListOf<ClassMapping>()
            for (i in 0 until length) {
                val name = getFixedLengthString()
                val streamId = getUInt()
                mappings.add(ClassMapping(name, streamId))
            }
            return mappings.toList()
        }

    }
}