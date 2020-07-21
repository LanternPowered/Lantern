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

import com.google.common.collect.ImmutableMap;
import io.netty.handler.codec.CodecException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.lanternpowered.server.game.registry.InternalIDRegistries;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutTags;

import java.util.Collections;
import java.util.Map;
import java.util.function.IntConsumer;

public final class CodecPlayOutTags implements Codec<PacketPlayOutTags> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutTags message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        // TODO: Replace this hack, is currently required to
        // TODO: avoid crashes on the client.
        final IntList wallSigns = new IntArrayList();
        final IntList standingSigns = new IntArrayList();
        InternalIDRegistries.BLOCK_TYPE_IDS.object2IntEntrySet().forEach(entry -> {
            if (entry.getKey().endsWith("_wall_sign")) {
                wallSigns.add(entry.getIntValue());
            } else if (entry.getKey().endsWith("_sign")) {
                standingSigns.add(entry.getIntValue());
            }
        });
        final IntList signs = new IntArrayList(wallSigns);
        signs.addAll(standingSigns);
        writeTags(buf, ImmutableMap.<String, IntList>builder()
                .put("minecraft:signs", signs)
                .put("minecraft:wall_signs", wallSigns)
                .put("minecraft:standing_signs", standingSigns)
                .build()); // Block Tags
        writeTags(buf, Collections.emptyMap()); // Item Tags
        writeTags(buf, Collections.emptyMap()); // Fluid Tags
        writeTags(buf, Collections.emptyMap()); // Entity Tags
        return buf;
    }

    private void writeTags(ByteBuffer buf, Map<String, IntList> entries) {
        buf.writeVarInt(entries.size());
        for (Map.Entry<String, IntList> entry : entries.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeVarInt(entry.getValue().size());
            entry.getValue().forEach((IntConsumer) buf::writeVarInt);
        }
    }
}
