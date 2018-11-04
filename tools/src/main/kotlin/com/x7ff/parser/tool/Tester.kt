package com.x7ff.parser.tool

import com.x7ff.parser.replay.Replay
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val path = Paths.get(args[0])
    val times = 10
    for (i in 0 until times) {
        parseReplay(i, path)
    }
}

private fun parseReplay(i: Int, path: Path) {
    val parseTime = measureTimeMillis {
        Replay.parse(path)
    }
    println("#$i: parsed replay in ${parseTime}ms")
}
