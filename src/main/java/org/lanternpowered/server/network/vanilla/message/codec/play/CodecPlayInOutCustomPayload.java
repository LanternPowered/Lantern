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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.EncoderException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.UnknownMessage;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutBrand;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenBook;
import org.spongepowered.api.data.type.HandTypes;

public final class CodecPlayInOutCustomPayload extends AbstractCodecPlayInOutCustomPayload {

    @Override
    protected MessageResult encode0(CodecContext context, Message message) throws CodecException {
        if (message instanceof MessagePlayInOutBrand) {
            return new MessageResult("minecraft:brand", context.byteBufAlloc().buffer()
                    .writeString(((MessagePlayInOutBrand) message).getBrand()));
        } else if (message instanceof MessagePlayOutOpenBook) {
            final ByteBuffer buf = context.byteBufAlloc().buffer();
            buf.writeVarInt(((MessagePlayOutOpenBook) message).getHandType() == HandTypes.MAIN_HAND ? 0 : 1);
            return new MessageResult("minecraft:book_open", buf);
        }
        throw new EncoderException("Unsupported message type: " + message);
    }

    @Override
    protected Message decode0(CodecContext context, String channel, ByteBuffer content) throws CodecException {
        if ("minecraft:brand".equals(channel)) {
            return new MessagePlayInOutBrand(content.readString());
        } else if ("FML|HS".equals(channel)) {
            throw new CodecException("Received and unexpected message with channel: " + channel);
        } else if ("FML".equals(channel)) {
            // Fml channel
        } else if (channel.startsWith("FML")) {
            // A unknown/ignored fml channel
        } else {
            content.retain(); // Retain the content until we can process it
            return new MessagePlayInOutChannelPayload(channel, content);
        }
        return UnknownMessage.INSTANCE;
    }
}
