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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.MessageRegistration;
import org.lanternpowered.server.network.message.NullMessage;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.protocol.Protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Decoding is handled in {@link MessageCodecHandler}, to avoid multiple times
 * receiving channel attributes and extra pipeline components.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MessageProcessorHandler extends ChannelOutboundHandlerAdapter {

    private final CodecContext codecContext;

    public MessageProcessorHandler(CodecContext codecContext) {
        this.codecContext = codecContext;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg0, ChannelPromise promise) {
        final Message msg = (Message) msg0;
        if (msg == NullMessage.INSTANCE) {
            return;
        }
        final Protocol protocol = this.codecContext.getSession().getProtocol();
        final MessageRegistration registration = protocol.outbound().findByMessageType(msg.getClass()).orElse(null);
        // There must be a registration
        if (registration == null) {
            throw new EncoderException(String.format("Message type (%s) is not registered in state %s!",
                    msg.getClass().getName(), this.codecContext.getSession().getProtocolState().name()));
        }

        final List<Processor> processors = registration.getProcessors();
        if (processors.isEmpty()) {
            // Just forward the message
            ctx.write(msg, promise);
        } else {
            final List<Object> messages = new ArrayList<>();
            for (Processor processor : processors) {
                // The processor should handle the output messages
                processor.process(this.codecContext, msg, messages);
            }
            // Only release the message its not being forwarded
            if (!messages.contains(msg)) {
                ReferenceCountUtil.release(msg);
            }
            if (messages.isEmpty()) {
                // Cancel the promise since the message didn't get sent further into the pipeline
                promise.cancel(false);
            } else {
                final ChannelPromise voidPromise = ctx.voidPromise();
                // Send all the messages, only send the last message with the promise
                final int last = messages.size() - 1;
                for (int i = 0; i < last; i++) {
                    ctx.write(messages.get(i), voidPromise);
                }
                ctx.write(messages.get(last), promise);
            }
        }
    }
}
