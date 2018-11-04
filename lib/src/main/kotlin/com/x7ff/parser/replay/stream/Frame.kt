package com.x7ff.parser.replay.stream

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.ClassAttributeMap
import com.x7ff.parser.replay.ObjectReference
import com.x7ff.parser.replay.Versions
import com.x7ff.parser.replay.stream.Replication.Companion.readReplication

data class Frame(
    val position: Int,
    val time: Float,
    val delta: Float,
    val bitLength: Int,
    val replications: List<Replication>
) {
    companion object {
        fun BitBuffer.readFrame(
            maxChannels: Int,
            versions: Versions,
            existingReplications: MutableMap<Long, SpawnedReplication>,
            objectReferences: List<ObjectReference>,
            classAttributeMap: ClassAttributeMap
        ): Frame {
            val position = positionBits()
            val time = getFloat()
            val delta = getFloat()

            val replications = mutableListOf<Replication>()

            do {
                val hasReplication = getBoolean()
                if (!hasReplication) {
                    break
                }

                val replication = readReplication(
                    maxChannels, versions, existingReplications, objectReferences, classAttributeMap)

                val actorId = replication.actor.first
                if (replication.value is DestroyedReplication) {
                    existingReplications.remove(actorId)
                } else if (replication.value is SpawnedReplication && existingReplications[actorId] == null) {
                    existingReplications[actorId] = replication.value
                }
                replications.add(replication)
            } while(hasReplication)

            return Frame(position, time, delta, 0, replications.toList())
        }

    }
}