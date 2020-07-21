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
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutSpawnExperienceOrb;

public final class CodecPlayOutSpawnExperienceOrb implements Codec<PacketPlayOutSpawnExperienceOrb> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutSpawnExperienceOrb message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeVarInt(message.getEntityId());
        buf.writeVector3d(message.getPosition());
        buf.writeShort((short) Math.min(message.getQuantity(), Short.MAX_VALUE));
        return buf;
    }
}
