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
package org.lanternpowered.server.network.vanilla.packet.codec.play;

import io.netty.handler.codec.CodecException;
import net.kyori.adventure.text.format.TextColor;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.packet.codec.Codec;
import org.lanternpowered.server.network.packet.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.TeamPacket;
import org.lanternpowered.server.registry.type.scoreboard.CollisionRuleRegistry;
import org.lanternpowered.server.registry.type.scoreboard.VisibilityRegistry;
import org.lanternpowered.server.text.LanternFormattingCodeTextSerializer;
import org.lanternpowered.server.text.LanternTexts;
import org.lanternpowered.server.text.translation.TranslationContext;

import java.util.List;

public final class CodecPlayOutTeams implements Codec<TeamPacket> {

    @Override
    public ByteBuffer encode(CodecContext context, TeamPacket packet) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeString(packet.getTeamName());
        if (packet instanceof TeamPacket.CreateOrUpdate) {
            buf.writeByte((byte) (packet instanceof TeamPacket.Create ? 0 : 2));
            final TeamPacket.CreateOrUpdate message1 = (TeamPacket.CreateOrUpdate) packet;
            context.write(buf, ContextualValueTypes.TEXT, message1.getDisplayName());
            int flags = 0;
            if (message1.friendlyFire) {
                flags |= 0x01;
            }
            if (message1.seeFriendlyInvisibles) {
                flags |= 0x02;
            }
            buf.writeByte((byte) flags);
            buf.writeString(VisibilityRegistry.get().requireId(message1.nameTagVisibility));
            buf.writeString(CollisionRuleRegistry.get().requireId(message1.collisionRule));
            final TextColor c = message1.getColor();
            buf.writeByte((byte) (c == TextColors.NONE.get() || c == TextColors.RESET.get() ? 21 :
                            LanternFormattingCodeTextSerializer.FORMATS_TO_CODE.getChar(c)));
            context.write(buf, ContextualValueTypes.TEXT, message1.getPrefix());
            context.write(buf, ContextualValueTypes.TEXT, message1.getSuffix());
        } else {
            buf.writeByte((byte) (packet instanceof TeamPacket.Remove ? 1 :
                    packet instanceof TeamPacket.AddMembers ? 3 : 4));
        }
        if (packet instanceof TeamPacket.Members) {
            final List<Text> members = ((TeamPacket.Members) packet).members;
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
