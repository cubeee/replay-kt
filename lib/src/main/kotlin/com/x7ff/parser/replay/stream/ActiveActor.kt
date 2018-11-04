package com.x7ff.parser.replay.stream

import com.x7ff.parser.buffer.BitBuffer

data class ActiveActor (
    val active: Boolean,
    val actorId: Int
) {
    companion object {
        fun BitBuffer.readActiveActor(): ActiveActor {
            val active = getBits(1) != -1L
            val actorId = getInt()
            return ActiveActor(active, actorId)
        }
    }
}