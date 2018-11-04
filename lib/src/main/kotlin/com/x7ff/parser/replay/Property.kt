package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.Properties.readProperties

data class Property(
    val name: String,
    @Transient val type: String = "None",
    val value: Any
) {
    constructor(name: String): this(name, "", 0)

    companion object {
        fun BitBuffer.readProperty(): Property {
            val name = getFixedLengthString()
            if (name == PropertyKey.None.id) {
                return Property(name)
            }

            val type = getFixedLengthString()
            getInt() // length
            getInt()

            val value: Any = when (type) {
                "IntProperty" -> getInt()
                "StrProperty", "NameProperty" -> getFixedLengthString()
                "QWordProperty" -> getLong()
                "BoolProperty" -> getByte() == 1.toByte()
                "FloatProperty" -> getFloat()
                "ByteProperty" -> {
                    val propertyType = getFixedLengthString()
                    val propertyValue = getFixedLengthString()
                    KeyValuePair(propertyType, propertyValue)
                }
                "ArrayProperty" -> {
                    val len = getInt()
                    val propertiesList = mutableListOf<List<Property>>()
                    for (i in 0 until len) {
                        propertiesList.add(readProperties())
                    }
                    propertiesList.toList()
                }
                else -> throw RuntimeException("Unknown property type '$type'")
            }

            return Property(name, type, value)
        }
    }
}