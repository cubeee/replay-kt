package com.x7ff.parser

inline fun <R> executeAndMeasureTimeNanos(block: () -> R): Pair<R, Long> {
    val start = System.nanoTime()
    val result = block()
    return result to (System.nanoTime() - start)
}
