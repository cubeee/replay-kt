package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.Properties.readProperties

data class Header(
    val engineVersion: Long,
    val licenseeVersion: Long,
    val patchVersion: Long,
    val replayClass: String,
    val properties: PropertyList
) {
    companion object {
        fun BitBuffer.readHeader(): Header {
            val engineVersion = getUInt()
            val licenseeVersion = getUInt()
            val patchVersion = when {
                engineVersion >= 868 && licenseeVersion >= 18 -> getUInt()
                else -> 0
            }
            val label = getFixedLengthString()
            val properties = readProperties()
            return Header(engineVersion, licenseeVersion, patchVersion, label, properties)
        }
    }

    override fun toString(): String {
        return "Header(\n" +
                "\t\tengineVersion=$engineVersion, \n" +
                "\t\tlicenseeVersion=$licenseeVersion, \n" +
                "\t\tpatchVersion=$patchVersion, \n" +
                "\t\treplayClass='$replayClass', \n" +
                "\t\tproperties=$properties)"
    }

}