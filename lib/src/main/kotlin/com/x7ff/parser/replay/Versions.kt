package com.x7ff.parser.replay

data class Versions(val engineVersion: Long, val licenseeVersion: Long, val patchVersion: Long) {

    operator fun compareTo(other: Versions)
            = compareValuesBy(this, other, { it.engineVersion }, { it.licenseeVersion }, { it.patchVersion })

}