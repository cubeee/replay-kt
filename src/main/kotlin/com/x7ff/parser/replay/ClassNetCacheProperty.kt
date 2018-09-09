package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer

data class ClassNetCacheProperty(
    val index: Int,
    val id: Int
) {
    companion object {
        fun BitBuffer.readClassNetCacheProperties(): Set<ClassNetCacheProperty> {
            val length = getInt()

            val properties = mutableListOf<ClassNetCacheProperty>()
            for (i in 0 until length) {
                val index = getInt()
                val id = getInt()
                properties.add(ClassNetCacheProperty(index, id))
            }
            return properties.toSet()
        }
    }
}