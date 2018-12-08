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
            val platform = readPlatform()
            return readUniqueId(versions, platform)
        }

        fun BitBuffer.readPlatform(): Platform {
            val platformId = getByte().toInt()
            return when(platformId) {
                0 -> Platform.SPLIT_SCREEN
                1 -> Platform.STEAM
                2 -> Platform.PS4
                4 -> Platform.XBOX
                6 -> Platform.SWITCH
                7 -> Platform.PSYNET
                else -> {
                    println("Next 2048 bits from position ${position()} (${positionBits()}): ")
                    for (i in 0..2048) {
                        print(getBits(1))
                    }
                    println()
                    throw IllegalArgumentException("Unknown platform id: $platformId")
                }
            }
        }

        fun BitBuffer.readUniqueId(versions: Versions, platform: Platform): UniqueIdAttribute {
            val id = when (platform) {
                Platform.SPLIT_SCREEN -> getUInt(24)
                Platform.STEAM -> getLong()
                Platform.PS4 -> {
                    if (versions.patchVersion >= 1) {
                        getBits(40 * 8)
                    } else {
                        getBits(32 * 8) // always 1?
                    }
                }
                Platform.XBOX -> getLong()
                Platform.SWITCH -> getBits(32 * 8)
                Platform.PSYNET -> getBits(32 * 8)
            }
            val playerNumber = getByte()

            return UniqueIdAttribute(platform, id, playerNumber)
        }
    }
}