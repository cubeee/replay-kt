package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer

data class TeamPaint(
    val team: Byte,
    val primaryColor: Byte,
    val accentColor: Byte,
    val primaryFinish: Int,
    val accentFinish: Int
) {
    companion object {
        fun BitBuffer.readTeamPaint(): TeamPaint {
            return TeamPaint(
                team = getByte(),
                primaryColor = getByte(),
                accentColor = getByte(),
                primaryFinish = getUInt().toInt(),
                accentFinish = getUInt().toInt()
            )
        }
    }
}