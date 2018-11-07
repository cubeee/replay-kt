package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.Platform
import com.x7ff.parser.replay.Versions

data class UniqueIdAttribute(
    val platform: Platform,
    val id: Number,
    val playerNumber: Byte
) {
    companion object {
        fun BitBuffer.readUniqueId(versions: Versions): UniqueIdAttribute {
            val platformId = getByte().toInt()
            val platform = when(platformId) {
                0 -> Platform.SPLIT_SCREEN
                1 -> Platform.STEAM
                2 -> Platform.PS4
                4 -> Platform.XBOX
                6 -> Platform.SWITCH
                else -> throw IllegalArgumentException("Unknown platform id: $platformId")
            }
            return readUniqueId(versions, platform)
        }

        fun BitBuffer.readUniqueId(versions: Versions, platform: Platform): UniqueIdAttribute {
            val id = when (platform) {
                Platform.SPLIT_SCREEN -> getUInt(24)
                Platform.STEAM -> getLong()
                Platform.PS4 -> {
                    if (versions.patchVersion >= 1) {
                        getBits(40 * 8)
                    } else {
                        getBits(32 * 8)
                    }
                }
                Platform.XBOX -> getLong()
                Platform.SWITCH -> getBits(32 * 8)
            }
            val playerNumber = getByte()

            return UniqueIdAttribute(platform, id, playerNumber)
        }
    }
}