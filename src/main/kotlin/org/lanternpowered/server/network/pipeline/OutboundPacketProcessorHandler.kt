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

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPromise
import io.netty.handler.codec.EncoderException
import io.netty.util.ReferenceCountUtil
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.packet.UnknownPacket
import java.util.ArrayList

/**
 * Decoding is handled in [PacketCodecHandler], to avoid multiple times
 * receiving channel attributes and extra pipeline components.
 */
class OutboundPacketProcessorHandler(private val codecContext: CodecContext) : ChannelOutboundHandlerAdapter() {

    override fun write(ctx: ChannelHandlerContext, packet: Any, promise: ChannelPromise) {
        packet as Packet
        if (packet === UnknownPacket)
            return
        val protocol = this.codecContext.session.protocol
        val registration = protocol.outbound.typeByType(packet.javaClass)
                ?: throw EncoderException("Packet type (${packet.javaClass.name}) is not registered in" +
                        " state ${codecContext.session.protocolState.name}!")
        // There must be a registration
        val processors = registration.processors
        if (processors.isEmpty()) {
            // Just forward the message
            ctx.write(packet, promise)
        } else {
            val packets = ArrayList<Packet>()
            for (processor in processors) {
                // The processor should handle the output messages
                processor.process(this.codecContext, packet, packets)
            }
            // Only release the message its not being forwarded
            if (!packets.contains(packet))
                ReferenceCountUtil.release(packet)
            if (packets.isEmpty()) {
                // Cancel the promise since the message didn't get sent further into the pipeline
                promise.cancel(false)
            } else {
                val voidPromise = ctx.voidPromise()
                // Send all the messages, only send the last message with the promise
                val last = packets.lastIndex
                for (i in 0 until last)
                    ctx.write(packets[i], voidPromise)
                ctx.write(packets[last], promise)
            }
        }
    }
}
