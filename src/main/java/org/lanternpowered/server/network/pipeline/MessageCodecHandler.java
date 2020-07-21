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
package org.lanternpowered.server.network.pipeline;

import static org.lanternpowered.server.network.buffer.LanternByteBuffer.readVarInt;
import static org.lanternpowered.server.network.buffer.LanternByteBuffer.writeVarInt;

import com.google.common.collect.Sets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.LanternByteBuffer;
import org.lanternpowered.server.network.packet.BulkPacket;
import org.lanternpowered.server.network.packet.CodecRegistration;
import org.lanternpowered.server.network.packet.HandlerPacket;
import org.lanternpowered.server.network.packet.Packet;
import org.lanternpowered.server.network.packet.MessageRegistration;
import org.lanternpowered.server.network.packet.UnknownPacket;
import org.lanternpowered.server.network.packet.codec.Codec;
import org.lanternpowered.server.network.packet.codec.CodecContext;
import org.lanternpowered.server.network.packet.handler.Handler;
import org.lanternpowered.server.network.packet.processor.Processor;
import org.lanternpowered.server.network.protocol.Protocol;
import org.lanternpowered.server.network.protocol.ProtocolState;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings({ "rawtypes", "unchecked" })
public final class MessageCodecHandler extends MessageToMessageCodec<ByteBuf, Packet> {

    private final CodecContext codecContext;

    public MessageCodecHandler(CodecContext codecContext) {
        this.codecContext = codecContext;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, List<Object> output) {
        final Protocol protocol = this.codecContext.getSession().getProtocol();
        final MessageRegistration<Packet> registration = (MessageRegistration<Packet>) protocol.outbound()
                .findByMessageType(packet.getClass()).orElse(null);
        if (registration == null) {
            throw new EncoderException("Message type (" + packet.getClass().getName() + ") is not registered!");
        }

        final CodecRegistration codecRegistration = registration.getCodecRegistration().orElse(null);
        if (codecRegistration == null) {
            throw new EncoderException("Message type (" + packet.getClass().getName() + ") is not registered to allow encoding!");
        }

        final ByteBuf opcode = ctx.alloc().buffer();

        // Write the opcode of the message
        writeVarInt(opcode, codecRegistration.getOpcode());

        final Codec codec = codecRegistration.getCodec();
        final LanternByteBuffer content;
        try {
            content = (LanternByteBuffer) codec.encode(this.codecContext, packet);
        } finally {
            ReferenceCountUtil.release(packet);
        }

        // Add the buffer to the output
        output.add(Unpooled.wrappedBuffer(opcode, content.getDelegate()));
    }

    private static final Set<Integer> warnedMissingOpcodes = Sets.newConcurrentHashSet();

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> output) {
        if (input.readableBytes() == 0) {
            return;
        }

        final int opcode = readVarInt(input);

        final ProtocolState state = this.codecContext.getSession().getProtocolState();
        final Protocol protocol = state.getProtocol();
        final CodecRegistration registration = protocol.inbound().find(opcode).orElse(null);

        if (registration == null) {
            if (warnedMissingOpcodes.add(opcode)) {
                Lantern.getLogger().warn("Failed to find a message registration with opcode 0x{} in state {}!",
                        Integer.toHexString(opcode), state);
            }
            return;
        }

        // Slice the buffer, the rest of the input is message content
        final ByteBuffer content = new LanternByteBuffer(input.slice());

        // Read the content of the message
        final Packet packet = registration.getCodec().decode(this.codecContext, content);
        if (content.available() > 0) {
            Lantern.getLogger().warn("Trailing bytes {}b after decoding with message codec {} with opcode 0x{} in state {}!\n{}",
                    content.available(), registration.getCodec().getClass().getName(), Integer.toHexString(opcode), state, packet);
        }

        processMessage(packet, output, protocol, state, this.codecContext);
        if (!output.contains(packet)) {
            ReferenceCountUtil.release(packet);
        }
    }

    private void processMessage(Packet packet, List<Object> output, Protocol protocol, ProtocolState state, CodecContext context) {
        if (packet == UnknownPacket.INSTANCE) {
            return;
        }
        if (packet instanceof BulkPacket) {
            ((BulkPacket) packet).getPackets().forEach(message1 ->
                    processMessage(message1, output, protocol, state, context));
            return;
        }
        final MessageRegistration messageRegistration = protocol.inbound()
                .findByMessageType(packet.getClass()).orElseThrow(() -> new DecoderException(
                        "The returned message type is not attached to the used protocol state (" + state.toString() + ")!"));
        final List<Processor> processors = messageRegistration.getProcessors();
        // Only process if there are processors found
        if (!processors.isEmpty()) {
            for (Processor processor : processors) {
                // The processor should handle the output messages
                processor.process(context, packet, output);
            }
        } else {
            final Optional<Handler> optHandler = messageRegistration.getHandler();
            if (optHandler.isPresent()) {
                // Add the message to the output
                output.add(new HandlerPacket(packet, optHandler.get()));
            } else {
                output.add(packet);
            }
        }
    }
}
