package com.x7ff.parser.replay

data class Versions(val engineVersion: Long, val licenseeVersion: Long, val patchVersion: Long) {

    fun gte(engine: Long, licensee: Long, patch: Long): Boolean
            = engineVersion >= engine && licenseeVersion >= licensee && patchVersion >= patch

}