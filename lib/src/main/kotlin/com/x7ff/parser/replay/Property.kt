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
            val length = getInt()

            if (name == "QWordProperty" && length == 0) {
                return Property(name)
            }

            getInt()

            val value: Any = when (type) {
                "IntProperty" -> getInt()
                "StrProperty", "NameProperty" -> getFixedLengthString()
                "QWordProperty" -> if (length == 0) getByte() else getLong()
                "BoolProperty" -> getByte() == 1.toByte()
                "FloatProperty" -> getFloat()
                "ByteProperty" -> {
                    when (val propertyType = getFixedLengthString()) {
                        "OnlinePlatform_Steam" -> {
                            KeyValuePair(propertyType, "")
                        }
                        "None" -> {
                            getByte()
                            KeyValuePair(propertyType, "")
                        }
                        else -> {
                            val propertyValue = getFixedLengthString()
                            KeyValuePair(propertyType, propertyValue)
                        }
                    }
                }
                "ArrayProperty" -> {
                    val len = getInt()
                    val propertiesList = mutableListOf<List<Property>>()
                    for (i in 0 until len) {
                        propertiesList.add(readProperties())
                    }
                    propertiesList.toList()
                }
                "StructProperty" -> {
                    getFixedLengthString() // Name
                    val propertiesList = mutableListOf<Property>()
                    while (hasRemaining()) {
                        val prop = readProperty()
                        if (prop.name == "None") {
                            break
                        }
                        propertiesList.add(prop)
                    }
                    propertiesList.toList()
                }
                else -> throw RuntimeException("Unknown property type '$type'")
            }

            return Property(name, type, value)
        }
    }
}
