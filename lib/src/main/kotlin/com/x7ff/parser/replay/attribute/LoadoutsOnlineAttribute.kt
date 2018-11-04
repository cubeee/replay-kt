package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.ObjectReference
import com.x7ff.parser.replay.Versions
import com.x7ff.parser.replay.attribute.LoadoutOnlineAttribute.Companion.readLoadoutOnline

data class LoadoutsOnlineAttribute(
    val blue: LoadoutOnlineAttribute,
    val orange: LoadoutOnlineAttribute,
    val unknown1: Boolean,
    val unknown2: Boolean
) {
    companion object {
        fun BitBuffer.readLoadoutsOnline(
            versions: Versions,
            objectReferences: List<ObjectReference>
        ): LoadoutsOnlineAttribute {
            val blue = readLoadoutOnline(versions, objectReferences)
            val orange = readLoadoutOnline(versions, objectReferences)
            if (blue.productAttributes.size != orange.productAttributes.size) {
                throw IllegalStateException("LoadoutOnlineAttribute list counts must match")
            }
            val unknown1 = getBoolean()
            val unknown2 = getBoolean()
            return LoadoutsOnlineAttribute(blue, orange, unknown1, unknown2)
        }
    }
}