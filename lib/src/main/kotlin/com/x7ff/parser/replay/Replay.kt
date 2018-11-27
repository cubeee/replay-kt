package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.executeAndMeasureTimeNanos
import com.x7ff.parser.readBytes
import com.x7ff.parser.replay.ClassMapping.Companion.readClassMappings
import com.x7ff.parser.replay.ClassNetCache.Companion.calculateMaxPropertyIds
import com.x7ff.parser.replay.ClassNetCache.Companion.fixClassParents
import com.x7ff.parser.replay.ClassNetCache.Companion.readClassNetCaches
import com.x7ff.parser.replay.Header.Companion.readHeader
import com.x7ff.parser.replay.KeyFrame.Companion.readKeyFrames
import com.x7ff.parser.replay.Level.Companion.readLevels
import com.x7ff.parser.replay.Mark.Companion.readMarks
import com.x7ff.parser.replay.Message.Companion.readMessages
import com.x7ff.parser.replay.Name.Companion.readNames
import com.x7ff.parser.replay.ObjectReference.Companion.readObjectReferences
import com.x7ff.parser.replay.Package.Companion.readPackages
import com.x7ff.parser.replay.stream.Frame
import com.x7ff.parser.replay.stream.Frame.Companion.readFrame
import com.x7ff.parser.replay.stream.SpawnedReplication
import java.io.File
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path

data class Replay(
    val header: Header,
    val levels: List<Level>,
    val keyFrames: List<KeyFrame>,
    val messages: List<Message>,
    val marks: List<Mark>,
    val packages: List<Package>,
    val objectReferences: List<ObjectReference>,
    val names: List<Name>,
    val classMappings: List<ClassMapping>,
    val caches: List<ClassNetCache>,
    val frames: List<Frame>
) {

    override fun toString(): String {
        return "Replay(\n" +
                "\theader=$header\n" +
                "\tlevels=$levels\n" +
                "\tkeyFrames=$keyFrames\n" +
                "\tmessages=$messages\n" +
                "\tmarks=$marks\n" +
                "\tpackages=$packages\n" +
                "\tobjectReferences=$objectReferences\n" +
                "\tnames=$names\n" +
                "\tclassMappings=$classMappings\n" +
                "\tcaches=$caches)"
    }

    companion object {
        fun ByteBuffer.parseReplay() = parse(this)
        fun File.parseReplay() = parse(this)
        fun Path.parseReplay() = parse(this)

        fun parse(path: File): Replay? = parse(path.toPath())

        fun parse(path: Path): Replay? {
            if (Files.isDirectory(path)) {
                return null
            }
            return parse(path.readBytes())
        }

        fun parse(bytes: ByteBuffer): Replay? {
            val buffer = BitBuffer(bytes)
            val header = buffer.parseHeader()
            val replayBuffer = buffer.readReplayBuffer()
            val levels = replayBuffer.readLevels()
            val keyFrames = replayBuffer.readKeyFrames()
            val networkStream = replayBuffer.readNetworkStreamBuffer()
            val messages = replayBuffer.readMessages()
            val marks = replayBuffer.readMarks()
            val packages = replayBuffer.readPackages()
            val objectReferences = replayBuffer.readObjectReferences()
            val names = replayBuffer.readNames()
            val classMappings = replayBuffer.readClassMappings()
            val caches = replayBuffer
                .readClassNetCaches()
                .fixClassParents(objectReferences)
                .calculateMaxPropertyIds()

            if (replayBuffer.hasRemainingBits()) {
                throw RuntimeException("Bits remaining in replay buffer: ${replayBuffer.remainingBits()}")
            }

            if (buffer.hasRemainingBits()) {
                throw RuntimeException("Bits remaining in main buffer: ${buffer.remainingBits()}")
            }

            val classAttributeMap = ClassAttributeMap.createClassAttributeMap(
                caches = caches,
                names = names,
                objectReferences = objectReferences
            )

            val (frames, _/*elapsed*/) = executeAndMeasureTimeNanos {
                parseFrames(networkStream, header, objectReferences, classAttributeMap, header.properties)
            }

            return Replay(
                header = header,
                levels = levels,
                keyFrames = keyFrames,
                messages = messages,
                marks = marks,
                packages = packages,
                objectReferences = objectReferences,
                names = names,
                classMappings = classMappings,
                caches = caches,
                frames = frames
            )
        }

        private fun BitBuffer.parseHeader(): Header {
            val headerLength = getUInt().toInt()
            val headerCrc = getUInt() // TODO: verify
            val headerBuffer = getFullBytes(headerLength)
            val header = headerBuffer.readHeader()
            if (headerBuffer.hasRemainingBits()) {
                throw RuntimeException("Bits remaining in header buffer: ${headerBuffer.remainingBits()}")
            }
            return header
        }

        private fun BitBuffer.readReplayBuffer(): BitBuffer {
            val replayLength = getUInt().toInt()
            val replayCrc = getInt() // TODO: verify
            return getFullBytes(replayLength)
        }

        private fun BitBuffer.readNetworkStreamBuffer(): BitBuffer {
            val networkStreamLength = getUInt().toInt()
            return getFullBytes(networkStreamLength)
        }

        private fun parseFrames(
            networkStream: BitBuffer,
            header: Header,
            objectReferences: List<ObjectReference>,
            classAttributeMap: ClassAttributeMap,
            properties: PropertyList
        ): List<Frame> {
            val engineVersion = header.engineVersion
            val licenseeVersion = header.licenseeVersion
            val patchVersion = getPatchVersion(header.patchVersion, properties)
            val numFrames: Int = PropertyKey.NumFrames intOrZeroFrom header.properties
            val maxChannels: Int = PropertyKey.MaxChannels intOrZeroFrom header.properties
            val versions = Versions(engineVersion, licenseeVersion, patchVersion)

            val existingReplications = mutableMapOf<Long, SpawnedReplication>()

            val frames = mutableListOf<Frame>()
            do {
                val frame = networkStream.readFrame(
                    maxChannels, versions, existingReplications, objectReferences, classAttributeMap)
                frames.add(frame)
            } while(frames.size <= numFrames)

            return frames.toList()
        }

        private fun getPatchVersion(headerPatchVersion: Long, properties: PropertyList): Long {
            return when (headerPatchVersion) {
                0L -> {
                    val matchType = properties.property<String>("MatchType")
                    when (matchType) {
                        "Lan" -> -1
                        else -> 0
                    }
                }
                else -> headerPatchVersion
            }
        }

    }

}
