package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer

data class PickupAttribute(
    val instigator: Boolean,
    val instigatorId: Int,
    val pickedUp: Boolean
) {
    companion object {
        fun BitBuffer.readPickupData(): PickupAttribute {
            val instigator = getBoolean()
            val instigatorId = getInt()
            val pickedUp = getBoolean()
            return PickupAttribute(instigator, instigatorId, pickedUp)
        }
    }
}