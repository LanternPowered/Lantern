/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.EncoderException;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.codec.serializer.Types;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBossBar;

public final class CodecPlayOutBossBar implements Codec<MessagePlayOutBossBar> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutBossBar message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        context.write(buf, Types.UNIQUE_ID, message.getUniqueId());
        if (message instanceof MessagePlayOutBossBar.Add) {
            MessagePlayOutBossBar.Add message0 = (MessagePlayOutBossBar.Add) message;
            context.writeVarInt(buf, 0);
            context.write(buf, Types.LOCALIZED_TEXT, message0.getTitle());
            buf.writeFloat(message0.getHealth());
            context.writeVarInt(buf, message0.getColor().ordinal());
            context.writeVarInt(buf, message0.getDivision().ordinal());
            buf.writeByte(toFlags(message0.isDarkenSky(), message0.isEndMusic()));
        } else if (message instanceof MessagePlayOutBossBar.Remove) {
            context.writeVarInt(buf, 1);
        } else if (message instanceof MessagePlayOutBossBar.UpdateHealth) {
            context.writeVarInt(buf, 2);
            buf.writeFloat(((MessagePlayOutBossBar.UpdateHealth) message).getHealth());
        } else if (message instanceof MessagePlayOutBossBar.UpdateTitle) {
            context.writeVarInt(buf, 3);
            context.write(buf, Types.LOCALIZED_TEXT, ((MessagePlayOutBossBar.UpdateTitle) message).getTitle());
        } else if (message instanceof MessagePlayOutBossBar.UpdateStyle) {
            MessagePlayOutBossBar.UpdateStyle message0 = (MessagePlayOutBossBar.UpdateStyle) message;
            context.writeVarInt(buf, 4);
            context.writeVarInt(buf, message0.getColor().ordinal());
            context.writeVarInt(buf, message0.getDivision().ordinal());
        } else if (message instanceof MessagePlayOutBossBar.UpdateMisc) {
            MessagePlayOutBossBar.UpdateMisc message0 = (MessagePlayOutBossBar.UpdateMisc) message;
            context.writeVarInt(buf, 5);
            buf.writeByte(toFlags(message0.isDarkenSky(), message0.isEndMusic()));
        } else {
            throw new EncoderException("Unsupported message type: " + message.getClass().getName());
        }
        return buf;
    }

    private static byte toFlags(boolean darkenSky, boolean endMusic) {
        byte flags = 0;
        if (darkenSky) {
            flags |= 0x1;
        }
        if (endMusic) {
            flags |= 0x2;
        }
        return flags;
    }
}
