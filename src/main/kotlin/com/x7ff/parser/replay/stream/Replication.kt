package com.x7ff.parser.replay.stream

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.ClassAttributeMap
import com.x7ff.parser.replay.ObjectReference
import com.x7ff.parser.replay.Versions
import com.x7ff.parser.replay.stream.ReplicationValue.Companion.readReplicationValue

data class Replication(
    val actor: Pair<Long, Int>,
    val value: ReplicationValue
) {
    companion object {
        fun BitBuffer.readReplication(
            maxChannels: Int,
            versions: Versions,
            existingReplications: MutableMap<Long, SpawnedReplication>,
            objectReferences: List<ObjectReference>,
            classAttributeMap: ClassAttributeMap
        ): Replication {
            val actorId = getUIntMax(maxChannels)
            val actor = Pair(actorId, maxChannels)
            val value = readReplicationValue(actor, versions, existingReplications, objectReferences, classAttributeMap)
            return Replication(actor, value)
        }

    }
}