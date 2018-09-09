package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.Property.Companion.readProperty

sealed class PropertyKey(val id: String) {
    object None: PropertyKey("None")
    object MaxChannels: PropertyKey("MaxChannels")
    object NumFrames: PropertyKey("NumFrames")
}

object Properties {
    fun BitBuffer.readProperties(): PropertyList {
        val properties = mutableListOf<Property>()
        while (hasRemainingBits()) {
            val property = readProperty()
            if (property.name == PropertyKey.None.id) {
                break
            }
            properties.add(property)
        }
        return properties.toList()
    }
}

typealias PropertyList = List<Property>

inline fun <reified T> PropertyList.property(name: PropertyKey): T = first { prop -> prop.name == name.id }.value as T
inline infix fun <reified T> PropertyKey.from(properties: PropertyList): T = properties.property(this)