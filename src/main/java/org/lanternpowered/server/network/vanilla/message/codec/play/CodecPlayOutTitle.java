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
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTitle;

public final class CodecPlayOutTitle implements Codec<MessagePlayOutTitle> {

    private static final int SET_TITLE = 0;
    private static final int SET_SUBTITLE = 1;
    private static final int SET_ACTIONBAR_TITLE = 2;
    private static final int SET_TIMES = 3;
    private static final int CLEAR = 4;
    private static final int RESET = 5;

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutTitle message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        if (message instanceof MessagePlayOutTitle.Clear) {
            buf.writeVarInt(CLEAR);
        } else if (message instanceof MessagePlayOutTitle.Reset) {
            buf.writeVarInt(RESET);
        } else if (message instanceof MessagePlayOutTitle.SetTitle) {
            buf.writeVarInt(SET_TITLE);
            context.write(buf, ContextualValueTypes.TEXT, ((MessagePlayOutTitle.SetTitle) message).getTitle());
        } else if (message instanceof MessagePlayOutTitle.SetSubtitle) {
            buf.writeVarInt(SET_SUBTITLE);
            context.write(buf, ContextualValueTypes.TEXT, ((MessagePlayOutTitle.SetSubtitle) message).getTitle());
        } else if (message instanceof MessagePlayOutTitle.SetActionbarTitle) {
            buf.writeVarInt(SET_ACTIONBAR_TITLE);
            context.write(buf, ContextualValueTypes.TEXT, ((MessagePlayOutTitle.SetActionbarTitle) message).getTitle());
        } else {
            final MessagePlayOutTitle.SetTimes message0 = (MessagePlayOutTitle.SetTimes) message;
            buf.writeVarInt(SET_TIMES);
            buf.writeInteger(message0.getFadeIn());
            buf.writeInteger(message0.getStay());
            buf.writeInteger(message0.getFadeOut());
        }
        return buf;
    }
}
