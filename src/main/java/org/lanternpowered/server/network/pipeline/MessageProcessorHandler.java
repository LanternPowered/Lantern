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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.MessageRegistration;
import org.lanternpowered.server.network.message.UnknownMessage;
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
        if (msg == UnknownMessage.INSTANCE) {
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
