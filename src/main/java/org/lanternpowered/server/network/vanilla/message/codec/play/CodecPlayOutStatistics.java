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
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutStatistics;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutStatistics.Entry;

import java.util.Set;

public final class CodecPlayOutStatistics implements Codec<MessagePlayOutStatistics> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutStatistics message) throws CodecException {
        ByteBuffer buf = context.byteBufAlloc().buffer();
        Set<Entry> entries = message.getEntries();
        buf.writeVarInt(entries.size());
        for (Entry entry : entries) {
            buf.writeString(entry.getName());
            buf.writeVarInt(entry.getValue());
        }
        return buf;
    }
}
