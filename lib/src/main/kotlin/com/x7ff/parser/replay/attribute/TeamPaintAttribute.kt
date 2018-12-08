package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer

data class TeamPaintAttribute(
    val team: Byte,
    val primaryColor: Byte,
    val accentColor: Byte,
    val primaryFinish: Int,
    val accentFinish: Int
) {
    companion object {
        fun BitBuffer.readTeamPaint(): TeamPaintAttribute {
            return TeamPaintAttribute(
                team = getByte(),
                primaryColor = getByte(),
                accentColor = getByte(),
                primaryFinish = getUInt().toInt(),
                accentFinish = getUInt().toInt()
            )
        }
    }
}