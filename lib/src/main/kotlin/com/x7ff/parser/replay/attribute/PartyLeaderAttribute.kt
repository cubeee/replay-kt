package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.Platform
import com.x7ff.parser.replay.Versions
import com.x7ff.parser.replay.attribute.UniqueIdAttribute.Companion.readPlatform
import com.x7ff.parser.replay.attribute.UniqueIdAttribute.Companion.readUniqueId

data class PartyLeaderAttribute(
    val platform: Platform,
    val uniqueIdAttribute: UniqueIdAttribute?
) {
    companion object {
        fun BitBuffer.readPartyLeader(versions: Versions): PartyLeaderAttribute {
            val platform = readPlatform()
            val uniqueId: UniqueIdAttribute? = when(platform) {
                Platform.SPLIT_SCREEN -> null
                else -> readUniqueId(versions, platform)
            }
            return PartyLeaderAttribute(platform, uniqueId)
        }
    }
}