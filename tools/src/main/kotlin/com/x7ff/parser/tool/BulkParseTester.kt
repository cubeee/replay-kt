package com.x7ff.parser.tool

import com.x7ff.parser.executeAndMeasureTimeNanos
import com.x7ff.parser.replay.Replay
import com.x7ff.parser.replay.intPropertyOrZero
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.sql.Connection
import java.time.Duration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.absoluteValue
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

class FailFastException(msg: String? = null, cause: Throwable? = null): Exception(msg, cause)

object WorkingFiles : Table() {
    val file = varchar("file", length = 2048).primaryKey()
}

class BulkParseTester(
    private val path: File,
    private val batchWorkers: Int = DEFAULT_WORKER_COUNT,
    private val failFast: Boolean = false,
    private val reparseWorking: Boolean = false
) {

    private var brokenReplays = 0
    private var zeroGoalReplays = 0

    private val testedWorkingFiles = mutableListOf<String>()
    lateinit var executor: ExecutorService

    private var runtime = 0L

    fun testReplays() {
        println("Starting with $batchWorkers threads...")
        executor = Executors.newFixedThreadPool(batchWorkers)

        println("Connecting to db...")
        Database.connect("jdbc:sqlite:tester.sqlite", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        val workingFileNames: MutableSet<String> = mutableSetOf()
        if (!reparseWorking) {
            println("Reading working replay file names...")
            workingFileNames.addAll(readWorkingReplayFileNames())
            println("Loaded ${workingFileNames.size} working replay file names")
        }

        val (replayCount, totalElapsed) = executeAndMeasureTimeNanos {
            var replayCount = 1
            if (path.isDirectory) {
                println("Path is a directory...")
                val replays = path
                    .walkBottomUp()
                    .toList()
                    .asSequence()
                    .filter { file -> !file.isDirectory }
                    .filter { file -> (!reparseWorking && !workingFileNames.contains(file.nameWithoutExtension)) || reparseWorking }
                    .toList()
                replayCount = replays.size
                println("Replays to test: $replayCount")

                batchRun(replayCount, failFast, replays)
            } else {
                println("Patch is not a directory!")
                if (reparseWorking && !workingFileNames.contains(path.nameWithoutExtension)) {
                    parseReplay(path, failFast = failFast)
                    writeWorkingReplayFileNames()
                }
            }
            replayCount
        }

        val brokenPercent = if (brokenReplays == 0 || replayCount == 0) 0 else (brokenReplays * 100 / replayCount)
        val zeroGoalPercent = if (zeroGoalReplays == 0 || replayCount == 0) 0 else (zeroGoalReplays * 100 / replayCount)
        val avgTimePerReplay = if (replayCount == 0) -1 else totalElapsed.toMillis() / replayCount

        println()
        println("Finished processing $replayCount replay(s) in ${totalElapsed.nanosToDateTime()} " +
                "(${totalElapsed.toMillis()}ms, ${totalElapsed}ns, ${avgTimePerReplay}ms per replay on avg) on $batchWorkers thread(s)")
        println("$brokenReplays of those were broken")
        println("\t$brokenReplays failed to parse ($brokenPercent%)")
        println("\t$zeroGoalReplays had zero goals scored ($zeroGoalPercent%)")

        println("Writing working replay file names...")
        executor.shutdown()
    }

    private fun batchRun(replayCount: Int, failFast: Boolean, replays: List<File>) {
        if (replays.isEmpty()) {
            return
        }

        replays
            .asSequence()
            .map { file ->
                executor.submit {
                    val elapsed = measureTimeMillis {
                        try {
                            parseReplay(file, failFast = failFast)
                        } catch (e: FailFastException) {
                            executor.shutdownNow()
                            e.printStackTrace()
                        }
                    }
                    runtime += elapsed
                }
            }
            .toList()
            .forEachIndexed { idx, future ->
                val index = idx + 1
                future.get()

                if (index % 50 == 0) {
                    val perReplay = runtime / index
                    val timeLeft = (replayCount - index) * perReplay / batchWorkers
                    println("-----> $index/$replayCount processed [avg ${perReplay}ms per replay, est time left: ${timeLeft.millisToDateTime()}]")
                }
                if (index % 200 == 0 || index == replays.size - 1) {
                    writeWorkingReplayFileNames()
                }
            }

        executor.shutdown()
    }

    private fun parseReplay(file: File, failFast: Boolean = false) {
        try {
            val (replay, parseTime) = executeAndMeasureTimeNanos {
                Replay.parse(file)
            }

            replay?.let {
                replay.header.run {
                    val blueGoals: Int = properties.intPropertyOrZero("Team0Score")
                    val orangeGoals: Int = properties.intPropertyOrZero("Team1Score")

                    if (blueGoals == 0 && orangeGoals == 0) {
                        zeroGoalReplays++
                    } else {
                        testedWorkingFiles.add(file.nameWithoutExtension)
                    }
                    this
                }
            } ?: run {
                file.markFailed(failFast)
            }
        } catch (e: Exception) {
            file.markFailed(failFast, e)
        }
    }

    private fun File.markFailed(failFast: Boolean, cause: Throwable? = null) {
        brokenReplays++

        if (failFast) {
            throw FailFastException(this.path, cause)
        }
    }

    private fun readWorkingReplayFileNames(): List<String> {
        return transaction {
            SchemaUtils.createMissingTablesAndColumns(WorkingFiles)

            WorkingFiles.selectAll().map { q -> q[WorkingFiles.file] }.toList()
        }
    }

    private fun writeWorkingReplayFileNames() {
        val testedWorkingCount = testedWorkingFiles.size
        val iterator = testedWorkingFiles.listIterator()
        val newList = mutableListOf<String>()
        iterator.forEach {
            newList.add(it)
            iterator.remove()
        }
        println("Writing ${newList.size}/$testedWorkingCount working file names")

        transaction {
            WorkingFiles.batchInsert(newList, ignore = true) { name ->
                this[WorkingFiles.file] = name
            }
        }
    }

    private fun Long.toMillis() = this / 1_000_000
    private fun Long.toNanos() = this * 1_000_000
    private fun Long.millisToDateTime(): String {
        return toNanos().nanosToDateTime()
    }
    private fun Long.nanosToDateTime(): String {
        val duration = Duration.ofNanos(this)
        val absSeconds = duration.seconds.absoluteValue
        return "%02dh %02dm %02ds".format(absSeconds / 3600, (absSeconds % 3600) / 60, absSeconds % 60)
    }

    companion object {
        private val DEFAULT_WORKER_COUNT = Runtime.getRuntime().availableProcessors()
    }
}

fun main(args: Array<String>) {
    val failFast = args.contains("failFast")
    val reparseWorking = args.contains("reparseWorking")

    if (args.isEmpty()) {
        println("Usage: BulkParserTest [path]\n\nPath can be a directory or a single file")
        exitProcess(1)
    }

    val path = File(args.first())
    if (!path.exists()) {
        println("Path does not exist!")
        exitProcess(1)
    }
    println(path)

    val tester = BulkParseTester(
        path = path,
        reparseWorking = reparseWorking,
        failFast = failFast)
    tester.testReplays()
}
