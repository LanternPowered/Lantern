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
package org.lanternpowered.server.network.vanilla.message.processor.play;

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.ByteBufferAllocator;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;

import java.util.List;

public final class ProcessorPlayOutChannelPayload implements Processor<MessagePlayInOutChannelPayload> {

    @Override
    public void process(CodecContext context, MessagePlayInOutChannelPayload message, List<Message> output) throws CodecException {
        byte[] content = message.getContent().array();

        if (content.length < 16777136) {
            output.add(message);
        // Support the multi part messages of forge, but only if the client supports it
        } else {
            boolean enabled = context.getChannel().attr(Session.FML_MARKER).get();
            if (!enabled) {
                throw new CodecException("Payload may not be larger than 16777135 bytes.");
            }
            int parts = (int) Math.ceil(content.length / 16777135.0);
            if (parts > 255) {
                throw new CodecException("Payload may not be larger than -16797616 bytes.");
            }
            ByteBuffer preamble = context.byteBufAlloc().buffer();
            preamble.writeString(message.getChannel());
            preamble.writeByte((byte) parts);
            preamble.writeInteger(content.length);
            output.add(new MessagePlayInOutChannelPayload("FML|MP", preamble));

            int offset = 0;
            for (int x = 0; x < parts; x++) {
                int length = Math.min(16777136, content.length - offset + 1);
                byte[] tmp = new byte[length];
                tmp[0] = ((byte) (x & 0xff));
                System.arraycopy(content, offset, tmp, 1, tmp.length - 1);
                offset += tmp.length - 1;
                output.add(new MessagePlayInOutChannelPayload("FML|MP", ByteBufferAllocator.unpooled().wrappedBuffer(tmp)));
            }
        }
    }
}
