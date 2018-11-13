package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.Vector
import com.x7ff.parser.replay.Vector3d.Companion.readVector
import com.x7ff.parser.replay.Versions

data class DamageStateAttribute(
    val unknown1: Byte,
    val unknown2: Boolean,
    val unknown3: Int,
    val unknown4: Vector,
    val unknown5: Boolean,
    val unknown6: Boolean
) {
    companion object {
        fun BitBuffer.readDamageState(versions: Versions): DamageStateAttribute {
            return DamageStateAttribute(
                unknown1 = getByte(),
                unknown2 = getBoolean(),
                unknown3 = getInt(),
                unknown4 = readVector(versions),
                unknown5 = getBoolean(),
                unknown6 = getBoolean()
            )
        }
    }
}