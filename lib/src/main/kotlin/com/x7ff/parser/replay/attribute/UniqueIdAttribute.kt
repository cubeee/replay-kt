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
            val platform = Platform.values()[getByte().toInt()]
            return readUniqueId(versions, platform)
        }

        fun BitBuffer.readUniqueId(versions: Versions, platform: Platform): UniqueIdAttribute {
            // TODO: implement rest of the platforms
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
                else -> throw IllegalArgumentException("Invalid platform type: $platform")
            }
            val playerNumber = getByte()

            return UniqueIdAttribute(platform, id, playerNumber)
        }
    }
}