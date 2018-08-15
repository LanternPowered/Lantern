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
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTeams;
import org.lanternpowered.server.text.LanternFormattingCodeTextSerializer;
import org.lanternpowered.server.text.LanternTexts;
import org.lanternpowered.server.text.translation.TranslationContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

public final class CodecPlayOutTeams implements Codec<MessagePlayOutTeams> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutTeams message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeString(message.getTeamName());
        if (message instanceof MessagePlayOutTeams.CreateOrUpdate) {
            buf.writeByte((byte) (message instanceof MessagePlayOutTeams.Create ? 0 : 2));
            final MessagePlayOutTeams.CreateOrUpdate message1 = (MessagePlayOutTeams.CreateOrUpdate) message;
            try (TranslationContext ignored = TranslationContext.enter()
                    .locale(context.getSession().getLocale())
                    .enableForcedTranslations()) {
                buf.writeString(LanternTexts.toLegacy(message1.getDisplayName()));
                buf.writeString(LanternTexts.toLegacy(message1.getPrefix()));
                buf.writeString(LanternTexts.toLegacy(message1.getSuffix()));
            }
            int flags = 0;
            if (message1.getFriendlyFire()) {
                flags |= 0x01;
            }
            if (message1.getSeeFriendlyInvisibles()) {
                flags |= 0x02;
            }
            buf.writeByte((byte) flags);
            buf.writeString(message1.getNameTagVisibility().getKey().toString());
            buf.writeString(message1.getCollisionRule().getName());
            final TextColor c = message1.getColor();
            buf.writeByte((byte) (c == TextColors.NONE || c == TextColors.RESET ? -1 :
                            LanternFormattingCodeTextSerializer.FORMATS_TO_CODE.getChar(c)));
        } else {
            buf.writeByte((byte) (message instanceof MessagePlayOutTeams.Remove ? 1 :
                    message instanceof MessagePlayOutTeams.AddMembers ? 3 : 4));
        }
        if (message instanceof MessagePlayOutTeams.Members) {
            final List<Text> members = ((MessagePlayOutTeams.Members) message).getMembers();
            buf.writeVarInt(members.size());
            try (TranslationContext ignored = TranslationContext.enter()
                    .locale(context.getSession().getLocale())
                    .enableForcedTranslations()) {
                members.forEach(member -> buf.writeString(LanternTexts.toLegacy(member)));
            }
        }
        return buf;
    }
}
