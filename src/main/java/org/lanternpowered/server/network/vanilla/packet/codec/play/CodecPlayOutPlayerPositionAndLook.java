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
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutPlayerPositionAndLook;
import org.spongepowered.api.util.RelativePositions;

import java.util.Set;

public final class CodecPlayOutPlayerPositionAndLook implements Codec<PacketPlayOutPlayerPositionAndLook> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutPlayerPositionAndLook packet) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeVector3d(packet.getPosition());
        buf.writeFloat(packet.getYaw());
        buf.writeFloat(packet.getPitch());
        final Set<RelativePositions> relativePositions = packet.getRelativePositions();
        byte flags = 0;
        if (relativePositions.contains(RelativePositions.X)) {
            flags |= 0x01;
        }
        if (relativePositions.contains(RelativePositions.Y)) {
            flags |= 0x02;
        }
        if (relativePositions.contains(RelativePositions.Z)) {
            flags |= 0x04;
        }
        if (relativePositions.contains(RelativePositions.PITCH)) {
            flags |= 0x08;
        }
        if (relativePositions.contains(RelativePositions.YAW)) {
            flags |= 0x10;
        }
        buf.writeByte(flags);
        buf.writeVarInt(packet.getTeleportId());
        return buf;
    }
}
