package com.x7ff.parser.replay.stream

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.classHasLocation
import com.x7ff.parser.classHasRotation
import com.x7ff.parser.rawObjectClass
import com.x7ff.parser.replay.*
import com.x7ff.parser.replay.Initialization.Companion.readInitialization

data class SpawnedReplication(
    val nameIndex: Long?,
    val name: String?,
    val objectId: Long,
    val objectName: String,
    val className: String,
    val initialization: Initialization,
    val classMap: ClassNetCache
): ReplicationValue {
    companion object {
        fun BitBuffer.readSpawnedReplication(
            versions: Versions,
            objectReferences: List<ObjectReference>,
            classAttributeMap: ClassAttributeMap
        ): SpawnedReplication {
            var nameIndex: Long? = null
            val name: String? = when {
                versions.gte(868, 14, 0) -> {
                    nameIndex = getUInt()
                    classAttributeMap.getName(nameIndex)
                }
                else -> null
            }

            getBoolean() // flag

            val objectId = getUInt()
            val objectName = objectReferences[objectId.toInt()].name
            val className = objectName.rawObjectClass()
            val classMap = classAttributeMap.getClassNetCacheByName(className)!!

            val hasLocation = className.classHasLocation()
            val hasRotation = className.classHasRotation()

            return SpawnedReplication(
                nameIndex = nameIndex,
                name = name,
                objectId = objectId,
                objectName = objectName,
                className = className,
                initialization = readInitialization(versions, hasLocation, hasRotation),
                classMap = classMap
            )
        }
    }
}