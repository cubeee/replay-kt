package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer

data class Message(
    val frame: Long,
    val name: String,
    val value: String
) {
    companion object {
        fun BitBuffer.readMessages(): List<Message> {
            val messages = mutableListOf<Message>()
            val count = getInt()
            for (i in 0 until count) {
                val frame = getUInt()
                val name = getFixedLengthString()
                val value = getFixedLengthString()
                messages.add(Message(frame, name, value))
            }
            return messages
        }
    }
}