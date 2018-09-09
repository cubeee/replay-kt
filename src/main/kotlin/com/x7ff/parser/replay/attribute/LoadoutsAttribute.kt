package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.attribute.LoadoutAttribute.Companion.readLoadout

data class LoadoutsAttribute(
    val blueLoadout: LoadoutAttribute,
    val orangeLoadout: LoadoutAttribute
) {
    companion object {
        fun BitBuffer.readLoadouts(): LoadoutsAttribute {
            return LoadoutsAttribute(
                blueLoadout = readLoadout(),
                orangeLoadout = readLoadout()
            )
        }
    }
}