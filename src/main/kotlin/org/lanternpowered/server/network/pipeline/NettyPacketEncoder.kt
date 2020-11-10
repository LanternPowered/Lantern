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

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.EncoderException
import io.netty.handler.codec.MessageToMessageEncoder
import io.netty.util.ReferenceCountUtil
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.network.buffer.LanternByteBuffer
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.protocol.OutboundPacketRegistry

class NettyPacketEncoder(private val codecContext: CodecContext) : MessageToMessageEncoder<Packet>() {

    override fun encode(ctx: ChannelHandlerContext, packet: Packet, output: MutableList<Any>) {
        val protocol = this.codecContext.session.protocol
        val typeRegistration = protocol.outbound.typeByType(packet.javaClass)
                .uncheckedCast<OutboundPacketRegistry.TypeRegistration<Packet>?>()
                ?: throw EncoderException("Packet type (" + packet.javaClass.name + ") is not registered!")
        val opcodeRegistration = typeRegistration.opcodeRegistration
                ?: throw EncoderException("Packet type (" + packet.javaClass.name + ") is not registered to allow encoding!")
        val opcode = ctx.alloc().buffer()

        // Write the opcode of the message
        LanternByteBuffer.writeVarInt(opcode, opcodeRegistration.opcode)
        val encoder = opcodeRegistration.encoder!!
        val content = try {
            encoder.encode(this.codecContext, packet) as LanternByteBuffer
        } finally {
            ReferenceCountUtil.release(packet)
        }

        // Add the buffer to the output
        output.add(Unpooled.wrappedBuffer(opcode, content.delegate))
    }
}
