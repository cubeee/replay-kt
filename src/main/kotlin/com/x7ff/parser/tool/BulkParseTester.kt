package com.x7ff.parser.tool

import com.x7ff.parser.executeAndMeasureTimeNanos
import com.x7ff.parser.replay.Property
import com.x7ff.parser.replay.Replay
import com.x7ff.parser.replay.property
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration
import java.util.concurrent.Executors
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.system.exitProcess

class FailFastException(cause: Throwable? = null): Exception(cause)

class BulkParseTester(private val path: File) {

    private var processedReplays = 0
    private val brokenReplays = mutableListOf<File>()
    private val zeroGoalReplays = mutableListOf<File>()

    private val workingFileNames: MutableSet<String> = mutableSetOf()

    fun testReplays(
        batchWorkers: Int = DEFAULT_WORKER_COUNT,
        reparseWorking: Boolean = true,
        failFast: Boolean = false
    ) {
        if (!reparseWorking) {
            workingFileNames.addAll(readWorkingReplayFileNames())
            println("Loaded ${workingFileNames.size} working replay file names")
        }

        val (_, totalElapsed) = executeAndMeasureTimeNanos {
            if (path.isDirectory) {
                val replays = path
                    .walkBottomUp()
                    .toList()
                    .asSequence()
                    .filter { file -> !file.isDirectory }
                    .filter { file -> (!reparseWorking && !workingFileNames.contains(file.nameWithoutExtension)) || reparseWorking }
                    .toList()
                println("Replays: ${replays.size}")

                batchRun(batchWorkers, failFast, replays)
            } else {
                if (reparseWorking && !workingFileNames.contains(path.nameWithoutExtension)) {
                    parseReplay(path, failFast = failFast)
                }
            }
        }

        val brokenPercent = if (brokenReplays.isEmpty() || processedReplays == 0) 0 else (brokenReplays.size * 100 / processedReplays)
        val zeroGoalPercent = if (zeroGoalReplays.isEmpty() || processedReplays == 0) 0 else (zeroGoalReplays.size * 100 / processedReplays)
        val avgTimePerReplay = if (processedReplays == 0) -1 else totalElapsed.toMillis() / processedReplays

        println()
        println("Finished processing $processedReplays replay(s) in ${totalElapsed.toDateTime()} " +
                "(${totalElapsed.toMillis()}ms, ${totalElapsed}ns, ${avgTimePerReplay}ms per replay on avg) on $batchWorkers thread(s)")
        println("${brokenReplays.size} of those were broken")
        println("\t${brokenReplays.size} failed to parse ($brokenPercent%)")
        println("\t${zeroGoalReplays.size} had zero goals scored ($zeroGoalPercent%)")

        writeWorkingReplayFileNames()
    }

    private fun batchRun(batchWorkers: Int, failFast: Boolean, replays: List<File>) {
        if (replays.isEmpty()) {
            return
        }

        val lists = replays.chunked(ceil(replays.size / batchWorkers.toDouble()).toInt())
        val executor = Executors.newFixedThreadPool(batchWorkers)

/*        for (file in replays) {
            try {
                println(file)
                parseReplay(file, failFast = failFast)
            } catch (e: Exception) {
                if (failFast && e is FailFastException) {
                    e.printStackTrace()
                    break
                }
            }
        }
*/
        lists
            .asSequence()
            .map { files ->
                executor.submit {
                    files.onEach { file ->
                        if (executor.isShutdown) {
                            return@onEach
                        }
                        try {
                            parseReplay(file, failFast = failFast)
                        } catch (e: FailFastException) {
                            executor.shutdownNow()
                            e.printStackTrace()
                        }
                    }
                }
            }
            .toList()
            .onEach { future -> future.get() }

        executor.shutdown()
    }

    private fun parseReplay(file: File, failFast: Boolean = false) {
        try {
            val (replay, parseTime) = executeAndMeasureTimeNanos {
                Replay.parse(file)
            }

            replay?.let {
                with(replay.header) {
                    val id: String = properties.property("Id")
                    val recorder: String = properties.property("PlayerName")
                    val goals: List<Property> = properties.property("Goals")

                    //println("Parsed replay '$id' in ${parseTime.toMillis()}ms (${parseTime}ms)")
                    //println("\trecorder: $recorder")
                    //println("\tgoals: ${goals.size}")

                    if (goals.isEmpty()) {
                        zeroGoalReplays.add(file)
                    } else {
                        workingFileNames.add(file.nameWithoutExtension)
                    }
                }
            } ?: run {
                file.markFailed(failFast)
            }
        } catch (e: Exception) {
            file.markFailed(failFast, e)
        }
        processedReplays++
        if (processedReplays % 50 == 0) {
            println("-----> $processedReplays processed")
        }
    }

    private fun File.markFailed(failFast: Boolean, cause: Throwable? = null) {
        brokenReplays.add(this)

        if (failFast) {
            throw FailFastException(cause)
        }
    }

    private fun readWorkingReplayFileNames(): List<String> = Files.readAllLines(Paths.get("working.txt"))

    private fun writeWorkingReplayFileNames() {
        println("Writing ${workingFileNames.size} working file names to file...")
        File("working.txt").bufferedWriter().use { out ->
            workingFileNames.forEach { name -> out.write(name); out.newLine() }
        }
    }

    private fun Long.toMillis() = this / 1_000_000
    private fun Long.toDateTime(): String {
        val duration = Duration.ofNanos(this)
        val absSeconds = duration.seconds.absoluteValue
        return "%02dh %02dm %02ds".format(absSeconds / 3600, (absSeconds % 3600) / 60, absSeconds % 60)
    }

    companion object {
        private val DEFAULT_WORKER_COUNT = Runtime.getRuntime().availableProcessors()
    }
}

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Usage: BulkParserTest [path]\n\nPath can be a directory or a single file")
        exitProcess(1)
    }

    val path = File(args.first())
    if (!path.exists()) {
        println("Path does not exist!")
        exitProcess(1)
    }

    val tester = BulkParseTester(path)
    tester.testReplays(reparseWorking = true, failFast = true, batchWorkers = 8)
}
