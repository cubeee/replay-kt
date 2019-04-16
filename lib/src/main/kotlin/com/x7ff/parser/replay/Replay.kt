package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer
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
        fun File.parseReplayHeader() = parseHeader(this)
        fun Path.parseReplayHeader() = parseHeader(this)
        fun ByteArray.parseReplayHeader() = parseHeader(this)
        fun ByteBuffer.parseReplayHeader() = parseHeader(this)

        fun parseHeader(path: File) = parse(path.toPath())
        fun parseHeader(buffer: ByteArray) = parseHeader(BitBuffer(buffer))
        fun parseHeader(buffer: ByteBuffer) = parseHeader(BitBuffer(buffer))
        fun parseHeader(buffer: BitBuffer) = buffer.parseReplayHeader()
        fun parseHeader(path: Path): Header {
            if (Files.isDirectory(path)) {
                throw IllegalArgumentException("Input path is a directory, can only parse individual files")
            }
            return parseHeader(path.readBytes())
        }

        fun File.parseReplay(parseFrames: Boolean = true) = parse(this, parseFrames)
        fun Path.parseReplay(parseFrames: Boolean = true) = parse(this, parseFrames)
        fun BitBuffer.parseReplay(parseFrames: Boolean = true) = parse(this, parseFrames)
        fun ByteBuffer.parseReplay(parseFrames: Boolean = true) = parse(this, parseFrames)
        fun ByteArray.parseReplay(parseFrames: Boolean = true) = parse(this, parseFrames)

        fun parse(path: File, parseFrames: Boolean = true): Replay = parse(path.toPath(), parseFrames)
        fun parse(bytes: ByteArray, parseFrames: Boolean = true) = parse(BitBuffer(bytes), parseFrames)
        fun parse(bytes: ByteBuffer, parseFrames: Boolean = true) = parse(BitBuffer(bytes), parseFrames)
        fun parse(path: Path, parseFrames: Boolean = true): Replay {
            if (Files.isDirectory(path)) {
                throw IllegalArgumentException("Input path is a directory, can only parse individual files")
            }
            return parse(path.readBytes(), parseFrames)
        }

        fun parse(
            buffer: BitBuffer,
            parseFrames: Boolean = true,
            strictBufferSize: Boolean = false
        ): Replay {
            val header = buffer.parseReplayHeader()
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

            if (strictBufferSize && replayBuffer.hasRemainingBits()) {
                throw RuntimeException("Bits remaining in replay buffer: ${replayBuffer.remainingBits()}")
            }

            if (strictBufferSize && buffer.hasRemainingBits()) {
                throw RuntimeException("Bits remaining in main buffer: ${buffer.remainingBits()}")
            }

            val classAttributeMap = ClassAttributeMap.createClassAttributeMap(
                caches = caches,
                names = names,
                objectReferences = objectReferences
            )

            val frames = when (parseFrames) {
                true -> parseFrames(networkStream, header, objectReferences, classAttributeMap, header.properties)
                else -> listOf()
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

        private fun BitBuffer.parseReplayHeader(): Header {
            val headerLength = getUInt().toInt()
            val headerCrc = getUInt() // TODO: verify
            val headerBuffer = getFullBytes(headerLength)
            val header = headerBuffer.readHeader()
            if (headerBuffer.hasRemainingBits()) {
                throw RuntimeException("Bits remaining in header buffer: ${headerBuffer.remainingBits()}")
            }
            return header
        }

        fun BitBuffer.readReplayBuffer(): BitBuffer {
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
            while(frames.size < numFrames && networkStream.hasRemainingBits()) {
                val frame = networkStream.readFrame(
                    maxChannels, versions, existingReplications, objectReferences, classAttributeMap)
                frames.add(frame)
            }

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
