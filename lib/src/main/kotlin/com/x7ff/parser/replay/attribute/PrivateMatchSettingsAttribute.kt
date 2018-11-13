package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer

data class PrivateMatchSettingsAttribute(
    val mutators: List<String>,
    val joinableBy: Long,
    val maxPlayers: Long,
    val gameName: String,
    val password: String,
    val flag: Boolean
) {
    companion object {
        fun BitBuffer.readPrivateMatchSettings(): PrivateMatchSettingsAttribute {
            val mutators = getFixedLengthString().split(",")
            val joinableBy = getUInt()
            val maxPlayers = getUInt()
            val gameName = getFixedLengthString()
            val password = getFixedLengthString()
            val flag = getBoolean()
            return PrivateMatchSettingsAttribute(
                mutators = mutators,
                joinableBy = joinableBy,
                maxPlayers = maxPlayers,
                gameName = gameName,
                password = password,
                flag = flag
            )
        }
    }
}