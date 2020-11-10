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
package org.lanternpowered.server.network.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import io.netty.util.ReferenceCountUtil
import org.lanternpowered.api.util.collections.concurrentHashSetOf
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.LanternGame
import org.lanternpowered.server.network.buffer.LanternByteBuffer
import org.lanternpowered.server.network.packet.BulkPacket
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.packet.HandlerPacket
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.packet.UnknownPacket
import org.lanternpowered.server.network.protocol.Protocol
import org.lanternpowered.server.network.protocol.ProtocolState

class NettyPacketDecoder(private val codecContext: CodecContext) : MessageToMessageDecoder<ByteBuf>() {

    public override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, output: MutableList<Any>) {
        if (input.readableBytes() == 0)
            return

        val opcode = LanternByteBuffer.readVarInt(input)
        val state = this.codecContext.session.protocolState
        val protocol = state.protocol
        val registration = protocol.inbound.byOpcode<Packet>(opcode)
        if (registration == null) {
            if (warnedMissingOpcodes.add(opcode))
                LanternGame.logger.warn("Failed to find a message registration with opcode 0x${opcode.toString(10)} in state $state!")
            return
        }

        val decoder = registration.decoder
        if (decoder == null) {
            // Decoding isn't supported, just consume the bytes and return
            input.skipBytes(input.readableBytes())
            return
        }

        // Slice the buffer, the rest of the input is message content
        val content = LanternByteBuffer(input.slice())

        // Read the content of the message
        val packet = decoder.decode(this.codecContext, content)
        if (content.available() > 0) {
            LanternGame.logger.warn("Trailing bytes ${content.available()}b after decoding with packet decoder " +
                    "${decoder.javaClass.name} with opcode 0x${opcode.toString(10)} in state $state!\n$packet")
        }

        this.processDecoded(packet, output.uncheckedCast(), protocol, state, this.codecContext)
        // Release the packet that was processed if it's not queued for further processing
        if (!output.contains(packet))
            ReferenceCountUtil.release(packet)
    }

    private fun processDecoded(packet: Packet, output: MutableList<Packet>, protocol: Protocol, state: ProtocolState, context: CodecContext) {
        if (packet == UnknownPacket)
            return
        if (packet is BulkPacket) {
            for (packet1 in packet.packets)
                this.processDecoded(packet1, output, protocol, state, context)
            return
        }
        val typeRegistration = protocol.inbound.byType(packet.javaClass) ?: return
        val processors = typeRegistration.processors
        // Only process if there are processors found
        if (!processors.isEmpty()) {
            for (processor in processors) {
                // The processor should handle the output packets
                processor.process(context, packet, output)
            }
        } else {
            val handler = typeRegistration.handler
            if (handler != null) {
                // Add the packet to the output
                output.add(HandlerPacket(packet, handler))
            } else {
                output.add(packet)
            }
        }
    }

    companion object {
        private val warnedMissingOpcodes = concurrentHashSetOf<Int>()
    }
}
