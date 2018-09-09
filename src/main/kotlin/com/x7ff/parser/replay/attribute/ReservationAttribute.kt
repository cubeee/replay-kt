package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.Platform
import com.x7ff.parser.replay.Versions
import com.x7ff.parser.replay.attribute.UniqueIdAttribute.Companion.readUniqueId

data class ReservationAttribute(
    val number: Int,
    val uniqueId: UniqueIdAttribute,
    val name: String?,
    val unknown3: Long
) {
    companion object {
        fun BitBuffer.readReservation(versions: Versions): ReservationAttribute {
            val number = getUIntFromBits(3).toInt()
            val uniqueId = readUniqueId(versions)
            val name = if (uniqueId.platform != Platform.SPLIT_SCREEN) getFixedLengthString() else null
            val unknown3 = when {
                versions.engineVersion < 868 || versions.licenseeVersion < 12 -> getBits(2)
                else -> getByte().toLong()
            }

            return ReservationAttribute(
                number = number,
                uniqueId = uniqueId,
                name = name,
                unknown3 = unknown3
            )
        }
    }
}