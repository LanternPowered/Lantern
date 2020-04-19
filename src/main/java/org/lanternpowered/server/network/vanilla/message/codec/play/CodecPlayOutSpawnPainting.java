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
import org.lanternpowered.server.data.type.LanternArtType;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnPainting;
import org.spongepowered.api.util.Direction;

public final class CodecPlayOutSpawnPainting implements Codec<MessagePlayOutSpawnPainting> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutSpawnPainting message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeVarInt(message.getEntityId());
        buf.writeUniqueId(message.getUniqueId());
        buf.writeVarInt(((LanternArtType) message.getArt()).getInternalId());
        buf.writePosition(message.getX(), message.getY(), message.getZ());
        buf.writeByte(toId(message.getDirection()));
        return buf;
    }

    private static byte toId(Direction direction) {
        switch (direction) {
            case EAST:
                return 3;
            case NORTH:
                return 2;
            case WEST:
                return 1;
            default:
                return 0;
        }
    }
}
