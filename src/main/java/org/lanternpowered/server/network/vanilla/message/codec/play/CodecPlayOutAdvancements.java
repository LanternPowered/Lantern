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
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.advancement.NetworkAdvancement;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutAdvancements;

import java.util.List;
import java.util.Map;

public final class CodecPlayOutAdvancements implements Codec<MessagePlayOutAdvancements> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutAdvancements message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeBoolean(message.getClear());
        final List<NetworkAdvancement> added = message.getAdded();
        buf.writeVarInt(added.size());
        added.forEach(advancement -> context.write(buf, ContextualValueTypes.ADVANCEMENT, advancement));
        final List<String> removed = message.getRemoved();
        buf.writeVarInt(removed.size());
        removed.forEach(buf::writeString);
        final Map<String, Object2LongMap<String>> progress = message.getProgress();
        buf.writeVarInt(progress.size());
        for (Map.Entry<String, Object2LongMap<String>> entry : progress.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeVarInt(entry.getValue().size());
            for (Object2LongMap.Entry<String> entry1 : entry.getValue().object2LongEntrySet()) {
                buf.writeString(entry1.getKey());
                final long time = entry1.getLongValue();
                buf.writeBoolean(time != -1L);
                if (time != -1L) {
                    buf.writeLong(time);
                }
            }
        }
        return buf;
    }
}
