package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.Vector
import com.x7ff.parser.replay.Vector3d.Companion.readVector
import com.x7ff.parser.replay.Versions

data class AppliedDamageAttribute(
    val unknown1: Byte,
    val position: Vector,
    val unknown3: Int,
    val unknown4: Int
) {
    companion object {
        fun BitBuffer.readAppliedDamage(versions: Versions): AppliedDamageAttribute {
            return AppliedDamageAttribute(
                unknown1 = getByte(),
                position = readVector(versions),
                unknown3 = getInt(),
                unknown4 = getInt()
            )
        }
    }
}