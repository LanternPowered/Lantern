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

import com.google.common.collect.ImmutableMap;
import io.netty.handler.codec.CodecException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.lanternpowered.server.game.registry.InternalIDRegistries;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTags;

import java.util.Collections;
import java.util.Map;
import java.util.function.IntConsumer;

public final class CodecPlayOutTags implements Codec<MessagePlayOutTags> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutTags message) throws CodecException {
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
