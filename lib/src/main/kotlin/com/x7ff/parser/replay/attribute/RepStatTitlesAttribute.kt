package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.attribute.ObjectTargetAttribute.Companion.readObjectTarget

data class RepStatTitlesAttribute(
    val unknown1: Boolean,
    val name: String,
    val value: Int,
    val objectTarget: ObjectTargetAttribute
) {
    companion object {
        fun BitBuffer.readRepStatTitles(): RepStatTitlesAttribute {
            val unknown1 = getBoolean()
            val name = getFixedLengthString()
            val objectTarget = readObjectTarget()
            val value = getInt()
            return RepStatTitlesAttribute(unknown1, name, value, objectTarget)
        }
    }
}