package com.x7ff.parser

import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path

inline fun <R> executeAndMeasureTimeNanos(block: () -> R): Pair<R, Long> {
    val start = System.nanoTime()
    val result = block()
    return result to (System.nanoTime() - start)
}

fun Path.readBytes(): ByteBuffer {
    val channel = FileInputStream(this.toFile()).channel
    return channel.use {
        channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
    }
}
