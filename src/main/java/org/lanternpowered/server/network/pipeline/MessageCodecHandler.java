/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
import org.lanternpowered.server.network.message.BulkMessage;
import org.lanternpowered.server.network.message.CodecRegistration;
import org.lanternpowered.server.network.message.HandlerMessage;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.MessageRegistration;
import org.lanternpowered.server.network.message.NullMessage;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.protocol.Protocol;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageInOutKeepAlive;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldTime;

import java.util.List;
import java.util.Set;

@SuppressWarnings({ "rawtypes", "unchecked" })
public final class MessageCodecHandler extends MessageToMessageCodec<ByteBuf, Message> {

    private final CodecContext codecContext;

    public MessageCodecHandler(CodecContext codecContext) {
        this.codecContext = codecContext;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, List<Object> output) throws Exception {
        final Protocol protocol = this.codecContext.getSession().getProtocol();
        final MessageRegistration<Message> registration = (MessageRegistration<Message>) protocol.outbound()
                .findByMessageType(message.getClass()).orElse(null);

        if (registration == null) {
            throw new EncoderException("Message type (" + message.getClass().getName() + ") is not registered!");
        }
        CodecRegistration codecRegistration = registration.getCodecRegistration().orElse(null);
        if (codecRegistration == null) {
            throw new EncoderException("Message type (" + message.getClass().getName() + ") is not registered to allow encoding!");
        }
        /*
        if (message instanceof MessagePlayOutWorldTime ||
                message instanceof MessageInOutKeepAlive) {
        } else {
            System.out.println(message.getClass().getName());
        }
        */

        final ByteBuf opcode = ctx.alloc().buffer();

        // Write the opcode of the message
        writeVarInt(opcode, codecRegistration.getOpcode());

        final Codec codec = codecRegistration.getCodec();
        final ByteBuffer content;
        try {
            content = codec.encode(this.codecContext, message);
        } finally {
            ReferenceCountUtil.release(message);
        }

        // Add the buffer to the output
        output.add(Unpooled.wrappedBuffer(opcode, ((LanternByteBuffer) content).getDelegate()));
    }

    private static final Set<Integer> warnedMissingOpcodes = Sets.newConcurrentHashSet();

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> output) throws Exception {
        if (input.readableBytes() == 0) {
            return;
        }

        final int opcode = readVarInt(input);

        final ProtocolState state = this.codecContext.getSession().getProtocolState();
        final Protocol protocol = state.getProtocol();
        final CodecRegistration registration = protocol.inbound().find(opcode).orElse(null);

        if (registration == null) {
            if (warnedMissingOpcodes.add(opcode)) {
                Lantern.getLogger().warn("Failed to find a message registration with opcode 0x{} in state {}!", Integer.toHexString(opcode), state);
            }
            return;
        }

        // Copy the remaining content of the buffer to a new buffer used by the
        // message decoding
        final ByteBuffer content = this.codecContext.byteBufAlloc().buffer(input.readableBytes());
        input.readBytes(((LanternByteBuffer) content).getDelegate(), input.readableBytes());

        // Read the content of the message
        final Message message;
        try {
            message = registration.getCodec().decode(this.codecContext, content);
        } finally {
            content.release();
        }
        this.processMessage(message, output, protocol, state, this.codecContext);
    }

    private void processMessage(Message message, List<Object> output, Protocol protocol, ProtocolState state, CodecContext context) {
        if (message == NullMessage.INSTANCE) {
            return;
        }
        if (message instanceof BulkMessage) {
            ((BulkMessage) message).getMessages().forEach(message1 ->
                    processMessage(message1, output, protocol, state, context));
            return;
        }
        final MessageRegistration messageRegistration = (MessageRegistration) protocol.inbound()
                .findByMessageType(message.getClass()).orElseThrow(() -> new DecoderException(
                        "The returned message type is not attached to the used protocol state (" + state.toString() + ")!"));
        final List<Processor> processors = messageRegistration.getProcessors();
        // Only process if there are processors found
        if (!processors.isEmpty()) {
            for (Processor processor : processors) {
                // The processor should handle the output messages
                processor.process(context, message, output);
            }
        } else {
            messageRegistration.getHandler().ifPresent(handler -> {
                // Add the message to the output
                output.add(new HandlerMessage(message, (Handler) handler));
            });
        }
    }
}
