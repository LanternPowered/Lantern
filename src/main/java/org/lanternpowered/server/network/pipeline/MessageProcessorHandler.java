/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.MessageRegistration;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.protocol.Protocol;

import java.util.List;

/**
 * Decoding is handled in {@link MessageCodecHandler}, to avoid multiple times
 * receiving channel attributes and extra pipeline components.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MessageProcessorHandler extends MessageToMessageEncoder<Message> {

    private final CodecContext codecContext;

    public MessageProcessorHandler(CodecContext codecContext) {
        this.codecContext = codecContext;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, List<Object> output) throws Exception {
        final Protocol protocol = this.codecContext.getSession().getProtocol();
        final MessageRegistration registration = protocol.outbound().findByMessageType(message.getClass()).orElse(null);

        if (registration == null) {
            throw new EncoderException("Message type (" + message.getClass().getName() +
                    ") is not registered in state " + this.codecContext.getSession().getProtocolState().name() + "!");
        }

        final List<Processor> processors = ((MessageRegistration) protocol.outbound()
                .findByMessageType(message.getClass()).get()).getProcessors();
        // Only process if there are processors found
        if (!processors.isEmpty()) {
            for (Processor processor : processors) {
                // The processor should handle the output messages
                processor.process(this.codecContext, message, output);
            }
        } else {
            // Add the message to the output
            output.add(message);
        }
    }
}
