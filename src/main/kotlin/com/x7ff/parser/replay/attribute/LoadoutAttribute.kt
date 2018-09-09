package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer

data class LoadoutAttribute(
    val version: Byte,
    val bodyProductId: Long,
    val decalProductId: Long,
    val wheelProductId: Long,
    val boostProductId: Long,
    val antennaProductId: Long,
    val topperProductId: Long,
    val unknown1: Long,
    val unknown2: Long,
    val engineAudioProductId: Long,
    val trailProductId: Long,
    val goalExplosionProductId: Long,
    val bannerProductId: Long,
    val unknown3: Long,
    val unknown4: Long,
    val unknown5: Long,
    val unknown6: Long
) {
    companion object {
        fun BitBuffer.readLoadout(): LoadoutAttribute {
            val version = getByte()
            val bodyProductId = getUInt()
            val decalProductId = getUInt()
            val wheelProductId = getUInt()
            val boostProductId = getUInt()
            val antennaProductId = getUInt()
            val topperProductId = getUInt()
            val unknown1 = getUInt()
            val unknown2 = when {
                version > 10 -> getUInt()
                else -> 0L
            }
            val (engineAudioProductId, trailProductId, goalExplosionProductId) = when {
                version >= 16 -> Triple(getUInt(), getUInt(), getUInt())
                else -> Triple(0L, 0L, 0L)
            }
            val bannerProductId = when {
                version >= 17 -> getUInt()
                else -> 0L
            }
            val unknown3 = when {
                version >= 19 -> getUInt()
                else -> 0L
            }
            val (unknown4, unknown5, unknown6) = when {
                version >= 22 -> Triple(getUInt(), getUInt(), getUInt())
                else -> Triple(0L, 0L, 0L)
            }
            return LoadoutAttribute(
                version, bodyProductId, decalProductId, wheelProductId, boostProductId, antennaProductId,
                topperProductId, unknown1, unknown2, engineAudioProductId, trailProductId, goalExplosionProductId,
                bannerProductId, unknown3, unknown4, unknown5, unknown6
            )
        }
    }
}