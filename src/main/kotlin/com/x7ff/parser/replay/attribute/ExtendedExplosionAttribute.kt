package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.Versions
import com.x7ff.parser.replay.attribute.ExplosionAttribute.Companion.readExplosion
import com.x7ff.parser.replay.stream.ActiveActor
import com.x7ff.parser.replay.stream.ActiveActor.Companion.readActiveActor

data class ExtendedExplosionAttribute(
    val explosion: ExplosionAttribute,
    val unknown: ActiveActor
) {
    companion object {
        fun BitBuffer.readExtendedExplosion(versions: Versions): ExtendedExplosionAttribute {
            return ExtendedExplosionAttribute(
                explosion = readExplosion(versions),
                unknown = readActiveActor()
            )
        }
    }
}