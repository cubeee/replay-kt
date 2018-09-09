package com.x7ff.parser.replay.stream

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.ObjectReference
import com.x7ff.parser.replay.Versions
import com.x7ff.parser.replay.stream.UpdatedReplication.Companion.readUpdatedReplication

data class UpdatedReplications(val updates: List<UpdatedReplication>): ReplicationValue {
    companion object {
        fun BitBuffer.readUpdatedReplications(
            versions: Versions,
            actor: Pair<Long, Int>,
            existingReplications: MutableMap<Long, SpawnedReplication>,
            objectReferences: List<ObjectReference>
        ): UpdatedReplications {
            val previousReplication = existingReplications[actor.first]!!

            val updates = mutableListOf<UpdatedReplication>()
            while(hasUpdate()) {
                val update = readUpdatedReplication(versions, previousReplication, objectReferences)
                updates.add(update)
            }
            return UpdatedReplications(updates)
        }

        private fun BitBuffer.hasUpdate() = getBoolean()

    }
}