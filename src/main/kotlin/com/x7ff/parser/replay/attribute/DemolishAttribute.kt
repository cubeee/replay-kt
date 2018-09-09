package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.Vector
import com.x7ff.parser.replay.Vector3d.Companion.readVector
import com.x7ff.parser.replay.Versions

data class DemolishAttribute(
    val attackerFlag: Boolean,
    val attackerActorId: Int,
    val victimFlag: Boolean,
    val victimActorId: Int,
    val attackerVelocity: Vector,
    val victimVelocity: Vector
) {
    companion object {
        fun BitBuffer.readDemolish(versions: Versions): DemolishAttribute {
            return DemolishAttribute(
                attackerFlag = getBoolean(),
                attackerActorId = getInt(),
                victimFlag = getBoolean(),
                victimActorId = getInt(),
                attackerVelocity = readVector(versions),
                victimVelocity = readVector(versions)
            )
        }
    }
}