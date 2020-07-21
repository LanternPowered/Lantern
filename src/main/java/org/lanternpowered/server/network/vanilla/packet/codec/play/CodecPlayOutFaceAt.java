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
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutFaceAt;
import org.spongepowered.math.vector.Vector3d;

public final class CodecPlayOutFaceAt implements Codec<PacketPlayOutFaceAt> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutFaceAt packet) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeVarInt(packet.getSourceBodyPosition().ordinal());
        final Vector3d pos = packet.getPosition();
        buf.writeDouble(pos.getX());
        buf.writeDouble(pos.getY());
        buf.writeDouble(pos.getZ());
        final boolean flag = packet instanceof PacketPlayOutFaceAt.Entity;
        buf.writeBoolean(flag);
        if (flag) {
            final PacketPlayOutFaceAt.Entity message1 = (PacketPlayOutFaceAt.Entity) packet;
            buf.writeVarInt(message1.getEntityId());
            buf.writeVarInt(message1.getEntityBodyPosition().ordinal());
        }
        return buf;
    }
}
