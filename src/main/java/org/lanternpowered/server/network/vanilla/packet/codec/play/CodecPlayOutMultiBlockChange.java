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
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutBlockChange;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutMultiBlockChange;
import org.spongepowered.math.vector.Vector3i;

import java.util.Collection;

public final class CodecPlayOutMultiBlockChange implements Codec<PacketPlayOutMultiBlockChange> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutMultiBlockChange message) throws CodecException {
        ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeInteger(message.getChunkX());
        buf.writeInteger(message.getChunkZ());
        Collection<PacketPlayOutBlockChange> changes = message.getChanges();
        buf.writeVarInt(changes.size());
        for (PacketPlayOutBlockChange change : changes) {
            Vector3i position = change.getPosition();
            buf.writeByte((byte) ((position.getX() & 0xf) << 4 | position.getZ() & 0xf));
            buf.writeByte((byte) position.getY());
            buf.writeVarInt(change.getBlockState());
        }
        return buf;
    }
}
