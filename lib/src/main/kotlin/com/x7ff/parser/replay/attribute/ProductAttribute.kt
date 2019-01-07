package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.ObjectReference
import com.x7ff.parser.replay.Versions

data class ProductAttribute(
    val unknown: Boolean,
    val objectId: Int,
    val objectName: String,
    val value: Any
) {
    companion object {
        fun BitBuffer.readProductAttribute(
            versions: Versions,
            objectReferences: List<ObjectReference>
        ): ProductAttribute {
            val unknown = getBoolean()
            val objectId = getUInt().toInt()
            val objectName = objectReferences[objectId].name
            val value: Any = when(objectName) {
                "TAGame.ProductAttribute_Painted_TA" -> readPainted(versions)
                "TAGame.ProductAttribute_UserColor_TA" -> readColor(versions)
                "TAGame.ProductAttribute_TitleID_TA" -> getFixedLengthString()
                "TAGame.ProductAttribute_SpecialEdition_TA" -> getBits(31) // TODO: figure out structure
                else -> throw IllegalArgumentException("Unknown object name $objectName for id $objectId")
            }
            return ProductAttribute(unknown, objectId, objectName, value)
        }

        private fun BitBuffer.readPainted(versions: Versions): Int {
            return when {
                versions.gte(868, 18, 0) -> getUInt(31).toInt()
                else -> getUIntMax(14).toInt()
            }
        }

        private fun BitBuffer.readColor(versions: Versions): Any {
            if (versions.licenseeVersion >= 23) {
                return arrayOf(getByte(), getByte(), getByte(), getByte())
            }
            val hasValue = getBoolean()
            return when (hasValue) {
                true -> getUInt(31).toInt()
                else -> 0
            }
        }

    }
}