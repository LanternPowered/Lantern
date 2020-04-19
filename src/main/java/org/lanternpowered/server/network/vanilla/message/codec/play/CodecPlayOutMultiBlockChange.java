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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockChange;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutMultiBlockChange;
import org.spongepowered.math.vector.Vector3i;

import java.util.Collection;

public final class CodecPlayOutMultiBlockChange implements Codec<MessagePlayOutMultiBlockChange> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutMultiBlockChange message) throws CodecException {
        ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeInteger(message.getChunkX());
        buf.writeInteger(message.getChunkZ());
        Collection<MessagePlayOutBlockChange> changes = message.getChanges();
        buf.writeVarInt(changes.size());
        for (MessagePlayOutBlockChange change : changes) {
            Vector3i position = change.getPosition();
            buf.writeByte((byte) ((position.getX() & 0xf) << 4 | position.getZ() & 0xf));
            buf.writeByte((byte) position.getY());
            buf.writeVarInt(change.getBlockState());
        }
        return buf;
    }
}
