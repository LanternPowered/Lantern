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
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.packet.codec.Codec;
import org.lanternpowered.server.network.packet.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutTabComplete;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;

public final class CodecPlayOutTabComplete implements Codec<PacketPlayOutTabComplete> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutTabComplete packet) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeVarInt(packet.getId());
        buf.writeVarInt(packet.getStart());
        buf.writeVarInt(packet.getLength());
        final List<PacketPlayOutTabComplete.Match> matches = packet.getMatches();
        buf.writeVarInt(matches.size());
        for (PacketPlayOutTabComplete.Match match : matches) {
            buf.writeString(match.getValue());
            final Optional<Text> tooltip = match.getTooltip();
            buf.writeBoolean(tooltip.isPresent());
            tooltip.ifPresent(text -> context.write(buf, ContextualValueTypes.TEXT, text));
        }
        return buf;
    }
}
