package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer

data class ObjectReference(
    val index: Int,
    val name: String
) {
    companion object {
        fun BitBuffer.readObjectReferences(): List<ObjectReference> {
            val length = getInt()

            val references = mutableListOf<ObjectReference>()
            for (i in 0 until length) {
                val name = getFixedLengthString()
                references.add(ObjectReference(i, name))
            }
            return references.toList()
        }
    }
}