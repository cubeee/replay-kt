package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer

data class RepStatTitlesAttribute(
    val unknown1: Boolean,
    val statName: String,
    val statValue: Long
) {
    companion object {
        fun BitBuffer.readRepStatTitles(): RepStatTitlesAttribute {
            val unknown1 = getBoolean()
            val statName = getFixedLengthString()
            val statValue = when(statName) {
                "StatCompound.EpicSaves" -> getBits(65)
                "StatTracking.FewestBallTouches" -> getBits(16)
                "StatStandard.Centers" -> getBits(65)
                "StatStandard.Saves" -> getBits(65)
                "StatStandard.Shots" -> getBits(65)
                "StatCompound.Goals_3_5" -> getBits(65)
                else -> throw IllegalArgumentException("Unknown stat name: $statName")
            }
            return RepStatTitlesAttribute(unknown1, statName, statValue)
        }
    }
}