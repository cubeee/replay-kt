package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.ObjectReference
import com.x7ff.parser.replay.Versions

data class ProductAttribute(
    val unknown: Boolean,
    val objectId: Int,
    val objectName: String,
    val value: Int
) {
    companion object {
        fun BitBuffer.readProductAttribute(
            versions: Versions,
            objectReferences: List<ObjectReference>
        ): ProductAttribute {
            val unknown = getBoolean()
            val objectId = getUInt().toInt()
            val objectName = objectReferences[objectId].name
            val value = when(objectName) {
                "TAGame.ProductAttribute_Painted_TA" -> readPainted(versions)
                "TAGame.ProductAttribute_UserColor_TA" -> readColor()
                else -> throw IllegalArgumentException("Unknown object name $objectName for id $objectId")
            }
            return ProductAttribute(unknown, objectId, objectName, value)
        }

        private fun BitBuffer.readPainted(versions: Versions): Int {
            return when {
                versions.gte(868, 18, 0) -> getUInt(31).toInt()
                else -> getUInt(13).toInt()
            }
        }

        private fun BitBuffer.readColor(): Int {
            val hasValue = getBoolean()
            return when (hasValue) {
                true -> getUInt(31).toInt()
                else -> 0
            }
        }

    }
}