package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.FixedPointVector3d.Companion.readFixedPointVector
import com.x7ff.parser.replay.RotationQuaternion.Companion.readRotationQuaternion
import com.x7ff.parser.replay.RotationVector.Companion.readRotationVector
import com.x7ff.parser.replay.Vector
import com.x7ff.parser.replay.Vector3d.Companion.readVector
import com.x7ff.parser.replay.Versions

data class RigidBodyStateAttribute(
    val sleeping: Boolean,
    val position: Vector,
    val rotation: Vector,
    val linearVelocity: Vector?,
    val angularVelocity: Vector?
) {
    companion object {
        fun BitBuffer.readRigidBodyState(versions: Versions): RigidBodyStateAttribute {
            val sleeping = getBoolean()
            val position = when {
                versions.gte(868, 22, 5) -> readFixedPointVector(versions)
                else -> readVector(versions)
            }
            val rotation = when {
                versions.gte(868, 22, 7) -> readRotationQuaternion()
                else -> readRotationVector()
            }
            val (linearVelocity, angularVelocity) = when {
                !sleeping -> Pair(readVector(versions), readVector(versions))
                else -> Pair(null, null)
            }
            return RigidBodyStateAttribute(
                sleeping,
                position,
                rotation,
                linearVelocity,
                angularVelocity
            )
        }
    }

    override fun toString(): String {
        //return "RigidBodyStateAttribute(\n\t\tsleeping=$sleeping, \n\t\tposition=$position, \n\t\trotation=$rotation, \n\t\tlinearVelocity=$linearVelocity, \n\t\tangularVelocity=$angularVelocity)"
        return "RigidBodyStateAttribute(sleeping=$sleeping, position=$position, rotation=$rotation, linearVelocity=$linearVelocity, angularVelocity=$angularVelocity)"
    }

}