package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer

data class Package(
    val name: String
) {
    companion object {
        fun BitBuffer.readPackages(): List<Package> {
            val length = getInt()

            val packages = mutableListOf<Package>()
            for (i in 0 until length) {
                val name = getFixedLengthString()
                packages.add(Package(name))
            }
            return packages.toList()
        }
    }
}