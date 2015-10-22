/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTitle;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTitle.SetTimes;

import org.spongepowered.api.text.Text;

public final class CodecPlayOutTitle implements Codec<MessagePlayOutTitle> {

    private static final int SET_TITLE = 0;
    private static final int SET_SUBTITLE = 1;
    private static final int SET_TIMES = 2;
    private static final int CLEAR = 3;
    private static final int RESET = 4;

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutTitle message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        if (message instanceof MessagePlayOutTitle.Clear) {
            context.writeVarInt(buf, CLEAR);
        } else if (message instanceof MessagePlayOutTitle.Reset) {
            context.writeVarInt(buf, RESET);
        } else if (message instanceof MessagePlayOutTitle.SetTitle) {
            context.writeVarInt(buf, SET_TITLE);
            context.write(buf, Text.class, ((MessagePlayOutTitle.SetTitle) message).getTitle());
        } else if (message instanceof MessagePlayOutTitle.SetSubtitle) {
            context.writeVarInt(buf, SET_SUBTITLE);
            context.write(buf, Text.class, ((MessagePlayOutTitle.SetSubtitle) message).getTitle());
        } else {
            MessagePlayOutTitle.SetTimes message0 = (SetTimes) message;
            context.writeVarInt(buf, SET_TIMES);
            buf.writeInt(message0.getFadeIn());
            buf.writeInt(message0.getStay());
            buf.writeInt(message0.getFadeOut());
        }
        return buf;
    }

    @Override
    public MessagePlayOutTitle decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }
}
