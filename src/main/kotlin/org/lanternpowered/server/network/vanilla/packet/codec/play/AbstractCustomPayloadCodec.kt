/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.network.vanilla.packet.codec.play

import com.google.common.base.Joiner
import com.google.common.base.Splitter
import io.netty.handler.codec.CodecException
import io.netty.handler.codec.DecoderException
import io.netty.util.AttributeKey
import org.lanternpowered.server.LanternGame
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.buffer.UnpooledByteBufferAllocator
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.packet.UnknownPacket
import org.lanternpowered.server.network.packet.codec.Codec
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.ChannelPayloadPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.RegisterChannelsPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.UnregisterChannelsPacket
import java.nio.charset.StandardCharsets

abstract class AbstractCustomPayloadCodec : Codec<Packet> {

    override fun encode(ctx: CodecContext, packet: Packet): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        val channel: String
        val content: ByteBuffer
        when (packet) {
            is ChannelPayloadPacket -> {
                content = packet.content
                // Retain because content will be released when written, MessagePlayInOutChannelPayload
                // is ReferenceCounted so it will be cleaned up later.
                content.retain()
                channel = packet.channel
            }
            is RegisterChannelsPacket -> {
                content = encodeChannels(packet.channels)
                channel = "minecraft:register"
            }
            is UnregisterChannelsPacket -> {
                content = encodeChannels(packet.channels)
                channel = "minecraft:unregister"
            }
            else -> {
                val result = encode0(ctx, packet)
                channel = result.channel
                content = result.byteBuf
            }
        }
        try {
            buf.writeString(channel)
            buf.writeBytes(content)
        } finally {
            content.release()
        }
        return buf
    }

    override fun decode(ctx: CodecContext, buf: ByteBuffer): Packet {
        val channel = buf.readLimitedString(100)
        val length = buf.available()
        if (length > Short.MAX_VALUE) {
            throw DecoderException("CustomPayload messages may not be longer then " + Short.MAX_VALUE + " bytes")
        }
        val content = buf.slice()
        val packet = decode0(ctx, content, channel)
        if (content.available() > 0) {
            LanternGame.logger.warn("Trailing bytes {}b after decoding with custom payload message codec {} with channel {}!\n{}",
                    content.available(), javaClass.name, channel, packet)
        }
        // Skip all the bytes, we already processed them
        buf.readerIndex(buf.readerIndex() + buf.available())
        return packet
    }

    private fun decode0(context: CodecContext, content: ByteBuffer, channel: String): Packet {
        if ("minecraft:register" == channel) {
            val channels = decodeChannels(content)
            channels.removeIf { c: String -> c.startsWith("FML") }
            if (channels.isNotEmpty()) {
                return RegisterChannelsPacket(channels)
            }
        } else if ("minecraft:unregister" == channel) {
            val channels = decodeChannels(content)
            channels.removeIf { c: String -> c.startsWith("FML") }
            if (channels.isNotEmpty()) {
                return UnregisterChannelsPacket(channels)
            }
        } else if ("FML|MP" == channel) {
            val attribute = context.channel.attr(FML_MULTI_PART_MESSAGE)
            val message0 = attribute.get()
            if (message0 == null) {
                val channel0 = content.readString()
                val parts: Int = content.readByte().toInt() and 0xff
                val size = content.readInt()
                if (size <= 0) {
                    throw CodecException("Received FML MultiPart packet outside of valid length bounds, Received: $size")
                }
                attribute.set(MultiPartMessage(channel0, context.byteBufAlloc().buffer(size), parts))
            } else {
                val part: Int = content.readByte().toInt() and 0xff
                if (part != message0.index) {
                    throw CodecException("Received FML MultiPart packet out of order, Expected: " + message0.index + ", Got: " + part)
                }
                val len = content.available() - 1
                content.readBytes(message0.buffer, message0.offset, len)
                message0.offset += len
                message0.index++
                if (message0.index >= message0.parts) {
                    val packet = decode0(context, message0.channel, message0.buffer)
                    attribute.set(null)
                    return packet
                }
            }
        } else {
            return decode0(context, channel, content)
        }
        return UnknownPacket
    }

    protected abstract fun encode0(context: CodecContext, packet: Packet): MessageResult

    protected abstract fun decode0(context: CodecContext, channel: String, content: ByteBuffer): Packet

    protected class MessageResult(
            val channel: String,
            val byteBuf: ByteBuffer
    )

    private class MultiPartMessage internal constructor(
            val channel: String,
            val buffer: ByteBuffer,
            val parts: Int
    ) {
        var index = 0
        var offset = 0
    }

    companion object {

        private val FML_MULTI_PART_MESSAGE = AttributeKey.valueOf<MultiPartMessage>("fml-mpm")

        /**
         * Decodes the byte buffer into a set of channels.
         *
         * @param buffer the byte buffer
         * @return the channels
         */
        private fun decodeChannels(buffer: ByteBuffer): MutableSet<String> {
            val bytes = ByteArray(buffer.available())
            buffer.readBytes(bytes)
            return Splitter.on('\u0000').split(String(bytes, StandardCharsets.UTF_8)).toHashSet()
        }

        /**
         * Encodes the set of channels into a byte buffer.
         *
         * @param channels the channels
         * @return the byte buffer
         */
        private fun encodeChannels(channels: Set<String?>): ByteBuffer {
            return UnpooledByteBufferAllocator.wrappedBuffer(Joiner.on('\u0000').join(channels).toByteArray(StandardCharsets.UTF_8))
        }
    }
}
