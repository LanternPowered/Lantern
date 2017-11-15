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
import org.lanternpowered.server.boss.LanternBossBarColor;
import org.lanternpowered.server.boss.LanternBossBarOverlay;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBossBar;

public final class CodecPlayOutBossBar implements Codec<MessagePlayOutBossBar> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutBossBar message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeUniqueId(message.getUniqueId());
        if (message instanceof MessagePlayOutBossBar.Add) {
            final MessagePlayOutBossBar.Add message0 = (MessagePlayOutBossBar.Add) message;
            buf.writeVarInt(0);
            context.write(buf, ContextualValueTypes.TEXT, message0.getTitle());
            buf.writeFloat(message0.getHealth());
            buf.writeVarInt(((LanternBossBarColor) message0.getColor()).getInternalId());
            buf.writeVarInt(((LanternBossBarOverlay) message0.getOverlay()).getInternalId());
            buf.writeByte(toFlags(message0.isDarkenSky(), message0.isEndMusic(), message0.shouldCreateFog()));
        } else if (message instanceof MessagePlayOutBossBar.Remove) {
            buf.writeVarInt(1);
        } else if (message instanceof MessagePlayOutBossBar.UpdatePercent) {
            buf.writeVarInt(2);
            buf.writeFloat(((MessagePlayOutBossBar.UpdatePercent) message).getPercent());
        } else if (message instanceof MessagePlayOutBossBar.UpdateTitle) {
            buf.writeVarInt(3);
            context.write(buf, ContextualValueTypes.TEXT, ((MessagePlayOutBossBar.UpdateTitle) message).getTitle());
        } else if (message instanceof MessagePlayOutBossBar.UpdateStyle) {
            final MessagePlayOutBossBar.UpdateStyle message0 = (MessagePlayOutBossBar.UpdateStyle) message;
            buf.writeVarInt(4);
            buf.writeVarInt(((LanternBossBarColor) message0.getColor()).getInternalId());
            buf.writeVarInt(((LanternBossBarOverlay) message0.getOverlay()).getInternalId());
        } else if (message instanceof MessagePlayOutBossBar.UpdateMisc) {
            final MessagePlayOutBossBar.UpdateMisc message0 = (MessagePlayOutBossBar.UpdateMisc) message;
            buf.writeVarInt(5);
            buf.writeByte(toFlags(message0.isDarkenSky(), message0.isEndMusic(), message0.shouldCreateFog()));
        } else {
            throw new EncoderException("Unsupported message type: " + message.getClass().getName());
        }
        return buf;
    }

    private static byte toFlags(boolean darkenSky, boolean endMusic, boolean createFog) {
        byte flags = 0;
        if (darkenSky) {
            flags |= 0x1;
        }
        if (endMusic) {
            flags |= 0x2;
        }
        if (createFog) {
            flags |= 0x4;
        }
        return flags;
    }
}
