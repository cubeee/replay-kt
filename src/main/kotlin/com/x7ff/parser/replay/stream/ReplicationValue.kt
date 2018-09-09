package com.x7ff.parser.replay.stream

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.ClassAttributeMap
import com.x7ff.parser.replay.ObjectReference
import com.x7ff.parser.replay.Versions
import com.x7ff.parser.replay.stream.SpawnedReplication.Companion.readSpawnedReplication
import com.x7ff.parser.replay.stream.UpdatedReplications.Companion.readUpdatedReplications

interface ReplicationValue {
    companion object {
        fun BitBuffer.readReplicationValue(
            actor: Pair<Long, Int>,
            versions: Versions,
            existingReplications: MutableMap<Long, SpawnedReplication>,
            objectReferences: List<ObjectReference>,
            classAttributeMap: ClassAttributeMap
        ): ReplicationValue {
            val isOpen = getBoolean()
            return if (isOpen) {
                val isNew = getBoolean()
                if (isNew) {
                    readSpawnedReplication(versions, objectReferences, classAttributeMap)
                } else {
                    readUpdatedReplications(versions, actor, existingReplications, objectReferences)
                }
            } else {
                DestroyedReplication()
            }
        }
    }
}