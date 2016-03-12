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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.codec.serializer.Types;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTeams;
import org.lanternpowered.server.text.FormattingCodeTextSerializer;

import java.util.List;

public final class CodecPlayOutTeams implements Codec<MessagePlayOutTeams> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutTeams message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        context.write(buf, Types.STRING, message.getTeamName());
        if (message instanceof MessagePlayOutTeams.Remove) {
            buf.writeByte(1);
        } else if (message instanceof MessagePlayOutTeams.CreateOrUpdate) {
            buf.writeByte(message instanceof MessagePlayOutTeams.Create ? 0 : 2);
            MessagePlayOutTeams.CreateOrUpdate message1 = (MessagePlayOutTeams.CreateOrUpdate) message;
            context.write(buf, Types.STRING, message1.getDisplayName());
            context.write(buf, Types.STRING, message1.getPrefix());
            context.write(buf, Types.STRING, message1.getSuffix());
            int flags = 0;
            if (message1.getFriendlyFire()) {
                flags |= 0x01;
            }
            if (message1.getSeeFriendlyInvisibles()) {
                flags |= 0x02;
            }
            buf.writeByte(flags);
            context.write(buf, Types.STRING, message1.getNameTagVisibility().getId());
            context.write(buf, Types.STRING, message1.getCollisionRule().getId());
            buf.writeByte(FormattingCodeTextSerializer.FORMATS.get(message1.getColor()));
        } else {
            buf.writeByte(message instanceof MessagePlayOutTeams.AddPlayers ? 3 : 4);
            MessagePlayOutTeams.AddOrRemovePlayers message1 = (MessagePlayOutTeams.AddOrRemovePlayers) message;
            List<String> players = message1.getPlayers();
            context.writeVarInt(buf, players.size());
            for (String player : players) {
                context.write(buf, Types.STRING, player);
            }
        }
        return buf;
    }
}
