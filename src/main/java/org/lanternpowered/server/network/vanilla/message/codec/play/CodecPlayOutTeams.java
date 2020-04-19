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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
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
            context.write(buf, ContextualValueTypes.TEXT, message1.getDisplayName());
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
            buf.writeByte((byte) (c == TextColors.NONE || c == TextColors.RESET ? 21 :
                            LanternFormattingCodeTextSerializer.FORMATS_TO_CODE.getChar(c)));
            context.write(buf, ContextualValueTypes.TEXT, message1.getPrefix());
            context.write(buf, ContextualValueTypes.TEXT, message1.getSuffix());
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
