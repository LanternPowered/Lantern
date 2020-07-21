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
import org.lanternpowered.server.network.packet.codec.Codec;
import org.lanternpowered.server.network.packet.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutEntityVelocity;

public final class CodecPlayOutEntityVelocity implements Codec<PacketPlayOutEntityVelocity> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutEntityVelocity packet) throws CodecException {
        ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeVarInt(packet.getEntityId());
        buf.writeShort((short) Math.min(packet.getX() * 8000.0, Short.MAX_VALUE));
        buf.writeShort((short) Math.min(packet.getY() * 8000.0, Short.MAX_VALUE));
        buf.writeShort((short) Math.min(packet.getZ() * 8000.0, Short.MAX_VALUE));
        return buf;
    }
}
